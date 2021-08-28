package com.example.siiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class consultar_pasajeros extends AppCompatActivity {

    /// NFC ///

    NfcAdapter nfcAdapter;

    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private NfcA mNfc;
    private StringBuilder mStringBuilder;

    private static final byte SELECT_COMMAND = (byte) 0x5A;
    private static final byte AUTHENTICATE_COMMAND = (byte) 0x0A;
    private static final byte READ_DATA_COMMAND = (byte) 0xBD;
    private static final byte[] NATIVE_AUTHENTICATION_COMMAND = new byte[]{(byte) 0x0A, (byte) 0x00};
    private static final byte[] NATIVE_SELECT_COMMAND = new byte[]{(byte) 0x5A, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    /// OBJETOS ///

    Vehiculos transporte = new Vehiculos();
    Conectar sql = new Conectar();
    Colaborador persona = new Colaborador();
    Sesion sesion = new Sesion();
    Rutas sitios = new Rutas();
    cargar_proceso carga = new cargar_proceso(this);

    /// VARIABLES ///

    private EditText codigoC;
    private Button botonConsultar;
    private RequestQueue mQueue;
    private TextView placaVehiculo, rutaViaje, cantidadPasajeros, texto_cerrar_pasajeros;
    private TextView nombrePasajero, cargoPasajero, empresaPasajero;
    private ImageView img_cerrar_pasajeros, img_colaborador, imgPasajero;

    private View vistaImg;
    LayoutInflater img_identidad;

    String urlObservacion = sql.api + "iocolaboradorAPP/";
    String urlPasajeros = sql.api + "registroPasajeroAPP/";
    String urlRegistroExistente = sql.api + "pasajeroRegistradoAPP/";
    String urlRegistroProceso = sql.api + "registrarProcesoAPP/";
    String urlActualizarViaje = sql.api + "actualizarVAPP/";
    String urlImgColaborador = sql.api + "imgColaboradores/";
    String url = sql.api + "colaboradorAPP/";
    String codigoBuscar, origen, destino, no3;
    int cantidadP;

    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);
        obtenerSesion();
        obtenerRuta();

        nfcAdapter = NfcAdapter.getDefaultAdapter(consultar_pasajeros.this);
        ejecutarNfc();

        codigoC = (EditText) findViewById(R.id.cajaCodigo);
        placaVehiculo = (TextView) findViewById(R.id.cajaPlaca);
        rutaViaje = (TextView) findViewById(R.id.cajaRuta);

        img_colaborador = (ImageView)findViewById(R.id.img_colaborador_despacho);
        nombrePasajero = (TextView)findViewById(R.id.caja_nombreP_despacho);
        cargoPasajero = (TextView)findViewById(R.id.caja_cargo_despacho);
        empresaPasajero = (TextView)findViewById(R.id.caja_empresa_despacho);

        cantidadPasajeros = (TextView) findViewById(R.id.texto_cantidad_pasajeros);
        texto_cerrar_pasajeros = (TextView)findViewById(R.id.texto_cerrar_pasajeros);
        img_cerrar_pasajeros = (ImageView)findViewById(R.id.img_cerrar_pasajeros);

        placaVehiculo.setText(transporte.getPlaca());
        rutaViaje.setText(sitios.getOrigen() + "-" + sitios.getDestino());
        cantidadPasajeros.setText(Integer.toString(transporte.getCantidadPasajeros()));

        botonConsultar = (Button) findViewById(R.id.botonConsultar);

        botonConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoBuscar = codigoC.getText().toString();

                if (codigoBuscar.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR DIGITA EL CODIGO", Toast.LENGTH_SHORT).show();

                } else {
                    tecladoOculto();
                    carga.iniciarCarga();
                    consultarColaborador();

                }

            }
        });


        img_cerrar_pasajeros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarBus();

            }
        });

        texto_cerrar_pasajeros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarBus();

            }
        });

    }

    public void consultarColaborador() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", codigoBuscar);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        codigoC.setText("");
                        consultarPasajero();

                    } else {
                        carga.cargaCompleta();
                        colaboradorInexistente();
                        codigoC.setText("");

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void consultar() {
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", codigoBuscar);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlObservacion, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject colaborador = response.getJSONObject("datos");

                        persona.setCodigoC(colaborador.getString("cf"));
                        persona.setCedulaC(colaborador.getString("Doc_colaborador"));
                        persona.setNombreC(colaborador.getString("Nom_colaborador"));
                        persona.setCargoC(colaborador.getString("Cargo_colaborador"));
                        persona.setEmpresa(colaborador.getString("Empresa"));
                        persona.setSubordinacion(colaborador.getString("Nom_subordinacion"));
                        persona.setEstado(colaborador.getString("Estado"));
                        persona.setObservacion(colaborador.getString("Observacion_ing"));
                        persona.setProceso(colaborador.getString("Tipo_ing"));

                        mostrarDatos();

                    } else {
                        carga.cargaCompleta();
                        noProgramado();

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void obtenerImg() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", persona.getCodigoC());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlImgColaborador, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        String resultado = response.getString("datos");

                        byte[] decodedString = Base64.decode(resultado, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        imgPasajero.setImageBitmap(decodedByte);
                        img_colaborador.setImageBitmap(decodedByte);

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS!, NO SE PUDO CERRAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!" +e, Toast.LENGTH_LONG).show();
                    cerrarVentana();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();
                cerrarVentana();

            }

        });

        mQueue.add(peticion);

    }

    public void consultarPasajero() {
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", codigoBuscar);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistroExistente, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        carga.cargaCompleta();
                        yaRegistrado();

                    } else {
                        consultar();

                    }

                } catch (Exception error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void registrarPasajero() {
        cantidadP = transporte.getCantidadPasajeros();

        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put("cf", persona.getCodigoC());
        hashMapToken.put("cedula", persona.getCedulaC());
        hashMapToken.put("placa", transporte.getPlaca());
        hashMapToken.put("origen", sitios.getOrigen());
        hashMapToken.put("destino", sitios.getDestino());
        hashMapToken.put("proceso", persona.getProceso());
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlPasajeros, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    sonarRegistro();
                    transporte.setCantidadPasajeros(cantidadP + 1);

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "REGISTRO CON EXITO!", Toast.LENGTH_SHORT).show();

                        if (persona.getProceso().equals("SALIDA")) {
                            registroActualizado();

                        } else {


                        }

                        if (transporte.getCantidadPasajeros() == transporte.getCapacidadBus()) {
                            transporte.setEstado(0);
                            actualizarViaje();

                            Toast.makeText(getApplicationContext(), "BUS LLENO!", Toast.LENGTH_SHORT).show();
                            cerrarBus();

                        } else {
                            transporte.setEstado(1);
                            actualizarViaje();

                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO REGISTRAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void registroActualizado() {
        HashMap<String, String> hashMapToken = new HashMap<>();
        hashMapToken.put("cf", persona.getCodigoC());
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistroProceso, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "REGISTRO ACTUALIZADO!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO ACTUALIZAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void actualizarViaje() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("placa", transporte.getPlaca());
        hashMapToken.put("cantidadP", Integer.toString(transporte.getCantidadPasajeros()));
        hashMapToken.put("estado", Integer.toString(transporte.getEstado()));

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlActualizarViaje, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        cantidadPasajeros.setText(Integer.toString(transporte.getCantidadPasajeros()));
                        guardarRuta();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO REGISTRAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_SHORT).show();

            }

        });

        mQueue.add(peticion);

    }

    public void mostrarDatos() {
        AlertDialog.Builder permitidos = new AlertDialog.Builder(this);
        permitidos.setTitle(persona.getNombreC());
        vistaImg = getLayoutInflater().inflate(R.layout.img_identidad, null);
        imgPasajero = (ImageView)vistaImg.findViewById(R.id.img_colaborador_identidad);
        permitidos.setView(vistaImg);
        permitidos.setMessage("CODIGO: " +persona.getCodigoC() +"\n"+ persona.getObservacion());
        permitidos.setCancelable(false);

        permitidos.setPositiveButton("SUBIR AL BUS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                registrarPasajero();

            }
        });

        permitidos.setNegativeButton("NO SUBIR AL BUS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if(persona.getObservacion().isEmpty() || persona.getObservacion().equals(null) || persona.getObservacion() == " ") {
            Toast.makeText(getApplicationContext(), "COLABORADOR NO PROGRAMADO.", Toast.LENGTH_SHORT).show();

        } else {
            permitidos.show();
            obtenerImg();

        }

        nombrePasajero.setText(persona.getNombreC());
        cargoPasajero.setText(persona.getCargoC());
        empresaPasajero.setText(persona.getEmpresa());

    }

    public void noProgramado() {
        AlertDialog.Builder noPermitidos = new AlertDialog.Builder(this);
        noPermitidos.setTitle("SEÑOR USUARIO");
        noPermitidos.setMessage("EL COLABORADOR NO HA SIDO PROGRAMADO.");
        noPermitidos.setCancelable(false);

        noPermitidos.setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        noPermitidos.show();

    }

    public void yaRegistrado() {
        AlertDialog.Builder yaRegistrado = new AlertDialog.Builder(this);
        yaRegistrado.setTitle("SEÑOR USUARIO");
        yaRegistrado.setMessage("EL COLABORADOR YA SE ENCUENTRA REGISTRADO.");
        yaRegistrado.setCancelable(false);

        yaRegistrado.setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        yaRegistrado.show();

    }

    public void colaboradorInexistente() {
        AlertDialog.Builder noExiste = new AlertDialog.Builder(this);
        noExiste.setTitle("SEÑOR USUARIO");
        noExiste.setMessage("ESTE COLABORADOR NO EXISTE.");
        noExiste.setCancelable(false);

        noExiste.setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        noExiste.show();

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {
            cerrarSesion();

        }

    }

    public void obtenerRuta() {
        SharedPreferences memoria = getSharedPreferences(transporte.getID_RUTA(), MODE_PRIVATE);
        transporte.setPlaca(memoria.getString(transporte.getPLACA_BUS(), null));
        transporte.setCapacidadBus(Integer.parseInt(memoria.getString(transporte.getCAPACIDAD_M(), null)));
        transporte.setCantidadPasajeros(Integer.parseInt(memoria.getString(transporte.getCANTIDAD_P(), null)));
        sitios.setOrigen(memoria.getString(transporte.getORIGEN_RUTA(), null));
        sitios.setDestino(memoria.getString(transporte.getDESTINO_RUTA(), null));

        if (transporte.getPlaca() == null || transporte.getCapacidadBus() == 0 || sitios.getOrigen() == null || sitios.getDestino() == null) {
            cerrarVentana();

        }

    }

    public void guardarSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(sesion.getID_USUARIO(), sesion.getId());
        establecer.putString(sesion.getID_APP(), sesion.getApp());
        establecer.putString(sesion.getNOM_APP(), sesion.getNomApp());
        establecer.putString(sesion.getCF(), sesion.getCodigo());
        establecer.putString(sesion.getNOM_USER(), sesion.getNombreUsuario());

        establecer.commit();

    }

    public void guardarRuta() {
        SharedPreferences memoria = getSharedPreferences(transporte.getID_RUTA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(transporte.getPLACA_BUS(), transporte.getPlaca());
        establecer.putString(transporte.getCAPACIDAD_M(), Integer.toString(transporte.getCapacidadBus()));
        establecer.putString(transporte.getCANTIDAD_P(), Integer.toString(transporte.getCantidadPasajeros()));
        establecer.putString(transporte.getORIGEN_RUTA(), sitios.getOrigen());
        establecer.putString(transporte.getDESTINO_RUTA(), sitios.getDestino());

        establecer.commit();

    }

    public void cerrarBus() {
        transporte.setEstado(0);
        actualizarViaje();

        Toast.makeText(getApplicationContext(), "BUS DESPACHADO!", Toast.LENGTH_SHORT).show();
        sesion.setNomApp("consultar_vehiculo");
        guardarSesion();
        eliminarRuta();

        Intent intento = new Intent(this, consultar_vehiculo.class);
        startActivity(intento);
        this.finish();

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Home.class);
        startActivity(intento);
        this.finish();

    }

    public void cerrarSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(sesion.getID_USUARIO(), null);
        establecer.putString(sesion.getID_APP(), null);
        establecer.putString(sesion.getNOM_APP(), null);
        establecer.putString(sesion.getCF(), null);
        establecer.putString(sesion.getNOM_USER(), null);

        establecer.commit();

        Toast.makeText(getApplicationContext(), "SESION FINALIZADA", Toast.LENGTH_LONG).show();
        cerrarVentana();

    }

    public void eliminarRuta() {
        SharedPreferences memoria = getSharedPreferences(transporte.getID_RUTA(), MODE_PRIVATE);

        SharedPreferences.Editor establecer = memoria.edit();
        establecer.putString(transporte.getPLACA_BUS(), null);
        establecer.putString(transporte.getCAPACIDAD_M(), null);
        establecer.putString(transporte.getCANTIDAD_P(), null);
        establecer.putString(transporte.getORIGEN_RUTA(), null);
        establecer.putString(transporte.getDESTINO_RUTA(), null);

        establecer.commit();

    }

    public void tecladoOculto() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(codigoC.getWindowToken(), 0);

    }

    public void sonarRegistro() {
        MediaPlayer registrado = MediaPlayer.create(this, R.raw.asegurar_carro);
        registrado.start();

    }

    private void processIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mNfc = NfcA.get(tag);
        try {
            mNfc.connect();
            Log.v("tag", "CONECTADO.");
            byte[] id = mNfc.getTag().getId();
            Log.v("tag", "ID DE TAG: " + id);
            codigoBuscar = getDec(id);

            byte[] response = mNfc.transceive(NATIVE_SELECT_COMMAND);
            displayText("SELECCIONAR APP", getHex(response));
            authenticate();
            String read = readCommand();
            displayText("LEIDO: ", read);

        } catch (IOException e) {
        } finally {
            if (mNfc != null) {
                try {
                    mNfc.close();
                } catch (IOException e) {
                    Log.v("tag", "ERROR DE LECTURA");
                }
            }
        }

        carga.iniciarCarga();
        consultarColaborador();
    }

    private String readCommand() {
        byte fileNo = (byte) 0x01;
        byte[] offset = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] length = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] message = new byte[8];

        message[0] = READ_DATA_COMMAND;
        message[1] = fileNo;

        System.arraycopy(offset, 0, message, 2, 3);
        System.arraycopy(length, 0, message, 2, 3);

        byte[] response;

        try {
            response = mNfc.transceive(message);

            return getHex(response);

        } catch (IOException e) {
            e.printStackTrace();

        }

        return "LECTURA FALLIDA";
    }

    private void authenticate() {
        byte[] rndB = new byte[8];
        byte[] response;

        try {
            response = mNfc.transceive(NATIVE_AUTHENTICATION_COMMAND);
            System.arraycopy(response, 1, rndB, 0, 8);

            byte[] command = new byte[17];

            System.arraycopy(DES.gen_sessionKey(rndB), 0, command, 1, 16);
            command[0] = (byte) 0xAF;

            response = mNfc.transceive(command);
            displayText("AUTENTICACIÓN DE ESTADO: ", getHex(response));

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    private String getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;

        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;

        }

        return result + "";
    }

    private String getHex(byte[] bytes) {
        Log.v("tag", "Getting hex");
        StringBuilder sb = new StringBuilder();

        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    private void displayText(String label, String text) {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        if (label != null) {
            mStringBuilder.append(label);
            mStringBuilder.append(":");
        }
        mStringBuilder.append(text);
        mStringBuilder.append("\n");

        no3 = mStringBuilder.toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        } else {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(consultar_pasajeros.this, pendingIntent,
                    intentFiltersArray, techListsArray);
        } else {
            Toast.makeText(getApplicationContext(), "NFC INACTIVO", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Log.v("tag", "In onNewIntent");
            processIntent(intent);
        }

    }

    public void ejecutarNfc() {
        pendingIntent = PendingIntent.getActivity(consultar_pasajeros.this, 0, new Intent(consultar_pasajeros.this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");

        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException();

        }

        intentFiltersArray = new IntentFilter[]{ndef,};
        techListsArray = new String[][]{new String[]{NfcA.class.getName()}};

    }

}
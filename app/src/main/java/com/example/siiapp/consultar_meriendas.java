package com.example.siiapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class consultar_meriendas extends AppCompatActivity {

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

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    Colaborador persona = new Colaborador();
    cargar_proceso carga = new cargar_proceso(this);

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    private EditText caja_id_colaborador;
    private EditText caja_observacion;

    private Button boton_consultarMerienda;

    private TextView texto_salir_meriendas;
    private ImageView img_salir_meriendas;

    private Spinner listaTipo;

    private View vistaObservacion;
    LayoutInflater alertaObservacion;

    private View vistaImg;
    LayoutInflater img_identidad;

    private ImageView img_colaborador;

    String url = sql.api + "colaboradorAPP/";
    String urlMeriendas = sql.api + "consultarMeriendaAPP/";
    String urlEntregaM = sql.api + "registrarMeriendaAPP/";
    String urlValidarM = sql.api + "validarMeriendaAPP/";
    String urlImgColaborador = sql.api + "imgColaboradores/";
    String codigoBuscar, tipo, observacion = "", no3;

    int alertar = 0;

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultar_meriendas);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        alertaObservacion = getLayoutInflater();

        caja_id_colaborador = (EditText)findViewById(R.id.caja_id_meriendas);
        listaTipo = (Spinner)findViewById(R.id.listaTipo_meriendas);

        listar();

        nfcAdapter = NfcAdapter.getDefaultAdapter(consultar_meriendas.this);
        ejecutarNfc();

        caja_id_colaborador.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    codigoBuscar = caja_id_colaborador.getText().toString();
                    tipo = listaTipo.getSelectedItem().toString();

                    if (tipo.equals("SELECCIONAR")) {
                        Toast.makeText(getApplicationContext(), "POR FAVOR ELIJA EL TIPO DE ENTREGA", Toast.LENGTH_LONG).show();
                        caja_id_colaborador.setText("");
                        caja_id_colaborador.requestFocus();

                    } else {
                        carga.iniciarCarga();
                        consultarColaborador();

                    }

                }
            }
        });

        boton_consultarMerienda = (Button)findViewById(R.id.boton_consulta_meriendas);
        boton_consultarMerienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoBuscar = caja_id_colaborador.getText().toString();
                tipo = listaTipo.getSelectedItem().toString();

                if(codigoBuscar.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE CODIGO O CEDULA", Toast.LENGTH_SHORT).show();

                } else if (tipo.equals("SELECCIONAR")) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR ELIJA EL TIPO DE ENTREGA", Toast.LENGTH_LONG).show();

                } else {
                    carga.iniciarCarga();
                    consultarColaborador();

                }

            }
        });

        texto_salir_meriendas = (TextView)findViewById(R.id.texto_salir_meriendas);
        texto_salir_meriendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

        img_salir_meriendas = (ImageView)findViewById(R.id.img_salir_meriendas);
        img_salir_meriendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

    }

    public void listar() {
        String[] procesos = new String[] {"SELECCIONAR", "DESAYUNO", "ALMUERZO", "CENA", "MERIENDA"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, procesos);
        listaTipo.setAdapter(adaptador);

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
                        JSONObject colaborador = response.getJSONObject("datos");

                        persona.setCodigoC(colaborador.getString("cf"));
                        consultarMerienda();

                    } else {
                        carga.cargaCompleta();
                        noEncontrado();

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

    public void consultarMerienda() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("codigo", persona.getCodigoC());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlMeriendas, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        persona.setCodigoC(datos.getString("cf"));
                        persona.setCedulaC(datos.getString("Id_colaborador"));
                        persona.setNombreC(datos.getString("Nom_colaborador"));
                        persona.setGrupo(datos.getString("Parametro"));
                        persona.setTurno(datos.getString("Turno"));
                        persona.setTurnoBurbuja(datos.getString("Tburbuja"));
                        persona.setJornada(datos.getString("Jornada"));
                        persona.setUbicacion(datos.getString("UBICACION"));

                        validarMerienda();

                    } else {
                        carga.cargaCompleta();
                        noEncontrado();

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void validarMerienda() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("codigo", persona.getCodigoC());
        hashMapToken.put("tipo", tipo);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlValidarM, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        yaMerienda();

                    } else {
                        mostrarDatos();

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void registrarMerienda() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("cf", persona.getCodigoC());
        hashMapToken.put("cedula", persona.getCedulaC());
        hashMapToken.put("grupo", persona.getGrupo());
        hashMapToken.put("turno", persona.getTurno());
        hashMapToken.put("tipo", tipo);
        hashMapToken.put("observacion", observacion);
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlEntregaM, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "REGISTRO CON EXITO!", Toast.LENGTH_SHORT).show();
                        alertar = 0;
                        caja_id_colaborador.setText("");
                        caja_id_colaborador.requestFocus();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO REGISTRAR.", Toast.LENGTH_SHORT).show();
                        caja_id_colaborador.requestFocus();

                    }

                } catch (JSONException error) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void mostrarDatos() {
        String mensaje, turno = persona.getTurnoBurbuja();

        if(!persona.getTurno().equals("noche") && tipo.equals("MERIENDA") || persona.getJornada().equals("SOLO DIA") && tipo.equals("MERIENDA")) {
            mensaje = "COLABORADOR NO TIENE DERECHO A " +tipo+ ".";
            alertar = 1;


        } else if (persona.getTurno().equals("disponible") || persona.getUbicacion().equals("FUERA DE OBRA")) {
            mensaje = "COLABORADOR NO TIENE DERECHO A " +tipo+ ".";
            alertar = 1;


        } else {
            mensaje = "COLABORADOR CON DERECHO A " +tipo+ ".";

        }

        if(persona.getTurnoBurbuja().equals("14*7") && !persona.getJornada().equals("SOLO DIA")) {
            turno = turno +" / "+ persona.getUbicacion() +" / "+ persona.getTurno();

        } else if (!persona.getTurnoBurbuja().equals("14*7")) {
            turno = turno +" / "+ persona.getUbicacion() +" / "+ persona.getJornada();
            persona.setTurno(persona.getJornada());

        } else {
            turno = turno +" / "+ persona.getUbicacion() +" / "+ persona.getJornada();
            persona.setTurno(persona.getJornada());

        }

        AlertDialog.Builder permitidos = new AlertDialog.Builder(this);
        permitidos.setTitle(persona.getNombreC());
        permitidos.setMessage("CODIGO: " +persona.getCodigoC() +"\n"+ "GRUPO: " +persona.getGrupo() +"\n"+ "TURNO: " +turno +"\n\n"+ mensaje);
        vistaImg = getLayoutInflater().inflate(R.layout.img_identidad, null);
        img_colaborador = (ImageView)vistaImg.findViewById(R.id.img_colaborador_identidad);
        permitidos.setView(vistaImg);
        permitidos.setCancelable(false);

        permitidos.setPositiveButton("ENTREGAR " +tipo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(alertar == 1) {
                    observacion();

                } else {
                    carga.iniciarCarga();
                    registrarMerienda();

                }


            }
        });

        permitidos.setNegativeButton("NO ENTREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                caja_id_colaborador.setText("");
                caja_id_colaborador.requestFocus();

            }
        });

        permitidos.show();
        obtenerImg();
    }

    public void observacion() {
        AlertDialog.Builder alertaObservacion = new AlertDialog.Builder(this);
        alertaObservacion.setTitle("¿Alguna observación?");
        vistaObservacion = getLayoutInflater().inflate(R.layout.alerta_observacion, null);
        caja_observacion = (EditText)vistaObservacion.findViewById(R.id.caja_observacion_alerta);
        alertaObservacion.setView(vistaObservacion);
        alertaObservacion.setCancelable(false);

        alertaObservacion.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carga.iniciarCarga();
                observacion = caja_observacion.getText().toString();
                caja_observacion.setText("");
                registrarMerienda();

            }
        });

        alertaObservacion.show();

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

    public void noEncontrado() {
        AlertDialog.Builder noEncontrado = new AlertDialog.Builder(this);
        noEncontrado.setTitle("SEÑOR USUARIO");
        noEncontrado.setMessage("ESTE COLABORADOR NO EXISTE.");
        noEncontrado.setCancelable(false);

        noEncontrado.setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                caja_id_colaborador.setText("");
                caja_id_colaborador.requestFocus();

            }
        });

        noEncontrado.show();

    }

    public void yaMerienda() {
        AlertDialog.Builder yaMerienda = new AlertDialog.Builder(this);
        yaMerienda.setTitle("SEÑOR USUARIO");
        yaMerienda.setMessage("EL COLABORADOR YA TIENE " +tipo+ ".");
        yaMerienda.setCancelable(false);

        yaMerienda.setNegativeButton("VOLVER A ENTREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrarDatos();

            }
        });

        yaMerienda.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                caja_id_colaborador.setText("");
                caja_id_colaborador.requestFocus();

            }
        });

        yaMerienda.show();

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

        }

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

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

        tipo = listaTipo.getSelectedItem().toString();

        if (tipo.equals("SELECCIONAR")) {
            Toast.makeText(getApplicationContext(), "POR FAVOR ELIJA EL TIPO DE ENTREGA", Toast.LENGTH_LONG).show();
            caja_id_colaborador.setText("");
            caja_id_colaborador.requestFocus();

        } else {
            carga.iniciarCarga();
            consultarColaborador();

        }

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
            nfcAdapter.enableForegroundDispatch(consultar_meriendas.this, pendingIntent,
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
        pendingIntent = PendingIntent.getActivity(consultar_meriendas.this, 0, new Intent(consultar_meriendas.this,
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

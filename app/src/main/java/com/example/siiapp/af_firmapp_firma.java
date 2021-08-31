package com.example.siiapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class af_firmapp_firma extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();

    loading carga = new loading(this);

    modelo_almacenes almacen = new modelo_almacenes();
    modelo_prestador_af_firmapp prestador = new modelo_prestador_af_firmapp();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    ArrayList<String> listaArticulos = new ArrayList<>();

    Bitmap imagen;
    String encode;
    byte[] envio;

    private ImageView imgColaborador;

    firmaDigital firmar;
    Button botonFirmar, botonBorrar;
    EditText cajaArticulos;
    TextView cajaCedula, cajaNombre;

    String urlDetalles = sql.api + "consultaDetallesFirmaAfAPP/";
    String url = sql.webServices + "entregaAlmacen.php";
    String urlRegistrarFirma = sql.api + "registrarFirmaInicialAfFirmaAPP/";
    String urlCerrarPrestamo = sql.api + "updateFirmaAfAPP/";
    String urlImgColaborador = sql.api + "imgColaboradores/";
    String urlFirmaInicial = sql.api + "consultaFirmaInicialAfFirmaAPP/";

    /// METODOS ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.af_firmapp_firma);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        prestador.setConsecutivo(Integer.parseInt(getIntent().getStringExtra("conse")));
        prestador.setCf(getIntent().getStringExtra("cf"));
        prestador.setCedula(prestador.getCf());
        prestador.setNombre(getIntent().getStringExtra("nombreC"));

        firmar = (firmaDigital)findViewById(R.id.firmaDigital);

        imgColaborador = (ImageView)findViewById(R.id.img_colaborador_af_firma);

        cajaCedula = (TextView)findViewById(R.id.caja_cedula_af_firmapp);
        cajaNombre = (TextView)findViewById(R.id.caja_nombre_af_firmapp);
        cajaArticulos = (EditText)findViewById(R.id.caja_articulos_af_firmapp);

        obtenerImg();

        /// BOTON FIRMAR ///

        botonFirmar = (Button) findViewById(R.id.botonListo);
        botonFirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarFirmaInicial();

            }
        });

        /// BOTON BORRAR ///

        botonBorrar = (Button) findViewById(R.id.botonBorrar);
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmar.borrarFirma();

            }
        });

        carga.iniciarCarga();
        obtenerDetalles();

    }

    public void obtenerImg() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("id", prestador.getCf());

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

                        imgColaborador.setImageBitmap(decodedByte);

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

    public void obtenerDetalles() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", String.valueOf(prestador.getConsecutivo()));

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlDetalles, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);

                            listaArticulos.add(datos.getString("NomActivo"));

                        }

                        mostrarDetalles();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO TIENE DETALLES DEL PRESTAMO.", Toast.LENGTH_SHORT).show();
                        cerrarVentana();

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

    public void mostrarDetalles() {
        cajaCedula.setText(prestador.getCedula());
        cajaNombre.setText(prestador.getNombre());
        cajaArticulos.setText(listaArticulos.toString());

    }

    public void confirmacion() {
        AlertDialog.Builder ventanita = new AlertDialog.Builder(this);

        ventanita.setTitle("FIRMA DE RESPONSABILIDAD");
        ventanita.setMessage("Declaro haber recibido del Consorcio CCC Ituango y en virtud de la relación de empleado que con ella mantengo, los activos relacionados en este formato responsabilizandome por su correcto uso y preservación exceptuando el desgaste del tiempo y su utilización normal, en caso de perdida o daño autorizo a este consorcio a descontar de mi salario la cantidad equivalente a su valor de reposición.");

        ventanita.setPositiveButton("ACEPTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carga.iniciarCarga();
                firmar.setDrawingCacheEnabled(false);
                subirFirma();

                dialog.dismiss();
            }

        });

        ventanita.setNegativeButton("NO ACEPTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }

        });

        ventanita.show();

    }

    public void validarFirmaInicial() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", prestador.getCf());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlFirmaInicial, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        confirmacion();

                    } else {
                        confirmacionRegistrarFirma();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!", Toast.LENGTH_LONG).show();

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

    public void confirmacionRegistrarFirma() {
        AlertDialog.Builder ventanita = new AlertDialog.Builder(this);

        ventanita.setTitle("SEÑOR COLABORADOR");
        ventanita.setMessage("UNA VEZ USTED PULSE ACEPTAR SU FIRMA SERÁ TOTALMENTE LEGAL ANTE CUALQUIER PROCESO QUE INVOLUCRE AL CONSORCIO CCC");

        ventanita.setPositiveButton("ACEPTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carga.iniciarCarga();
                firmar.setDrawingCacheEnabled(false);
                imagenFirmada();
                registrarFirma();

                dialog.dismiss();
            }

        });

        ventanita.setNegativeButton("NO ACEPTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }

        });

        ventanita.show();

    }

    public void registrarFirma() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("codigo", prestador.getCf());
        hashMapToken.put("firma", encode);
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistrarFirma, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        confirmacion();

                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR AL GUARDAR LA FIRMA.", Toast.LENGTH_SHORT).show();

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

    public void subirFirma() {
        StringRequest peticion = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                firmar();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();
                carga.cargaCompleta();

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();

                String datosFirma = imagenFirmada();
                parametros.put("image", datosFirma);
                parametros.put("nombreImg", prestador.getCf());
                parametros.put("consecutivo", String.valueOf(prestador.getConsecutivo()));

                return parametros;
            }

        };

        peticion.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(peticion);

    }

    public void firmar() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("consecutivo", String.valueOf(prestador.getConsecutivo()));
        hashMapToken.put("codigo", prestador.getCf());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlCerrarPrestamo, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "FIRMA CON EXITO.", Toast.LENGTH_SHORT).show();
                        firmar.borrarFirma();
                        confirmar();

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

    public String imagenFirmada() {
        firmar.setDrawingCacheEnabled(true);
        imagen = firmar.getDrawingCache();

        /// CONVERSION DE BITMAP A BASE64 ///
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap tamaño = Bitmap.createScaledBitmap(imagen, 350, 200, true);
        tamaño.compress(Bitmap.CompressFormat.PNG, 100, stream);
        envio = stream.toByteArray();

        encode = Base64.encodeToString(envio, Base64.DEFAULT);

        prestador.setFirmaA(tamaño);

        return encode;
    }

    public void confirmar() {
        Intent intento = new Intent(this, af_firmapp_confirmar_firma.class);
        startActivity(intento);
        this.finish();

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Home_af_firmapp.class);
        startActivity(intento);
        this.finish();

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));
        sesion.setRol(memoria.getString(sesion.getROL(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {

        }

    }

}

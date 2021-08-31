package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home_af_firmapp extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();

    loading carga = new loading(this);
    modelo_almacenes almacen = new modelo_almacenes();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    Thread actualizar;
    public static final long Tiempo = 1000;
    private Handler handler = new Handler();
    private Runnable runnable;

    private RecyclerView listaPendientes;
    private adaptador_af_firmas_pendientes adaptadorFirmas;

    List<modelo_af_firmapp_pendientes> pendientes = new ArrayList<>();
    LinearLayout areaAlertA;

    ArrayList<Bitmap> imgColaborador = new ArrayList<Bitmap>();

    String urlAlmacen = sql.api + "consultaAlmacenUsuarioAPP/";
    String urlFirmasPendientes = sql.api + "consultaFirmasPendientesAfAPP/";
    String urlImgColaborador = sql.api + "imgColaboradores/";

    /// METODOS ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.af_firmapp);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        areaAlertA = (LinearLayout)findViewById(R.id.area_alerta_af_firmapp);

        listaPendientes = (RecyclerView)findViewById(R.id.listadoPendientes_af_firmapp);
        listaPendientes.setLayoutManager(new LinearLayoutManager(this));

        carga.iniciarCarga();
        obtenerAlmacen();

        actualizar = new Thread() {
            @Override
            public void run() {
                while(!isFinishing()) {
                    try {
                        Thread.sleep(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pendientes.clear();
                                obtenerFirmasPendientes();

                            }
                        });

                    } catch (Exception error) {
                        error.printStackTrace();

                    }

                }

            }
        };

        actualizar.start();

    }

    public void obtenerAlmacen() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", sesion.getCodigo());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlAlmacen, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        almacen.setId(datos.getString("Id_Almacen"));
                        almacen.setNombre(datos.getString("Nom_Almacen"));

                        carga.iniciarCarga();
                        obtenerFirmasPendientes();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO TIENE ALMACEN ASIGNADO.", Toast.LENGTH_SHORT).show();
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

    public void obtenerFirmasPendientes() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("almacen", almacen.getNombre());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlFirmasPendientes, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    areaAlertA.setVisibility(View.GONE);
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONArray jArray = response.getJSONArray("datos");

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject datos = jArray.getJSONObject(i);
                            String resultado = response.getString("foto");

                            byte[] decodedString = Base64.decode(resultado, Base64.DEFAULT);
                            InputStream entrada = new ByteArrayInputStream(decodedString);
                            Bitmap img = BitmapFactory.decodeStream(entrada);

                            pendientes.add(new modelo_af_firmapp_pendientes(datos.getString("Nom_colaborador"), "CODIGO: " + datos.getString("Id_colaborador"), "CONSECUTIVO: " + datos.getString("id_prestamo"), img, "EN " + datos.getString("Nom_Almacen")));

                        }

                        adaptarPendientes();

                    } else {
                        areaAlertA.setVisibility(View.VISIBLE);

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!" +e, Toast.LENGTH_LONG).show();
                    cerrarVentana();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_LONG).show();
                cerrarVentana();

            }

        });

        mQueue.add(peticion);

    }

    public void adaptarPendientes() {
        adaptadorFirmas = new adaptador_af_firmas_pendientes(this, pendientes);
        listaPendientes.setAdapter(adaptadorFirmas);

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
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

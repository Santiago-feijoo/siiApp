package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class tp_registro_rutas extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();
    modelo_vehiculos transporte = new modelo_vehiculos();
    modelo_rutas sitios = new modelo_rutas();

    /// VARIABLES ///

    private Spinner origen, destino;
    private Button botonConsultarRutas;
    private RequestQueue mQueue;
    private TextView texto_salir_rutas;
    private ImageView img_salir_rutas;
    ArrayList<String> listaRutas;

    String origenRuta, destinoRuta;
    String urlRutas = sql.api + "rutasbusesAPP/";
    String urlRegistrarViaje = sql.api + "registraViajeBAPP/";

    /// METODOS ///


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_rutas);

        mQueue = Volley.newRequestQueue(this);
        obtenerSesion();

        transporte.setPlaca(getIntent().getStringExtra("placa"));
        transporte.setCapacidadBus(Integer.parseInt(getIntent().getStringExtra("capacidadB")));
        transporte.setCantidadPasajeros(Integer.parseInt(getIntent().getStringExtra("cantidadP")));

        origen = (Spinner)findViewById(R.id.origenSpinner);
        destino = (Spinner)findViewById(R.id.destinoSpinner);
        botonConsultarRutas = (Button)findViewById(R.id.boton_consultar_rutas);
        img_salir_rutas = (ImageView)findViewById(R.id.img_salir_rutas);
        texto_salir_rutas = (TextView)findViewById(R.id.texto_salir_rutas);

        obtenerLista();
        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaRutas);
        origen.setAdapter(adaptador);
        destino.setAdapter(adaptador);

        botonConsultarRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                origenRuta = origen.getSelectedItem().toString();
                destinoRuta = destino.getSelectedItem().toString();

                if (origenRuta.equals("SELECCIONA UNA RUTA")) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR SELECCIONA EL ORIGEN",Toast.LENGTH_SHORT).show();

                } else if (destinoRuta.equals("SELECCIONA UNA RUTA")) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR SELECCIONA EL DESTINO",Toast.LENGTH_SHORT).show();

                } else {
                    sitios.setOrigen(origenRuta);
                    sitios.setDestino(destinoRuta);
                    registrarViaje();

                }

            }
        });

        img_salir_rutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar();

            }
        });

        texto_salir_rutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar();

            }
        });

    }

    public void obtenerLista() {
        listaRutas = new ArrayList<String>();
        listaRutas.add("SELECCIONA UNA RUTA");

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, urlRutas, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jArray = response.getJSONArray("datos");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject datos = jArray.getJSONObject(i);

                        listaRutas.add(datos.getString("ruta"));

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "ERROR: " +e, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

    }

    public void registrarViaje() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("placa", transporte.getPlaca());
        hashMapToken.put("capacidadM", Integer.toString(transporte.getCapacidadBus()));
        hashMapToken.put("cantidadP", Integer.toString(transporte.getCantidadPasajeros()));
        hashMapToken.put("origen", sitios.getOrigen());
        hashMapToken.put("destino", sitios.getDestino());
        hashMapToken.put("estado", "1");
        hashMapToken.put("usuario", sesion.getId());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlRegistrarViaje, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO!", Toast.LENGTH_LONG).show();
                        sesion.setNomApp("tp_registro_pasajeros");

                        guardarSesion();
                        guardarRuta();
                        abrirBus();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS! NO SE PUDO REGISTRAR.", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException error) {
                    Toast.makeText(getApplicationContext(), "ERROR: " +error, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();

            }

        });

        mQueue.add(peticion);

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

    public void abrirBus() {
        Intent intento = new Intent(this, tp_registro_pasajeros.class);
        startActivity(intento);

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

    public void cerrarVentana() {
        Intent intento = new Intent(this, Home.class);
        startActivity(intento);
        this.finish();

    }

    public void cerrar() {
        Intent intento = new Intent(this, consultar_vehiculo.class);
        startActivity(intento);
        onBackPressed();

    }

}

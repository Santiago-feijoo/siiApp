package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;

public class consultar_vehiculo extends AppCompatActivity {

    /// OBJETOS ///

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    Vehiculos transporte = new Vehiculos();
    Rutas sitios = new Rutas();

    /// VARIABLES ///

    private EditText cajaPlaca;
    private Button botonConsultarBus;
    private RequestQueue mQueue;
    private TextView texto_salir_vehiculo;
    private ImageView img_salir_vehiculo;

    String placaUsuario;
    String urlVehiculos = sql.api + "consultabusAPP/";
    String urlCantidadPasajeros = sql.api + "viajeActivoAPP/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultar_vehiculo);

        mQueue = Volley.newRequestQueue(this);
        obtenerSesion();

        cajaPlaca = (EditText)findViewById(R.id.cajaPlaca_buses);
        botonConsultarBus = (Button)findViewById(R.id.botonConsultarBus);
        texto_salir_vehiculo = (TextView)findViewById(R.id.texto_salir_vehiculo);
        img_salir_vehiculo = (ImageView)findViewById(R.id.img_salir_vehiculo);

        botonConsultarBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placaUsuario = cajaPlaca.getText().toString();

                if (placaUsuario.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR DIGITAR LA PLACA", Toast.LENGTH_SHORT).show();

                } else {
                    consultarVehiculo();

                }

            }

        });

        texto_salir_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

        img_salir_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

    }

    public void consultarVehiculo() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("placa", placaUsuario);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlVehiculos, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONObject vehiculo = response.getJSONObject("datos");

                        transporte.setTipo(vehiculo.getString("Lineav"));
                        transporte.setPlaca(vehiculo.getString("Placav"));
                        transporte.setCapacidadBus(Integer.parseInt(vehiculo.getString("Capacidadv")));
                        transporte.setCantidadPasajeros(0);

                        disponibilidadViaje();

                    } else {
                        Toast.makeText(getApplicationContext(), "VEHICULO INEXISTENTE!", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException error) {
                    //Toast.makeText(getApplicationContext(), "ERROR: C " +e, Toast.LENGTH_LONG).show();
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

    public void disponibilidadViaje() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("placa", transporte.getPlaca());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlCantidadPasajeros, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                        JSONObject viaje = response.getJSONObject("datos");

                        transporte.setCantidadPasajeros(Integer.parseInt(viaje.getString("Cantidad_Pasajeros")));
                        Toast.makeText(getApplicationContext(), "BUS EN DESPACHO!", Toast.LENGTH_SHORT).show();

                        sesion.setNomApp("consultar_pasajeros");
                        sitios.setOrigen(viaje.getString("Origen"));
                        sitios.setDestino(viaje.getString("Destino"));

                        guardarSesion();
                        guardarRuta();
                        abrirBus();

                    } else {
                        Toast.makeText(getApplicationContext(), "BUS DISPONIBLE!", Toast.LENGTH_SHORT).show();
                        registrarRuta();

                    }

                } catch (JSONException | ClassNotFoundException error) {
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

    public void registrarRuta() {
        Intent intento = new Intent(this, registrar_rutas.class);
        intento.putExtra("placa", transporte.getPlaca());
        intento.putExtra("capacidadB", Integer.toString(transporte.getCapacidadBus()));
        intento.putExtra("cantidadP", Integer.toString(transporte.getCantidadPasajeros()));
        startActivity(intento);

    }

    public void abrirBus() throws ClassNotFoundException {
        String link = "com.example.siiapp."+sesion.getNomApp();

        Intent intento = new Intent(this, Class.forName(link));
        startActivity(intento);

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setId(memoria.getString(sesion.getID_USUARIO(), null));
        sesion.setApp(memoria.getString(sesion.getID_APP(), null));
        sesion.setNomApp(memoria.getString(sesion.getNOM_APP(), null));
        sesion.setCodigo(memoria.getString(sesion.getCF(), null));
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));

        if (sesion.getId() == null || sesion.getNomApp() == null || sesion.getApp() == null || sesion.getCodigo() == null) {
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

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

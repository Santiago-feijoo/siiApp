package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class consultar_viajes_colaborador extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();
    modelo_colaborador persona = new modelo_colaborador();
    loading carga = new loading(this);

    /// ATRIBUTOS ///

    private RecyclerView listaViajes;
    private adaptador_tp_viajes_colaborador adaptador;

    List<modelo_viajes_colaborador> listaV = new ArrayList<>();

    private RequestQueue mQueue;

    private TextView cajaCodigo, cajaCedula, cajaNombre;
    private Button botonVolver;

    String urlViajes = sql.api + "consultarViajesAPP/";

    /// METODOS ///


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tp_consulta_colaboradores_viajes);

        persona.setCodigoC(getIntent().getStringExtra("buscar"));

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        cajaCodigo = (TextView) findViewById(R.id.caja_codigo_colaboradores_viajes);
        cajaCedula = (TextView) findViewById(R.id.caja_cedula_colaboradores_viajes);
        cajaNombre = (TextView) findViewById(R.id.caja_nombre_colaboradores_viajes);

        listaViajes = (RecyclerView)findViewById(R.id.listaViajes_colaboradores_viajes);
        listaViajes.setLayoutManager(new LinearLayoutManager(this));

        carga.iniciarCarga();
        obtenerLista();

        botonVolver = (Button)findViewById(R.id.boton_consultar_viajes);
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

    }

    public void obtenerLista() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("codigo", persona.getCodigoC());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlViajes, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    carga.cargaCompleta();
                    String valor = response.getString("ok");

                    if (valor.equals("true")) {
                    JSONArray jArray = response.getJSONArray("datos");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject datos = jArray.getJSONObject(i);

                        listaV.add(new modelo_viajes_colaborador(datos.getString("Proceso"), datos.getString("PlacaV"), datos.getString("Origen") +" - "+ datos.getString("Destino"), datos.getString("Nom_Usuario"), datos.getString("FechaHoraR")));
                        persona.setCedulaC(datos.getString("Doc_colaborador"));
                        persona.setNombreC(datos.getString("Nom_colaborador"));

                    }

                    adaptar();

                    } else {
                        Toast.makeText(getApplicationContext(), "NO HAY VIAJES REGISTRADOS!", Toast.LENGTH_LONG).show();

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

    public void adaptar() {
        adaptador = new adaptador_tp_viajes_colaborador(consultar_viajes_colaborador.this, listaV);
        listaViajes.setAdapter(adaptador);

        cajaCodigo.setText(persona.getCodigoC());
        cajaCedula.setText(persona.getCedulaC());
        cajaNombre.setText(persona.getNombreC());

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
        Intent intento = new Intent(this, Home_Datos.class);
        startActivity(intento);
        this.finish();

    }

}

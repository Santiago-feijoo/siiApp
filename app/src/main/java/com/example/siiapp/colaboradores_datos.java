package com.example.siiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class colaboradores_datos extends AppCompatActivity {

    /// OBJETOS ///
    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    Colaborador persona = new Colaborador();
    cargar_proceso carga = new cargar_proceso(this);

    /// VARIABLES ///
    private EditText codigoC, cedulaC, nombreC, cargoC, suborC, empresaC;
    private Button botonConsultar;
    private RequestQueue mQueue;

    private String codigoBuscar;
    private String url = sql.api + "colaboradorAPP/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colaboradores_datos);

        mQueue = Volley.newRequestQueue(this);

        sesion.setId(getIntent().getStringExtra("usuario"));
        sesion.setApp(getIntent().getStringExtra("idApp"));
        codigoBuscar = getIntent().getStringExtra("buscar");

        carga.iniciarCarga();
        consultarColaborador();

        codigoC = (EditText)findViewById(R.id.caja_codigo_datos);
        cedulaC = (EditText)findViewById(R.id.caja_cedula_datos);
        nombreC = (EditText)findViewById(R.id.caja_nombre_datos);
        cargoC = (EditText)findViewById(R.id.caja_cargo_datos);
        suborC = (EditText)findViewById(R.id.caja_subor_datos);
        empresaC = (EditText)findViewById(R.id.caja_empresa_datos);
        botonConsultar = (Button)findViewById(R.id.boton_consultar_datos);

        botonConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirConsulta();

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
                    JSONObject datos = response.getJSONObject("datos");
                    carga.cargaCompleta();

                    persona.setCodigoC(datos.getString("cf"));
                    persona.setCedulaC(datos.getString("Doc_colaborador"));
                    persona.setNombreC(datos.getString("Nom_colaborador"));
                    persona.setCargoC(datos.getString("Cargo_colaborador"));
                    persona.setSubordinacion(datos.getString("Nom_subordinacion"));
                    persona.setEmpresa(datos.getString("Empresa"));
                    persona.setEstado(datos.getString("Estado"));

                    llenarDatos();

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "COLABORADOR INEXISTENTE!", Toast.LENGTH_SHORT).show();
                    abrirConsulta();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_LONG).show();
                abrirConsulta();

            }

        });

        mQueue.add(peticion);

    }

    public void llenarDatos() {
        codigoC.setText(persona.getCodigoC());
        cedulaC.setText(persona.getCedulaC());
        nombreC.setText(persona.getNombreC());
        cargoC.setText(persona.getCargoC());
        suborC.setText(persona.getSubordinacion());
        empresaC.setText(persona.getEmpresa());

    }

    public void abrirConsulta() {
        Intent intento = new Intent(this, Home_Datos.class);
        intento.putExtra("usuario", sesion.getId());
        intento.putExtra("idApp", sesion.getApp());
        startActivity(intento);
        onBackPressed();

    }

    @Override protected void onStart() {
        super.onStart();
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onRestart() {
        super.onRestart();
    }

    @Override protected void onDestroy() {
        super.onDestroy();

    }

}

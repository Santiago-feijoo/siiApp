package com.example.siiapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    conexion sql = new conexion();
    modelo_sesion sesion = new modelo_sesion();
    modelo_colaborador persona = new modelo_colaborador();
    loading carga = new loading(this);

    /// VARIABLES ///
    private ImageView img_colaborador;
    private EditText codigoC, cedulaC, nombreC, cargoC, suborC, empresaC;
    private Button botonConsultar;
    private RequestQueue mQueue;

    private String codigoBuscar;
    private String url = sql.api + "colaboradorAPP/";
    private String urlImgColaborador = sql.api + "imgColaboradores/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colaboradores_datos);

        mQueue = Volley.newRequestQueue(this);

        sesion.setId(getIntent().getStringExtra("usuario"));
        sesion.setApp(getIntent().getStringExtra("idApp"));
        codigoBuscar = getIntent().getStringExtra("buscar");

        img_colaborador = (ImageView)findViewById(R.id.img_datos);

        codigoC = (EditText)findViewById(R.id.caja_codigo_datos);
        cedulaC = (EditText)findViewById(R.id.caja_cedula_datos);
        nombreC = (EditText)findViewById(R.id.caja_nombre_datos);
        cargoC = (EditText)findViewById(R.id.caja_cargo_datos);
        suborC = (EditText)findViewById(R.id.caja_subor_datos);
        empresaC = (EditText)findViewById(R.id.caja_empresa_datos);
        botonConsultar = (Button)findViewById(R.id.boton_consultar_datos);

        carga.iniciarCarga();
        consultarColaborador();

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

                    carga.iniciarCarga();
                    obtenerImg();

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
                        llenarDatos();

                    } else {
                        Toast.makeText(getApplicationContext(), "UPS!, NO SE PUDO CERRAR.", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN!" +e, Toast.LENGTH_LONG).show();
                    abrirConsulta();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();
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

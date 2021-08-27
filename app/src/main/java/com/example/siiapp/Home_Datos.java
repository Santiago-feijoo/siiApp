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

public class Home_Datos extends AppCompatActivity {

    /// OBJETOS ///

    Conectar sql = new Conectar();
    Sesion sesion = new Sesion();
    Colaborador persona = new Colaborador();
    cargar_proceso carga = new cargar_proceso(this);

    /// VARIABLES ///
    private TextView salir;
    private EditText cajaId;
    private Button botonConsultar;
    private RequestQueue mQueue;
    private ImageView imgSalir;

    private String codigoBuscar;
    private String url = sql.api + "colaboradorAPP/";

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_colaboradores);

        mQueue = Volley.newRequestQueue(this);
        obtenerSesion();

        cajaId = (EditText) findViewById(R.id.caja_id_colaboradores);
        botonConsultar = (Button) findViewById(R.id.boton_consultar_colaboradores);

        botonConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoBuscar = cajaId.getText().toString();

                if(codigoBuscar.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "POR FAVOR DIGITE CODIGO O CEDULA!", Toast.LENGTH_LONG).show();

                } else {
                    carga.iniciarCarga();
                    consultarColaborador();

                }

            }
        });

        salir = (TextView)findViewById(R.id.texto_salir_colaboradores);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

            }
        });

        imgSalir = (ImageView)findViewById(R.id.img_salir_colaboradores);
        imgSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();

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
                    abrirDatos();

                } catch (JSONException e) {
                    carga.cargaCompleta();
                    Toast.makeText(getApplicationContext(), "COLABORADOR INEXISTENTE!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carga.cargaCompleta();
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN: " + error, Toast.LENGTH_LONG).show();

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

        }

    }

    public void abrirDatos() {
        Intent intento;

        if (sesion.getApp().equals("14")) {
            intento = new Intent(this, consultar_viajes_colaborador.class);

        } else if (sesion.getApp().equals("30")) {
            intento = new Intent(this, colaboradores_datos.class);

        } else {
            intento = new Intent(this, Home_Parte_Diaria.class);

        }

        intento.putExtra("buscar", codigoBuscar);
        startActivity(intento);

    }

    public void cerrarVentana() {
        Intent intento = new Intent(this, Menu.class);
        startActivity(intento);
        this.finish();

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

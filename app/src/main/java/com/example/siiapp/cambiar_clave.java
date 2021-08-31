package com.example.siiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class cambiar_clave extends AppCompatActivity {

    /// OBJETOS ///
    conexion sql = new conexion();

    /// ATRIBUTOS ///
    EditText cajaClaveNueva, cajaConfirmacion;
    TextView claveObligatoria, claveObligatoria2;
    Button botonCambiar;
    private RequestQueue mQueue;

    String usuario, idAplicativo, clave;
    String urlClave = sql.api + "passmovilesAPP/";

    /// METODOS ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambio_clave);

        mQueue = Volley.newRequestQueue(this);

        usuario = getIntent().getStringExtra("usuario");
        idAplicativo = getIntent().getStringExtra("idApp");

        cajaClaveNueva = (EditText)findViewById(R.id.cajaClaveNueva);
        cajaConfirmacion = (EditText)findViewById(R.id.cajaConfirmacion);

        claveObligatoria = (TextView)findViewById(R.id.claveObligatoria);
        claveObligatoria2 = (TextView)findViewById(R.id.claveObligatoria2);

        botonCambiar = (Button)findViewById(R.id.botonCambiar);
        botonCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clave = cajaClaveNueva.getText().toString();

                if (clave.isEmpty()) {
                    claveObligatoria.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "¡POR FAVOR DIGITA LA NUEVA CLAVE!", Toast.LENGTH_LONG).show();

                } else if(cajaConfirmacion.getText().toString().equals(clave)) {
                    cambiarClave();

                } else {
                    claveObligatoria2.setVisibility(View.VISIBLE);
                    claveObligatoria.setVisibility(View.INVISIBLE);

                }

            }
        });

    }

    public void cambiarClave() {

        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", usuario);
        hashMapToken.put("clave", clave);
        hashMapToken.put("app", idAplicativo);

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST,urlClave, new JSONObject(hashMapToken), new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject datos) {
                Toast.makeText(getApplicationContext(), "¡CONTRASEÑA CAMBIADA CON EXITO!", Toast.LENGTH_SHORT).show();
                Salir();

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR DE CONEXIÓN: " +error, Toast.LENGTH_LONG).show();
            }
        });

        mQueue.add(peticion);

    }

    public void Salir() {
        Intent intento = new Intent(cambiar_clave.this, Home.class);
        startActivity(intento);
        onBackPressed();

    }

}

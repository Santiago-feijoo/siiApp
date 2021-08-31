package com.example.siiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class af_firmapp_confirmar_firma extends AppCompatActivity {

    /// OBJETOS ///

    conexion sql = new conexion();
    loading carga = new loading(this);

    modelo_sesion sesion = new modelo_sesion();
    modelo_prestador_af_firmapp prestador = new modelo_prestador_af_firmapp();

    /// ATRIBUTOS ///

    private RequestQueue mQueue;

    private ImageView imgFirmaI;
    private ImageView imgFirmaActual;

    private Button botonConfirmar;

    String urlFirmaInicial = sql.api + "consultaFirmaInicialAfFirmaAPP/";

    /// METODOS ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.af_comparar_firma_firmapp);

        obtenerSesion();
        mQueue = Volley.newRequestQueue(this);

        imgFirmaI = (ImageView)findViewById(R.id.img_firma_inicial_firmapp);
        imgFirmaActual = (ImageView)findViewById(R.id.img_firma_actual_firmapp);

        imgFirmaActual.setImageBitmap(prestador.getFirmaA());

        botonConfirmar = (Button)findViewById(R.id.boton_confirmar_firmas_firmapp);
        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirPendientes();

            }
        });

        carga.iniciarCarga();
        obtenerFirmaInicial();

    }

    public void obtenerFirmaInicial() {
        HashMap<String,String> hashMapToken = new HashMap<>();
        hashMapToken.put("usuario", prestador.getCf());

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, urlFirmaInicial, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.getString("ok");
                    carga.cargaCompleta();

                    if (valor.equals("true")) {
                        JSONObject datos = response.getJSONObject("datos");

                        prestador.setFirmaI(datos.getString("firma"));
                        byte[] decodedString = Base64.decode(prestador.getFirmaI(), Base64.DEFAULT);
                        InputStream entrada = new ByteArrayInputStream(decodedString);
                        Bitmap img = BitmapFactory.decodeStream(entrada);
                        imgFirmaI.setImageBitmap(img);

                    } else {
                        Toast.makeText(getApplicationContext(), "NO HAY FIRMAS REGISTRADAS.", Toast.LENGTH_SHORT).show();

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

    public void abrirPendientes() {
        Intent intento = new Intent(this, Home_af_firmapp.class);
        startActivity(intento);
        this.finish();

    }

}

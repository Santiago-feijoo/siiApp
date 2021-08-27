package com.example.siiapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splash_sii extends AppCompatActivity {

    /// OBJETOS ///

    Sesion sesion = new Sesion();

    /// VARIABLES ///

    Context c;
    String version;
    TextView textoVersion;
    TextView texto_Bienvenido;

    /// METODOS ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_sii);

        obtenerSesion();

        texto_Bienvenido = (TextView)findViewById(R.id.texto_bienvenido_splash);
        textoVersion = (TextView)findViewById(R.id.texto_version_splash);

        if (sesion.getNombreUsuario() == null) {


        } else {
            String[] nombre = sesion.getNombreUsuario().split(" ");
            texto_Bienvenido.setText("¡HOLA " +nombre[2]+ "!");

        }

        textoVersion.setText("VERSIÓN " +BuildConfig.VERSION_NAME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intento = new Intent(splash_sii.this, Home.class);
                startActivity(intento);
                finish();

            }
        },2000);

    }

    public void obtenerSesion() {
        SharedPreferences memoria = getSharedPreferences(sesion.getID_MEMORIA(), MODE_PRIVATE);
        sesion.setNombreUsuario(memoria.getString(sesion.getNOM_USER(), null));

    }

}

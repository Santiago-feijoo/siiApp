package com.example.siiapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Red {

    /// ATRIBUTOS ///

    private ConnectivityManager cm;
    private NetworkInfo ni;
    private boolean conexion = false;

    private Context contexto;

    /// METODOS ///

    Red(Context contexto) {
        this.contexto = contexto;

    }

    public Boolean red() {
        cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();

        if (ni != null) {
            ConnectivityManager conectividad = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo internet = conectividad.getActiveNetworkInfo();

            if (internet != null && internet.isConnected()) {
                conexion = true;

            }

        } else {


        }

        return conexion;
    }

}

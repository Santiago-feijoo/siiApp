package com.example.siiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class loading {

    /// VARIABLES ///

    private Activity miActividad;
    private AlertDialog carga;

    /// METODOS ///

    loading(Activity miActividad) {
        this.miActividad = miActividad;

    }

    void iniciarCarga() {
        AlertDialog.Builder ventana = new AlertDialog.Builder(miActividad);
        LayoutInflater inflar = miActividad.getLayoutInflater();

        ventana.setView(inflar.inflate(R.layout.carga_proceso, null));
        ventana.setCancelable(false);

        carga = ventana.create();
        carga.show();

    }

    void cargaCompleta() {
        carga.dismiss();

    }

}

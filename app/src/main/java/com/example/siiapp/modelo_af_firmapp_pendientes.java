package com.example.siiapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class modelo_af_firmapp_pendientes {

    /// ATRIBUTOS ///

    private String nombreColaborador, codigo, consecutivo, almacen;
    private Bitmap imagenCola;

    /// METODOS ///

    public modelo_af_firmapp_pendientes() {

    }

    public modelo_af_firmapp_pendientes(String nombreColaborador, String codigo, String consecutivo, Bitmap imagenCola, String almacen) {
        this.nombreColaborador = nombreColaborador;
        this.codigo = codigo;
        this.consecutivo = consecutivo;
        this.imagenCola = imagenCola;
        this.almacen = almacen;

    }

    public String getNombreColaborador() {
        return nombreColaborador;

    }

    public void setNombreColaborador(String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;

    }

    public String getCodigo() {
        return codigo;

    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;

    }

    public String getConsecutivo() {
        return consecutivo;

    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public Bitmap getImagenCola() {
        return imagenCola;

    }

    public void setImagenCola(Bitmap imagenCola) {
        this.imagenCola = imagenCola;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

}

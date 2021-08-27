package com.example.siiapp;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class modelo_prestador_af_firmapp {

    /// ATRIBUTOS ///

    private static int consecutivo;
    private static String cf, cedula, nombre;
    private static ArrayList<String> articulos = new ArrayList<>();
    private static String firmaI;
    private static Bitmap firmaA;

    /// METODOS ///


    public static int getConsecutivo() {
        return consecutivo;
    }

    public static void setConsecutivo(int consecutivo) {
        modelo_prestador_af_firmapp.consecutivo = consecutivo;
    }

    public static String getCf() {
        return cf;
    }

    public static void setCf(String cf) {
        modelo_prestador_af_firmapp.cf = cf;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<String> getArticulos() {
        return articulos;
    }

    public void setArticulos(ArrayList<String> articulos) {
        this.articulos = articulos;
    }

    public static String getFirmaI() {
        return firmaI;
    }

    public static void setFirmaI(String firmaI) {
        modelo_prestador_af_firmapp.firmaI = firmaI;
    }

    public static Bitmap getFirmaA() {
        return firmaA;
    }

    public static void setFirmaA(Bitmap firmaA) {
        modelo_prestador_af_firmapp.firmaA = firmaA;
    }
}

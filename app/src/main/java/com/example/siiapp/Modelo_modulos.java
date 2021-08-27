package com.example.siiapp;

public class Modelo_modulos {

    /// ATRIBUTOS ///

    private String nombreApp, ruta;
    private int img_app;

    /// METODOS ///

    public Modelo_modulos() {

    }

    public Modelo_modulos(String nombreApp, int img_app, String ruta) {
        this.nombreApp = nombreApp;
        this.img_app = img_app;
        this.ruta = ruta;

    }

    public String getNombreApp() {
        return nombreApp;
    }

    public void setNombreApp(String nombreApp) {
        this.nombreApp = nombreApp;
    }

    public int getImg_app() {
        return img_app;
    }

    public void setImg_app(int img_app) {
        this.img_app = img_app;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}

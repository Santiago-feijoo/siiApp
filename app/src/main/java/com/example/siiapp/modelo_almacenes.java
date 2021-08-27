package com.example.siiapp;

public class modelo_almacenes {

    /// ATRIBUTOS ///

    private static String id, nombre;

    /// METODOS ///

    public modelo_almacenes() {

    }

    public modelo_almacenes(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}

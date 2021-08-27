package com.example.siiapp;

public class modelo_descripciones_pd {

    /// ATRIBUTOS ///

    private String id, descripcion, interferencia;

    /// METODOS ///

    public modelo_descripciones_pd() {

    }

    public modelo_descripciones_pd(String id, String descripcion, String interferencia) {
        this.id = id;
        this.descripcion = descripcion;
        this.interferencia = interferencia;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInterferencia() {
        return interferencia;
    }

    public void setInterferencia(String interferencia) {
        this.interferencia = interferencia;
    }
}

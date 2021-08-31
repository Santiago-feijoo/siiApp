package com.example.siiapp;

public class modelo_descripciones_pd {

    /// ATRIBUTOS ///

    private String id, descripcion, interferencia, horasLaboradas, minutosLaborados;

    /// METODOS ///

    public modelo_descripciones_pd() {

    }

    public modelo_descripciones_pd(String id, String descripcion, String interferencia, String horasLaboradas, String minutosLaborados) {
        this.id = id;
        this.descripcion = descripcion;
        this.interferencia = interferencia;
        this.horasLaboradas = horasLaboradas;
        this.minutosLaborados = minutosLaborados;

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

    public String getHorasLaboradas() {
        return horasLaboradas;
    }

    public void setHorasLaboradas(String horasLaboradas) {
        this.horasLaboradas = horasLaboradas;
    }

    public String getMinutosLaborados() {
        return minutosLaborados;
    }

    public void setMinutosLaborados(String minutosLaborados) {
        this.minutosLaborados = minutosLaborados;
    }
}

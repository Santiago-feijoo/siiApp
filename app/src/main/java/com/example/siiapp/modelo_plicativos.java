package com.example.siiapp;

public class modelo_plicativos {

    /// ATRIBUTOS ///

    private String idAplicativo, aplicativo;

    /// METODOS ///

    public void App(String idAplicativo, String aplicativo) {
        this.idAplicativo = idAplicativo;
        this.aplicativo = aplicativo;

    }

    public String setIdAplicativo(String idAplicativo) {
        return idAplicativo;

    }

    public String setAplicativo(String aplicativo) {
        return aplicativo;

    }

    public String getIdAplicativo() {
        return idAplicativo;

    }

    public String getAplicativo() {
        return aplicativo;

    }



}

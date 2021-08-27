package com.example.siiapp;

public class modelo_respuestas {

    /// ATRIBUTOS ///

    String id, respuesta;

    /// METODOS ///

    public modelo_respuestas() {


    }

    public modelo_respuestas(String id, String respuesta) {
        this.id = id;
        this.respuesta = respuesta;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

}

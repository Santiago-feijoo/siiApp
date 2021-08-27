package com.example.siiapp;

public class modelo_preguntas {

    /// ATRIBUTOS ///

    private String formato, id, pregunta, respuesta;
    private String observacion;

    /// METODOS ///

    public modelo_preguntas() {

    }

    public modelo_preguntas(String formato, String id, String pregunta, String respuesta) {
        this.formato = formato;
        this.id = id;
        this.pregunta = pregunta;
        this.respuesta = respuesta;

    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}

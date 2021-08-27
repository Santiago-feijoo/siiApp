package com.example.siiapp;

public class modelo_viajes_colaborador {

    /// ATRIBUTOS ///

    private String permiso, placa, ruta, despacho, fecha;

    /// METODOS ///

    public modelo_viajes_colaborador() {

    }

    public modelo_viajes_colaborador(String permiso, String placa, String ruta, String despacho, String fecha) {
        this.permiso = permiso;
        this.placa = placa;
        this.ruta = ruta;
        this.despacho = despacho;
        this.fecha = fecha;

    }

    public String getPermiso() {
        return permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getDespacho() {
        return despacho;
    }

    public void setDespacho(String despacho) {
        this.despacho = despacho;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

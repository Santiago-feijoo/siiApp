package com.example.siiapp;

public class modelo_vehiculos {

    private final String ID_RUTA = "RUTA_BUS";
    private final String PLACA_BUS = "placa";
    private final String CAPACIDAD_M = "capacidadM";
    private final String CANTIDAD_P = "cantidadP";
    private final String ORIGEN_RUTA = "origen";
    private final String DESTINO_RUTA = "destino";

    private String tipo, placa;
    private int capacidadBus = 0, cantidadPasajeros = 0, estado;

    /// METODOS ///

    /// GET & SET VARIABLES ///

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidadBus() {
        return capacidadBus;
    }

    public void setCapacidadBus(int capacidadBus) {
        this.capacidadBus = capacidadBus;
    }

    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    /// GET & SET (RUTA DEL VEHICULO) ///

    public String getID_RUTA() {
        return ID_RUTA;
    }

    public String getPLACA_BUS() {
        return PLACA_BUS;
    }

    public String getCAPACIDAD_M() {
        return CAPACIDAD_M;
    }

    public String getCANTIDAD_P() {
        return CANTIDAD_P;
    }

    public String getORIGEN_RUTA() {
        return ORIGEN_RUTA;
    }

    public String getDESTINO_RUTA() {
        return DESTINO_RUTA;
    }
}

package com.example.siiapp;

public class modelo_equipos_pd {

    /// ATRIBUTOS ///

    private final String ID_CONSECUTIVO = "idConsecutivo";
    private final String CONSECUTIVO = "consecutivo";

    private static String con;
    private static String codigo, tipo, placa, descripcion, turno, jornada, horasLaboradas, km_inicial, km_final, estado;

    /// METODOS ///

    public modelo_equipos_pd() {

    }

    public modelo_equipos_pd(String codigo, String tipo, String placa, String descripcion, String km_inicial, String estado) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.placa = placa;
        this.descripcion = descripcion;
        this.km_inicial = km_inicial;
        this.estado = estado;

    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static String getTurno() {
        return turno;
    }

    public static void setTurno(String turno) {
        modelo_equipos_pd.turno = turno;
    }

    public static String getJornada() {
        return jornada;
    }

    public static void setJornada(String jornada) {
        modelo_equipos_pd.jornada = jornada;
    }

    public static String getHorasLaboradas() {
        return horasLaboradas;
    }

    public static void setHorasLaboradas(String horasLaboradas) {
        modelo_equipos_pd.horasLaboradas = horasLaboradas;
    }

    public static String getKm_inicial() {
        return km_inicial;
    }

    public static void setKm_inicial(String km_inicial) {
        modelo_equipos_pd.km_inicial = km_inicial;
    }

    public static String getKm_final() {
        return km_final;
    }

    public static void setKm_final(String km_final) {
        modelo_equipos_pd.km_final = km_final;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getID_CONSECUTIVO() {
        return ID_CONSECUTIVO;
    }

    public String getCONSECUTIVO() {
        return CONSECUTIVO;
    }
}

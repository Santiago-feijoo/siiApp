package com.example.siiapp;

public class Sesion {

    private String Id, codigo, nombreUsuario, cargo, rol, clave, nomApp, app;
    private final String ID_MEMORIA = "credenciales";
    private final String ID_USUARIO = "id_user";
    private final String NOM_APP = "nomApp_user";
    private final String ID_APP = "idApp_user";
    private final String CF = "cf_user";
    private final String NOM_USER = "nom_user";
    private final String CARGO_USER = "cargo_user";
    private final String ROL = "rol";

    /// METODOS ///

    /// GET & SET VARIABLES ///

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNomApp() {
        return nomApp;
    }

    public void setNomApp(String nomApp) {
        this.nomApp = nomApp;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    /// GET & SET (CREDENCIALES) ///

    public String getID_MEMORIA() {
        return ID_MEMORIA;
    }

    public String getID_USUARIO() {
        return ID_USUARIO;
    }

    public String getNOM_APP() {
        return NOM_APP;
    }

    public String getID_APP() {
        return ID_APP;
    }

    public String getCF() {
        return CF;
    }

    public String getNOM_USER() {
        return NOM_USER;
    }

    public String getCARGO_USER() {
        return CARGO_USER;
    }

    public String getROL() {
        return ROL;
    }

}

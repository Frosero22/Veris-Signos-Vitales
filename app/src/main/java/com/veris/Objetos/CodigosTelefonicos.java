package com.veris.Objetos;

public class CodigosTelefonicos {

    private int codigoPais;
    private String nombrePais;
    private String codigoTelefonico;
    private int cantidadDigitosCelular;
    private int cantidadDigitosFijo;
    private String codigoISO;
    private String esDefault;

    public int getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(int codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getCodigoTelefonico() {
        return codigoTelefonico;
    }

    public void setCodigoTelefonico(String codigoTelefonico) {
        this.codigoTelefonico = codigoTelefonico;
    }

    public int getCantidadDigitosCelular() {
        return cantidadDigitosCelular;
    }

    public void setCantidadDigitosCelular(int cantidadDigitosCelular) {
        this.cantidadDigitosCelular = cantidadDigitosCelular;
    }

    public int getCantidadDigitosFijo() {
        return cantidadDigitosFijo;
    }

    public void setCantidadDigitosFijo(int cantidadDigitosFijo) {
        this.cantidadDigitosFijo = cantidadDigitosFijo;
    }

    public String getCodigoISO() {
        return codigoISO;
    }

    public void setCodigoISO(String codigoISO) {
        this.codigoISO = codigoISO;
    }

    public String getEsDefault() {
        return esDefault;
    }

    public void setEsDefault(String esDefault) {
        this.esDefault = esDefault;
    }


    public String toString(){
        return codigoTelefonico+" - "+nombrePais;
    }

}

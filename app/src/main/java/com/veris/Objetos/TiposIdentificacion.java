package com.veris.Objetos;

public class TiposIdentificacion {

    public int codigoTipoIdentificacion;
    private String nombreTipoIdentificacion;
    private String nemonicoAlgoritmoValidacion;

    public int getCodigoTipoIdentificacion() {
        return codigoTipoIdentificacion;
    }

    public void setCodigoTipoIdentificacion(int codigoTipoIdentificacion) {
        this.codigoTipoIdentificacion = codigoTipoIdentificacion;
    }

    public String getNombreTipoIdentificacion() {
        return nombreTipoIdentificacion;
    }

    public void setNombreTipoIdentificacion(String nombreTipoIdentificacion) {
        this.nombreTipoIdentificacion = nombreTipoIdentificacion;
    }

    public String getNemonicoAlgoritmoValidacion() {
        return nemonicoAlgoritmoValidacion;
    }

    public void setNemonicoAlgoritmoValidacion(String nemonicoAlgoritmoValidacion) {
        this.nemonicoAlgoritmoValidacion = nemonicoAlgoritmoValidacion;
    }


    public String toString(){
        return nombreTipoIdentificacion;
    }



}

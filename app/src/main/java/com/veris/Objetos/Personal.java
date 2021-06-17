package com.veris.Objetos;


public class Personal {

    public int secuenciaUsuario;
    private String nombreUsuario;
    private String idToken;
    private String codigoUsuario;


    public int getSecuenciaUsuario() {
        return secuenciaUsuario;
    }

    public void setSecuenciaUsuario(int secuenciaUsuario) {
        this.secuenciaUsuario = secuenciaUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }
}

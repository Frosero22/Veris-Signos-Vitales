package com.veris.Objetos;

import java.util.ArrayList;
import java.util.List;

public class PacienteCrear {

    private ArrayList<JSONDatosPrincipales> datosPrincipales;
    private ArrayList<JSONContacto> datosContacto;

    public ArrayList<JSONDatosPrincipales> getDatosPrincipales() {
        return datosPrincipales;
    }

    public void setDatosPrincipales(ArrayList<JSONDatosPrincipales> datosPrincipales) {
        this.datosPrincipales = datosPrincipales;
    }

    public List<JSONContacto> getDatosContacto() {
        return datosContacto;
    }

    public void setDatosContacto(ArrayList<JSONContacto> datosContacto) {
        this.datosContacto = datosContacto;
    }
}

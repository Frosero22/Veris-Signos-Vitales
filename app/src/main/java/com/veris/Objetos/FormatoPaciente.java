package com.veris.Objetos;

public class FormatoPaciente {

    public static String armaJsonPaciente(int intCodigoTipoIdentificacion,
                                          String strPrimerNombre,
                                          String strSegundoNombre,
                                          String strPrimerApellido,
                                          String strSegundoApellido,
                                          String strFechaNaciiento,
                                          String strGenero,
                                          String strNumeroCelular,
                                          int intCodigoPaisCelular,
                                          String strCorreoElectronico){
        String jsonPaciente = "";

        jsonPaciente = jsonPaciente +
                "{\n"+
                "datosPrincipales: {\n"+
                "codigoTipoIdentificacion:"+intCodigoTipoIdentificacion+",\n"+
                "primerNombre:"+strPrimerNombre+",\n"+
                "segundoNombre:"+strSegundoNombre+",\n"+
                "primerApellido:"+strPrimerApellido+",\n"+
                "segundoApellido:"+strSegundoApellido+",\n"+
                "fechaNacimiento:"+strFechaNaciiento+",\n"+
                "genero:"+strGenero+",\n"+
                "},\n"+
                "datosContacto: {\n"+
                "codigoPaisCelular:"+intCodigoPaisCelular+",\n"+
                "telefonoCelular:"+strNumeroCelular+",\n"+
                "correoElectronico:"+strCorreoElectronico+",\n" +
                "}\n"+
        "}";


            return jsonPaciente;
    }



}

package com.veris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.veris.Objetos.Personal;
import com.veris.Objetos.Preferencias;

public class OpcionActivity extends AppCompatActivity {

    private Button btnTomarMuestas;
    private Button btnCrearPaciente;
    private TextView txvNombrePersonal;
    private Personal objPersonal = new Personal();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opcion_activity);
        obtieneDatosEnMemoria();
        initComponentes();
    }

    public void obtieneDatosEnMemoria(){
        SharedPreferences shpDatosUsuario = getSharedPreferences("CRENDENCIALES_USUARIO", Context.MODE_PRIVATE);
        objPersonal.setIdToken(shpDatosUsuario.getString("strToken",""));
        objPersonal.setNombreUsuario(shpDatosUsuario.getString("strNombreUsuario",""));
        objPersonal.setCodigoUsuario(shpDatosUsuario.getString("strCodigoUsuario",""));
        objPersonal.setSecuenciaUsuario(shpDatosUsuario.getInt("intSecuenciaUsuario",0));

    }

    public void initComponentes(){
        txvNombrePersonal = findViewById(R.id.txvNombrePersonal);
        txvNombrePersonal.setText(objPersonal.getCodigoUsuario());
        btnTomarMuestas = findViewById(R.id.btnTomarMuestras);
        btnTomarMuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaPaciente();
            }
        });

        btnCrearPaciente = findViewById(R.id.btnCrearPaciente);
        btnCrearPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCrearPaciente();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem cerrarSesion = menu.findItem(R.id.cerrar_sesion);
        cerrarSesion.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                ConfirmaCerrarSesion();

                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    public void ConfirmaCerrarSesion(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OpcionActivity.this);
        alertDialogBuilder.setTitle("Sesión");
        alertDialogBuilder.setIcon(R.drawable.logoveris);
        alertDialogBuilder.setMessage("¿Está seguro de cerrar sesión?");
        alertDialogBuilder.setPositiveButton("Aceptar",
                (dialog, which) -> BorrarPreferenceSesion()).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
    }

    public void BorrarPreferenceSesion(){
        Preferencias.savePreferenciaBoolean(OpcionActivity.this, false, "estado.buton.sesion");
        startActivity(new Intent(getBaseContext(), LoginActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    public void goCrearPaciente(){
        Intent intent = new Intent(this,CrearPacienteActivity.class);
        startActivity(intent);
    }



    public void goTomaDeMuestras(){
        Intent intent = new Intent(this,SignosVitalesActivity.class);
        startActivity(intent);
    }

    public void validaPaciente(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(OpcionActivity.this);
        LayoutInflater li = (LayoutInflater) OpcionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vieW = li.inflate(R.layout.valida_paciente, null,false);
        final AlertDialog dialog = builder.create();
        dialog.setView(vieW);
        dialog.setCancelable(true);

        Button btnAceptar = vieW.findViewById(R.id.btnBuscar);
        Button btnCancelar = vieW.findViewById(R.id.btnCancelar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTomaDeMuestras();
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();

    }

}
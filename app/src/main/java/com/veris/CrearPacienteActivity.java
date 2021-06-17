package com.veris;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.veris.Objetos.CodigosTelefonicos;
import com.veris.Objetos.FormatoPaciente;
import com.veris.Objetos.JSONContacto;
import com.veris.Objetos.JSONDatosPrincipales;
import com.veris.Objetos.PacienteCrear;
import com.veris.Objetos.Personal;
import com.veris.Objetos.TiposIdentificacion;
import com.veris.Util.Utilitarios;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.veris.Objetos.FormatoPaciente.armaJsonPaciente;

public class CrearPacienteActivity extends AppCompatActivity {

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private Button btnTomarFechaNacimiento;
    private Button btnRegistrarPaciente;

    private Spinner spTiposIdentificación;
    private Spinner spCodigoTelefono;
    private Spinner spGenero;
    private ProgressDialog progressDialog;


    private TextInputLayout txlInpFechaNacimiento;
    private TextInputLayout txlInpIdentificacion;
    private TextInputLayout txtInpPrimerNombre;
    private TextInputLayout txlInpSegundoNombre;
    private TextInputLayout txlInpPrimerApellido;
    private TextInputLayout txlInpSegundoApellido;
    private TextInputLayout txlInpDomicilio;
    private TextInputLayout txlInpCorreo;
    private TextInputLayout txlInpCelular;




    private EditText editIdentificacion;
    private EditText editPrimerNobre;
    private EditText editSegundoNombre;
    private EditText editPrimerApellido;
    private EditText editSegundoApellido;
    private EditText editDomicilio;
    private EditText editCorreo;
    private EditText editCelular;
    private EditText editFechaNacimiento;



    private final OkHttpClient clienteHttp = new OkHttpClient();
    private Personal objPersonal = new Personal();
    private List<TiposIdentificacion> lsTiposIdentificacion = new ArrayList<>();
    private List<CodigosTelefonicos> lstCodigoTelefonicos = new ArrayList<>();
    private List<String> lsGeneros = new ArrayList<>();

    private int intCodigoTipoIdentificacion;
    private int intCodigoPaisCelular;
    private int intCantidadDigitosCelular;
    private int intCantidadDigitosFijo;
    private String strGenero;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_paciente_activity);
        initComponente();
        wsTiposIdentificacion();
        wsTiposDeTelefonos();
        llenaComboGenero();



    }

    public void initComponente(){

        SharedPreferences shpDatosUsuario = getSharedPreferences("CRENDENCIALES_USUARIO", Context.MODE_PRIVATE);
        objPersonal.setIdToken(shpDatosUsuario.getString("strToken",""));
        objPersonal.setNombreUsuario(shpDatosUsuario.getString("strNombreUsuario",""));
        objPersonal.setCodigoUsuario(shpDatosUsuario.getString("strCodigoUsuario",""));
        objPersonal.setSecuenciaUsuario(shpDatosUsuario.getInt("intSecuenciaUsuario",0));

        txlInpIdentificacion = findViewById(R.id.txtInpIdentificacion);
        editIdentificacion = findViewById(R.id.txtidentificacion);

        txtInpPrimerNombre = findViewById(R.id.txtInpPrimerNombre);
        editPrimerNobre = findViewById(R.id.txtPrimerNombre);

        txlInpSegundoNombre = findViewById(R.id.txtInpSegundoNombre);
        editSegundoNombre = findViewById(R.id.txtSegundoNombre);

        txlInpPrimerApellido = findViewById(R.id.txtInpPrimerApellido);
        editPrimerApellido = findViewById(R.id.txtPrimerApellido);

        txlInpSegundoApellido = findViewById(R.id.txtInpSegundoApellido);
        editSegundoApellido = findViewById(R.id.txtSegundoApellido);

        txlInpDomicilio = findViewById(R.id.edtInpDomicilio);
        editDomicilio = findViewById(R.id.editDomicilio);

        txlInpCorreo = findViewById(R.id.editInpCorreo);
        editCorreo = findViewById(R.id.editCorreo);

        txlInpCelular = findViewById(R.id.editInpCelular);
        editCelular = findViewById(R.id.editCelular);


        spTiposIdentificación = findViewById(R.id.tiposIdentificación);
        spCodigoTelefono = findViewById(R.id.codigoTelefono);
        spGenero = findViewById(R.id.spGenero);

        editFechaNacimiento = findViewById(R.id.editFechaNacimiento);
        txlInpFechaNacimiento = findViewById(R.id.editInpFechaNacimiento);
        btnTomarFechaNacimiento = findViewById(R.id.btnTomarFechaNacimiento);
        btnTomarFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invocaDatePicker();
            }
        });

        btnRegistrarPaciente = findViewById(R.id.btnRegistrarPaciente);
        btnRegistrarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registrarPaciente();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);

                }
            }
        });

        spTiposIdentificación.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intCodigoTipoIdentificacion = lsTiposIdentificacion.get(position).getCodigoTipoIdentificacion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intCodigoTipoIdentificacion = lsTiposIdentificacion.get(0).getCodigoTipoIdentificacion();
            }
        });

        spCodigoTelefono.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intCodigoPaisCelular = lstCodigoTelefonicos.get(position).getCodigoPais();
                Log.e("-----","intCodigoPaisCelular --- "+intCodigoPaisCelular);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intCodigoPaisCelular = lstCodigoTelefonicos.get(0).getCodigoPais();
                Log.e("-----","intCodigoPaisCelular --- "+intCodigoPaisCelular);
            }
        });


        spGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strGenero = parent.getItemAtPosition(position).toString();
                Log.e("-----","GENERO --- "+strGenero);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strGenero = parent.getItemAtPosition(0).toString();
                Log.e("-----","GENERO --- "+strGenero);
            }
        });

    }

    public void wsTiposIdentificacion(){
        progressDialog = Utilitarios.progressDialog(this,"Cargando...");
        OkHttpClient httpClient = clienteHttp.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                                                          .readTimeout(15, TimeUnit.SECONDS)
                                                          .writeTimeout(15, TimeUnit.SECONDS)
                                                          .build();


        Request request = new Request.Builder()
                .url(this.getString(R.string.urlSeg)+"/v1/tipos_identificacion/gestion_usuarios?codigoEmpresa=1")
                .addHeader("Application", "UEhBTlRPTVhfV0VC")
                .addHeader("Authorization", objPersonal.getIdToken())
                .method("GET", null)
                .build();



        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                progressDialog.dismiss();
                Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);
                Utilitarios.refrescaToken(CrearPacienteActivity.this);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{

                    if(response.code() == 200){
                        ResponseBody responseBody = response.body();
                        JSONObject jsonObject = new JSONObject(responseBody.string());

                        Log.e("RESPONSE","IDENTIFICACION ---> "+jsonObject );

                        JSONArray jsonArreglo = jsonObject.getJSONArray("data");

                        for(int i = 0; i< jsonArreglo.length(); i++){
                            TiposIdentificacion objTiposIdentificacion = new TiposIdentificacion();
                            objTiposIdentificacion.setCodigoTipoIdentificacion(jsonArreglo.getJSONObject(i).getInt("codigoTipoIdentificacion"));
                            objTiposIdentificacion.setNombreTipoIdentificacion(jsonArreglo.getJSONObject(i).getString("nombreTipoIdentificacion"));
                            objTiposIdentificacion.setNemonicoAlgoritmoValidacion(jsonArreglo.getJSONObject(i).getString("nemonicoAlgoritmoValidacion"));
                            lsTiposIdentificacion.add(objTiposIdentificacion);
                        }


                        ArrayAdapter<TiposIdentificacion> a = new ArrayAdapter<TiposIdentificacion>(CrearPacienteActivity.this,android.R.layout.simple_dropdown_item_1line,lsTiposIdentificacion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spTiposIdentificación.setAdapter(a);
                            }
                        });
                    }else if(response.code() == 401){
                        progressDialog.dismiss();
                        Utilitarios.refrescaToken(CrearPacienteActivity.this);
                        Intent intent = new Intent(CrearPacienteActivity.this,CrearPacienteActivity.class);
                        startActivity(intent);
                        finish();
                    }




                }catch (Exception e){
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);

                }
            }
        });



    }


    public void wsTiposDeTelefonos(){
        OkHttpClient httpClient = clienteHttp.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();


        Request request = new Request.Builder()
                .url(this.getString(R.string.urlGen)+"/v1/paises/codigos_telefonicos?codigoEmpresa=1")
                .addHeader("Application", "UEhBTlRPTVhfV0VC")
                .addHeader("Authorization", objPersonal.getIdToken())
                .method("GET", null)
                .build();



        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                progressDialog.dismiss();
                Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{

                    if(response.code() == 200){
                        ResponseBody responseBody = response.body();
                        JSONObject jsonObject = new JSONObject(responseBody.string());

                        Log.e("RESPONSE","IDENTIFICACION ---> "+jsonObject );

                        JSONArray jsonArreglo = jsonObject.getJSONArray("data");
                        CodigosTelefonicos objCodigosTelefonicosAux = new CodigosTelefonicos();
                        for(int i = 0; i< jsonArreglo.length(); i++){
                            CodigosTelefonicos objCodigosTelefonicos = new CodigosTelefonicos();

                            objCodigosTelefonicos.setCodigoPais(jsonArreglo.getJSONObject(i).getInt("codigoPais"));
                            objCodigosTelefonicos.setNombrePais(jsonArreglo.getJSONObject(i).getString("nombrePais"));
                            objCodigosTelefonicos.setCodigoTelefonico(jsonArreglo.getJSONObject(i).getString("codigoTelefonico"));
                            objCodigosTelefonicos.setCodigoISO(jsonArreglo.getJSONObject(i).getString("codigoISO"));
                            objCodigosTelefonicos.setEsDefault(jsonArreglo.getJSONObject(i).getString("esDefault"));

                            if(lstCodigoTelefonicos.size() == 0){
                                if(objCodigosTelefonicos.getEsDefault().equalsIgnoreCase("S")){
                                    lstCodigoTelefonicos.add(objCodigosTelefonicos);
                                }else{

                                    objCodigosTelefonicosAux.setCodigoPais(jsonArreglo.getJSONObject(i).getInt("codigoPais"));
                                    objCodigosTelefonicosAux.setNombrePais(jsonArreglo.getJSONObject(i).getString("nombrePais"));
                                    objCodigosTelefonicosAux.setCodigoTelefonico(jsonArreglo.getJSONObject(i).getString("codigoTelefonico"));
                                    objCodigosTelefonicosAux.setCodigoISO(jsonArreglo.getJSONObject(i).getString("codigoISO"));
                                    objCodigosTelefonicosAux.setEsDefault(jsonArreglo.getJSONObject(i).getString("esDefault"));
                                }
                            }else{
                                lstCodigoTelefonicos.add(objCodigosTelefonicos);
                            }

                        }

                        lstCodigoTelefonicos.add(objCodigosTelefonicosAux);

                        ArrayAdapter<CodigosTelefonicos> a = new ArrayAdapter<CodigosTelefonicos>(CrearPacienteActivity.this,android.R.layout.simple_spinner_dropdown_item,lstCodigoTelefonicos);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spCodigoTelefono.setAdapter(a);

                            }
                        });

                        progressDialog.dismiss();
                    }else if(response.code() == 401){
                        progressDialog.dismiss();
                        Utilitarios.refrescaToken(CrearPacienteActivity.this);
                        Intent intent = new Intent(CrearPacienteActivity.this,CrearPacienteActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }catch (Exception e){
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);

                }
            }
        });



    }

    public void llenaComboGenero(){
        lsGeneros.add("MASCULINO");
        lsGeneros.add("FEMENINO");

        ArrayAdapter<String> a = new ArrayAdapter<String>(CrearPacienteActivity.this,android.R.layout.simple_spinner_dropdown_item,lsGeneros);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spGenero.setAdapter(a);
            }
        });


    }

    public void invocaDatePicker(){

        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int anio = calendar.get(Calendar.YEAR);

        Log.e("DIA","---> "+dia);
        Log.e("MES","---> "+mes);
        Log.e("AÑO","---> "+anio);



        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editFechaNacimiento.setText(dayOfMonth+"/"+(month+1)+"/"+year);

            }
        },dia,mes,anio);
        datePickerDialog.show();

    }


    public void registrarPaciente() throws JSONException, ParseException {
            if(validaComponentes()){
                wsCrearPaciente();
            }
    }


    public boolean validaComponentes(){

        if(editIdentificacion.getText().toString().equals("") || editIdentificacion.getText() == null){
            txlInpIdentificacion.setErrorEnabled(true);
            txlInpIdentificacion.setError("Campo Obligatorio");
            return false;
        }

        if(editPrimerNobre.getText().toString().equals("") || editPrimerNobre.getText() == null){
            txtInpPrimerNombre.setErrorEnabled(true);
            txtInpPrimerNombre.setError("Campo Obligatorio");
            return false;
        }

        if(editPrimerApellido.getText().toString().equals("") || editPrimerApellido.getText() == null){
            txlInpPrimerApellido.setErrorEnabled(true);
            txlInpPrimerApellido.setError("Campo Obligatorio");
            return false;
        }


        if(editFechaNacimiento.getText().toString().equals("") || editFechaNacimiento.getText() == null){
            txlInpFechaNacimiento.setErrorEnabled(true);
            txlInpFechaNacimiento.setError("Campo Obligatorio");
            return false;
        }

        if(editDomicilio.getText().toString().equals("") || editDomicilio.getText() == null){
            txlInpDomicilio.setErrorEnabled(true);
            txlInpDomicilio.setError("Campo Obligatorio");
            return false;
        }

        if(editCorreo.getText().toString().equals("") || editCorreo.getText() == null){
            txlInpCorreo.setErrorEnabled(true);
            txlInpCorreo.setError("Campo Obligatorio");
            return false;
        }

        if(editCelular.getText().toString().equals("") || editCelular.getText() == null){
            txlInpCelular.setErrorEnabled(true);
            txlInpCelular.setError("Campo Obligatorio");
            return false;
        }



        return  true;
    }



    public void wsCrearPaciente() throws JSONException, ParseException {
        progressDialog = Utilitarios.progressDialog(this,"Creando Paciente...");
        OkHttpClient httpClient = clienteHttp.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                                                          .readTimeout(15, TimeUnit.SECONDS)
                                                          .writeTimeout(15, TimeUnit.SECONDS)
                                                          .build();

      String strGen = "";
      if(strGenero.equalsIgnoreCase("MASCULINO")){
          strGen = "M";
      }else if(strGenero.equalsIgnoreCase("FEMENINO")){
          strGen = "F";
      }

        String strJson = armaJsonPaciente(intCodigoTipoIdentificacion,
                                  editPrimerNobre.getText().toString(),
                                  editSegundoNombre.getText().toString(),
                                  editPrimerApellido.getText().toString(),
                                  editSegundoApellido.getText().toString(),
                                  editFechaNacimiento.getText().toString(),
                                  strGen,
                                  editCelular.getText().toString(),
                                  intCodigoPaisCelular,
                                  editCorreo.getText().toString());

       long date =  Date.parse(editFechaNacimiento.getText().toString());



        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("pacienteCrear",
                null,RequestBody.create(MediaType.parse("application/json"), "{" +
                                        "  \"datosPrincipales\": {" +
                                        "    \"codigoTipoIdentificacion\": "+intCodigoTipoIdentificacion+"," +
                                        "    \"numeroIdentificacion\": \""+editIdentificacion.getText().toString()+"\"," +
                                        "    \"primerNombre\": \""+editPrimerNobre.getText().toString()+"\"," +
                                        "    \"segundoNombre\": \""+editSegundoNombre.getText().toString()+"\"," +
                                        "    \"primerApellido\": \""+editPrimerApellido.getText().toString()+"\"," +
                                        "    \"segundoApellido\": \""+editSegundoApellido.getText().toString()+"\"," +
                                        "    \"fechaNacimiento\": \""+dateFormat.format(date)+"\"," +
                                        "    \"genero\": \""+strGen+"\"" +
                                        "  }," +
                                        "  \"datosContacto\": {" +
                                        "    \"correoElectronico\": \""+editCorreo.getText().toString()+"\"," +
                                        "    \"codigoPaisCelular\": "+intCodigoPaisCelular+"," +
                                        "    \"telefonoCelular\": \""+editCelular.getText().toString()+"\"" +
                                        "  }" +
                                        "}")).build();

        Request request = new Request.Builder()
                .url(this.getString(R.string.urlGen)+"/v1/pacientes")
                .addHeader("Application", "UEhBTlRPTVhfV0VC")
                .addHeader("IdOrganizacion", "365509c8-9596-4506-a5b3-487782d5876e")
                .addHeader("Authorization",objPersonal.getIdToken())
                .post(body)
                .build();


        clienteHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                progressDialog.dismiss();
                Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    ResponseBody responseBody = response.body();
                    JSONObject jsonObject = new JSONObject(responseBody.string());

                    if(response.code() == 200){
                        progressDialog.dismiss();

                        Log.e("RESPONSE","IDENTIFICACION ---> "+jsonObject );
                        mensajeTransaccionExitosa();

                    }else{
                        progressDialog.dismiss();
                        Log.e("RESPONSE","IDENTIFICACION ---> "+jsonObject );
                        Log.e("RESPONSE","IDENTIFICACION ---> "+jsonObject.getString("message") );
                        Utilitarios.mensajeError(CrearPacienteActivity.this,"Error en la Transacción",jsonObject.getString("message"));
                    }


                }catch (Exception e){
                    progressDialog.dismiss();
                    Utilitarios.mensajeError(CrearPacienteActivity.this,"Ocurrío un error",""+e);
                }

            }
        });

    }




    public void mensajeTransaccionExitosa(){
        Looper.prepare();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CrearPacienteActivity.this);
        alertDialogBuilder.setTitle("Transacción Exitosa");
        alertDialogBuilder.setIcon(R.drawable.cheque);
        alertDialogBuilder.setMessage("Paciente creado con exíto, ¿Deseá registrar los signos vitales?");
        alertDialogBuilder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goSignosVitales();
                        dialog.dismiss();

                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                goOpciones();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        Looper.loop();
    }



    public void goSignosVitales(){
        Toast.makeText(this, "OPCION NO VALIDA POR EL MOMENTO, REDIRECCIONADO A LAS OPCIONES", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CrearPacienteActivity.this,OpcionActivity.class);
        startActivity(intent);
        finish();
    }

    public void goOpciones(){
        Intent intent = new Intent(CrearPacienteActivity.this,OpcionActivity.class);
        startActivity(intent);
        finish();
    }



}
package com.veris;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.veris.Objetos.Personal;
import com.veris.Objetos.Preferencias;
import com.veris.Util.Utilitarios;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    private Button btnIniciarSesion;
    private TextInputLayout inputUser;
    private TextInputLayout inputPassword;
    private EditText edtUser;
    private EditText edtPassword;
    private ProgressDialog progressDialog;
    private final OkHttpClient clienteHttp = new OkHttpClient();
    public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.buton.sesion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (Preferencias.obtenerPreferenciaBoolean(this, PREFERENCE_ESTADO_BUTTON_SESION)) {
            Intent i = new Intent(LoginActivity.this, OpcionActivity.class);
            startActivity(i);
            finish();
        }
        initComponentes();
    }

    public void initComponentes(){

        inputUser = findViewById(R.id.input_user);
        inputPassword = findViewById(R.id.input_password);
        edtUser = findViewById(R.id.edit_user);
        edtPassword = findViewById(R.id.edit_password);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaComponentes()){
                   if(validaInternet()){
                       String strUsuario = edtUser.getText().toString();
                       String strPassword = edtPassword.getText().toString();
                       wsLogin(strUsuario,strPassword);
                   }
                }
            }
        });
    }

    //VALIDA CAMPOS DE USUARIO Y CONTRASEÑA
    public boolean validaComponentes(){

        if(edtUser.getText().toString().equals("")){
            inputUser.setErrorEnabled(true);
            inputUser.setError("Campo Obligatorio");
            return false;
        }

        if(edtPassword.getText().toString().equals("")){
            inputPassword.setErrorEnabled(true);
            inputPassword.setError("Campo Obligatorio");
            return false;
        }


        return true;
    }

    public boolean validaInternet(){

        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con != null;
        NetworkInfo networkInfo = con.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Utilitarios.mensajeError(LoginActivity.this,"Conexión","Debe estar conectado/a a una red para poder continuar");
            return false;
        }



    }

    //SE EJECUTA EL SERVICIO DE LOGIN
    public void wsLogin(String strUsuario, String strPassword){

        progressDialog = Utilitarios.progressDialog(this,"Cargando...");
        OkHttpClient httpClient = clienteHttp.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                                                          .readTimeout(15, TimeUnit.SECONDS)
                                                          .writeTimeout(15, TimeUnit.SECONDS)
                                                          .build();

        JsonObject jsonInfo = new JsonObject();
        jsonInfo.addProperty("user",strUsuario);
        jsonInfo.addProperty("pass",strPassword);

        String strHeader = Utilitarios.generarHeader(LoginActivity.this,strUsuario,strPassword);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody =RequestBody.create(mediaType, jsonInfo.toString());
        Request request = new Request.Builder()
                .url(this.getString(R.string.urlSeg)+"/v1/autenticacion/login")
                .addHeader("Application", "UEhBTlRPTVhfV0VC")
                .addHeader("Authorization", strHeader)
                .method("POST", postBody)
                .build();


        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                progressDialog.dismiss();
                Utilitarios.mensajeError(LoginActivity.this,"Ocurrío un error",""+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{

                    ResponseBody responseBody = response.body();
                    JSONObject jsonObject = new JSONObject(responseBody.string());



                    if(response.code() == 200){
                        progressDialog.dismiss();
                        String objJSON = jsonObject.getString("data");
                        JSONObject data = new JSONObject(objJSON);
                        String strEstadoCuenta = data.getString("estadoUsuario");

                        if(validaCuenta(strEstadoCuenta)){
                            guardaDatosMemoria(data);
                            Preferencias.savePreferenciaBoolean(LoginActivity.this, true, PREFERENCE_ESTADO_BUTTON_SESION);
                            goOpcionActivity();
                        }


                    }else{
                        progressDialog.dismiss();
                        String strMensaje = jsonObject.getString("message");
                        if(strMensaje.isEmpty() || strMensaje.equals("") || strMensaje.equalsIgnoreCase("null")){
                            Utilitarios.mensajeError(LoginActivity.this,"Autenticación","Ocurrió un error, Comuniquese con el aréa de sistemas");
                        }else{
                            Utilitarios.mensajeError(LoginActivity.this,"Autenticación",strMensaje);
                        }

                    }



                }catch (Exception e){
                    e.printStackTrace();
                    Utilitarios.mensajeError(LoginActivity.this,"Ocurrío un error en el response",""+e);
                }
            }
        });


    }

    public boolean validaCuenta(String strEstadoCuenta){

        if(strEstadoCuenta.equalsIgnoreCase("FORCE_CHANGE_PASSWORD")){
            Utilitarios.mensajeError(LoginActivity.this,"Autenciación","Por favor confirme su cuenta en PHANTOM X");
            return false;
        }

        if(strEstadoCuenta.equalsIgnoreCase("CHANGE_PASSWORD")){
            Utilitarios.mensajeError(LoginActivity.this,"Autenticación","Contraseña Caducada, por favor actualicela en PHANTOM X");
        }

        if(strEstadoCuenta.equalsIgnoreCase("RESET_REQUIRED")){
            Utilitarios.mensajeError(LoginActivity.this,"Autenciación","Cuenta Importada, Por favor siga el flujo para recuperar su contraseña");
        }

        return true;

    }


    public void guardaDatosMemoria(JSONObject jsonObject) throws JSONException {

        int intSecuenciaUsuario = jsonObject.getInt("secuenciaUsuario");
        String strToken = jsonObject.getString("idToken");
        String strNombreUsuario = jsonObject.getString("nombreUsuario");
        String strCodigoUsuario = jsonObject.getString("codigoUsuario");



        SharedPreferences shpDatosUsuario = getSharedPreferences("CRENDENCIALES_USUARIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shpDatosUsuario.edit();
        editor.putString("strToken",strToken);
        editor.putString("strNombreUsuario",strNombreUsuario);
        editor.putString("strCodigoUsuario",strCodigoUsuario);
        editor.putInt("intSecuenciaUsuario",intSecuenciaUsuario);
        editor.putString("strUsuarioLogin",edtUser.getText().toString());
        editor.putString("strPassLogin",edtPassword.getText().toString());
        editor.apply();

    }


    public void goOpcionActivity(){
        Intent intent = new Intent(this,OpcionActivity.class);
        startActivity(intent);
        finish();
    }


}
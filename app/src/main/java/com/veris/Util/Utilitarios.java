package com.veris.Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.veris.LoginActivity;
import com.veris.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

public class Utilitarios {

    private static String strUsuario;
    private static String strPass;
    private static ProgressDialog progressDialog;
    private static final OkHttpClient client = new OkHttpClient();

    public static void mensajeError(Context context,  String strTitulo,String strMensaje){
        Looper.prepare();
        new AlertDialog.Builder(context)
                .setIcon(R.mipmap.cancelar)
                .setTitle(strTitulo)
                .setMessage(strMensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
        Looper.loop();

    }


    public static ProgressDialog progressDialog(Context context, String mensaje){

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(mensaje);
        progressDialog.show();

        return progressDialog;
    }


    public static String generarHeader(Context context, String usuario, String clave) {
        HashMap<String, String> params = new HashMap<String, String>();
        String auth = "";
        if(usuario != null && clave != null){
            String credentials = usuario + ":" + clave;
            auth =  "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE|Base64.NO_WRAP);
            //auth = new String(Base64.encode((usuario+":"+clave).getBytes(), Base64.URL_SAFE|Base64.NO_WRAP));
            Log.d(TAG, auth);
        }

        params.put("Authorization", auth);

        return auth;
    }


    public static void refrescaToken(Context context){
        SharedPreferences shpDatosUsuario = context.getSharedPreferences("CRENDENCIALES_USUARIO", Context.MODE_PRIVATE);
        strUsuario = shpDatosUsuario.getString("strUsuarioLogin","");
        strPass = shpDatosUsuario.getString("strPassLogin","");

        Log.e("USUARIOOO","---> "+strUsuario);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = Utilitarios.progressDialog(context,"Validando Sesión, Un Momento...");
                OkHttpClient cliente = client.newBuilder().connectTimeout(15, TimeUnit.SECONDS)
                                                            .readTimeout(15, TimeUnit.SECONDS)
                                                            .writeTimeout(15, TimeUnit.SECONDS)
                                                            .build();

                String strHeader = Utilitarios.generarHeader(context,strUsuario,strPass);

                JsonObject jsonInfo = new JsonObject();
                jsonInfo.addProperty("user",strUsuario);
                jsonInfo.addProperty("pass",strPass);

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody postBody =RequestBody.create(mediaType, jsonInfo.toString());
                Request request = new Request.Builder()
                        .url(context.getString(R.string.urlSeg)+"/v1/autenticacion/login")
                        .addHeader("Application", "UEhBTlRPTVhfV0VC")
                        .addHeader("Authorization", strHeader)
                        .method("POST", postBody)
                        .build();


                cliente.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        progressDialog.dismiss();
                        Utilitarios.mensajeError(context,"Error en la Transacción","Ocurrió un error, Excepción obtenida: "+e);

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try{

                            if(response.code() == 200){

                                progressDialog.dismiss();
                                ResponseBody responseBody = response.body();
                                JSONObject jsonObject = new JSONObject(responseBody.string());
                                String objJSON = jsonObject.getString("data");
                                JSONObject data = new JSONObject(objJSON);
                                String strToken = data.getString("idToken");
                               SharedPreferences.Editor editor =  shpDatosUsuario.edit();
                               editor.putString("strToken",strToken);
                               Log.e("---->","tokeeeeeeeen "+strToken);
                               editor.apply();
                            }


                        }catch (Exception e){
                            progressDialog.dismiss();
                            Utilitarios.mensajeError(context,"Error en la Transacción","Ocurrió un error, Excepción obtenida: "+e);
                        }
                    }
                });


            }
        });

    }

}

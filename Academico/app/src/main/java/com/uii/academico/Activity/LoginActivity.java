package com.uii.academico.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        Utils.init(this);

        username = (EditText) findViewById(R.id.ET_username);
        password = (EditText) findViewById(R.id.ET_password);
        login = (Button) findViewById(R.id.BT_login);
        progressBar = (ProgressBar) findViewById(R.id.PB_progress);
        progressBar.setVisibility(View.GONE);


        username.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // jika eventnya key-down/pencet dan tekan tombol "enter"

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    username.clearFocus();
                    password.requestFocus();
                    return true;
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = username.getText().toString().trim();
                String pass = password.getText().toString();

                if (!user.isEmpty() && !pass.isEmpty()) {
                    // Cek untuk Login
                    RequestLogin(user, pass, Utils.sharedPreferences.getString("FcmToken",""));

                    //Progress Bar Berputar
                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

                } else {
                    Toast.makeText(LoginActivity.this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void RequestLogin (final String user, final String pass, final String token) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject res = new JSONObject(response);
                            boolean jsonResponse = res.getBoolean("error");

                            if (!jsonResponse){

                                // Ambil value jsonObject (dari WebService)
                                String nama = res.getString("nama");
                                String id_parent = res.getString("id_parent");
                                String status = res.getString("status");
                                String foto = res.getString("foto");
                                String id_jurusan = res.getString("id_jurusan");
                                String namaJurusan = res.getString("nama_jurusan");
                                String namaParent = res.getString("nama_parent");
                                String namaKajur = res.getString("nama_kajur");

                                Utils.editSP.putString("id_user",user);
                                Utils.editSP.putString("pass", pass);

                                Utils.editSP.putString("nama",nama);
                                Utils.editSP.putString("id_parent", id_parent);
                                Utils.editSP.putString("status",status);
                                Utils.editSP.putString("foto",foto);
                                Utils.editSP.putString("id_jurusan",id_jurusan);
                                Utils.editSP.putString("nama_jurusan",namaJurusan);
                                Utils.editSP.putString("nama_parent",namaParent);
                                Utils.editSP.putString("nama_kajur",namaKajur);

                                Utils.editSP.commit();

                                Log.e("STATUS dan PARENT", status + id_parent);

                                // Ke Main Activity
                                Intent keMainActivity = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(keMainActivity);
                            }else{
                                Toast.makeText(LoginActivity.this, "Maaf Username atau Password Salah", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("user", user);
                params.put("pass", pass);
                params.put("token", token);

                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    @Override
    public void onBackPressed() {

        Intent keSplash = new Intent(LoginActivity.this,SplashScreenActivity.class);
        keSplash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        keSplash.putExtra("EXIT",true);
        startActivity(keSplash);
    }

}

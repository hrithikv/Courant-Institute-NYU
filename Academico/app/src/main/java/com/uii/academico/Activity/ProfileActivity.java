package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.R;
import com.uii.academico.Utility.BlurBuilder;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ProgressBar progressUpdate;
    ProgressDialog progressSubmit, progressLogout;
    NetworkImageView fotoProfil;
    TextView nama, idUser, jurusan, dpa, kajurMhs, kajurDosen;
    String status, id_user;
    LinearLayout ll_kajurDosen, ll_kajurMhs, ll_DPA;
    Button btnUbahProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView bgProfil = (ImageView) findViewById(R.id.IV_backgroundProfil);
        btnUbahProfil = (Button) findViewById(R.id.BT_ubahProfil);

        btnUbahProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UbahProfil();
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ulil);
        Bitmap blurredBitmap = BlurBuilder.blur(this, bitmap);
        bgProfil.setImageBitmap(blurredBitmap);

        nama = (TextView) findViewById(R.id.namaProfil);
        idUser = (TextView) findViewById(R.id.idProfil);
        jurusan = (TextView) findViewById(R.id.namaJurusan);
        dpa = (TextView) findViewById(R.id.namaDPA);
        kajurMhs = (TextView) findViewById(R.id.namaKajurMhs);
        kajurDosen = (TextView) findViewById(R.id.namaKajurDosen);

        ll_DPA = (LinearLayout) findViewById(R.id.ll_DPA);
        ll_kajurMhs = (LinearLayout) findViewById(R.id.ll_kajurMhs);
        ll_kajurDosen = (LinearLayout) findViewById(R.id.ll_kajurDosen);
        progressUpdate = (ProgressBar) findViewById(R.id.PB_progressUpdate);
        progressUpdate.setVisibility(View.GONE);

        nama.setText(Utils.sharedPreferences.getString("nama", ""));
        idUser.setText(Utils.sharedPreferences.getString("id_user", ""));
        jurusan.setText(Utils.sharedPreferences.getString("nama_jurusan", ""));
        dpa.setText(Utils.sharedPreferences.getString("nama_parent", ""));
        kajurMhs.setText(Utils.sharedPreferences.getString("nama_kajur", ""));
        kajurDosen.setText(Utils.sharedPreferences.getString("nama_parent", ""));

        id_user = Utils.sharedPreferences.getString("id_user", "");
        status = Utils.sharedPreferences.getString("status", "");

        if (status.equals("mahasiswa")) {
            ll_kajurDosen.setVisibility(View.GONE);

        } else if (status.equals("dosen")) {
            ll_kajurDosen.setVisibility(View.VISIBLE);
            ll_kajurMhs.setVisibility(View.GONE);
            ll_DPA.setVisibility(View.GONE);
        } else {
            ll_kajurDosen.setVisibility(View.GONE);
            ll_kajurMhs.setVisibility(View.GONE);
            ll_DPA.setVisibility(View.GONE);
        }

        fotoProfil = (NetworkImageView) findViewById(R.id.fotoProfil);

        ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = Utils.sharedPreferences.getString("foto", "");

        if (imageLoader != null && imageUrl != null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoProfil.setImageBitmap(response.getBitmap());
                        // progressBar.setVisibility(View.GONE);
                    }
                }
            });
            fotoProfil.setImageUrl(imageUrl, imageLoader);
        }
    }

    public void UbahProfil() {

        LayoutInflater inflater = getLayoutInflater();
        View formProfil = inflater.inflate(R.layout.form_ubah_profil, null);

        final EditText ETpassLama = (EditText) formProfil.findViewById(R.id.ET_passLama);
        final EditText ETpassBaru = (EditText) formProfil.findViewById(R.id.ET_passBaru);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(formProfil);
        alertDialog.setTitle("Ubah Data Profil Anda");

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String passLama = ETpassLama.getText().toString();
                        String passBaru = ETpassBaru.getText().toString();

                        if (passLama.isEmpty() || passBaru.isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "Lengkapi Semua Form Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {

                            if (!passLama.equals(Utils.sharedPreferences.getString("pass", ""))) {
                                Toast.makeText(ProfileActivity.this, "Maaf Password Lama Tidak Sesuai", Toast.LENGTH_SHORT).show();
                            } else {
                                progressSubmit = new ProgressDialog(ProfileActivity.this);
                                progressSubmit.setTitle("Submit Perubahan");
                                progressSubmit.setMessage("Tunggu Sebentar ...");
                                progressSubmit.show();

                                btnUbahProfil.setVisibility(View.GONE);
                                progressUpdate.setVisibility(View.VISIBLE);

                                UpdateProfil(id_user, passBaru, status);

                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void UpdateProfil(final String id_user, final String pass_baru, final String status) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.PROFILEUPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            String berhasil = obj.getString("berhasil");


                            Toast.makeText(ProfileActivity.this, "Profil berhasil diperbaharui " + berhasil, Toast.LENGTH_SHORT).show();

                            progressLogout = new ProgressDialog(ProfileActivity.this);
                            progressLogout.setTitle("Logout Aplikasi");
                            progressLogout.setMessage("Tunggu Sebentar ...");
                            progressLogout.show();

                            ProgressLogoutApps(id_user,status);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();

                        }

                        btnUbahProfil.setVisibility(View.VISIBLE);

                        // hilangkan loading progress update
                        progressUpdate.setVisibility(View.GONE);
                        progressSubmit.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressUpdate.setVisibility(View.GONE);
                        progressSubmit.dismiss();
                        Toast.makeText(ProfileActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        btnUbahProfil.setVisibility(View.GONE);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_user", id_user);
                params.put("pass_baru", pass_baru);
                params.put("status", status);

                return params;
            }
        };

        MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(postRequest);
    }


    public void ProgressLogoutApps (final String id_user, final String status){

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);
                            String logout = obj.getString("Logout");

                            if (logout.equals("sukses")){

                                Utils.clearPreference();
                                Intent keLogin = new Intent(ProfileActivity.this,LoginActivity.class);
                                keLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(keLogin);
                                Toast.makeText(ProfileActivity.this, "Logout " + logout, Toast.LENGTH_LONG).show();

                            }else {

                                Toast.makeText(ProfileActivity.this, "Logout " + logout, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        progressLogout.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        Toast.makeText(ProfileActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                        progressLogout.dismiss();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_user", id_user);
                params.put("status", status);

                return params;
            }
        };

        MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(postRequest);
    }

}

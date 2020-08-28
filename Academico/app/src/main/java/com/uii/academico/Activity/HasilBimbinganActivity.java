package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class HasilBimbinganActivity extends AppCompatActivity {

    TextView tanggal, waktu, lokasi, topik, hasilBimbingan;
    String tanggalnya, waktunya, lokasinya, topiknya, hasilBimbingannya, id_riwayat, status;

    ProgressDialog progressSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_bimbingan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tanggal = (TextView) findViewById(R.id.tanggal_hasilBimbingan);
        waktu = (TextView) findViewById(R.id.waktu_hasilBimbingan);
        lokasi = (TextView) findViewById(R.id.lokasi_hasilBimbingan);
        topik = (TextView) findViewById(R.id.topik_hasilBimbingan);
        hasilBimbingan = (TextView) findViewById(R.id.teks_hasilBimbingan);

        tanggalnya = getIntent().getExtras().getString("tanggal");
        waktunya = getIntent().getExtras().getString("waktu");
        lokasinya = getIntent().getExtras().getString("lokasi");
        topiknya = getIntent().getExtras().getString("topik");
        hasilBimbingannya = getIntent().getExtras().getString("hasil_bimbingan");
        id_riwayat = getIntent().getExtras().getString("id_riwayat");
        status = getIntent().getExtras().getString("status_user");

        tanggal.setText(tanggalnya);
        waktu.setText(waktunya);
        lokasi.setText(lokasinya);
        topik.setText(topiknya);
        hasilBimbingan.setText(hasilBimbingannya);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHasilBimbingan(id_riwayat);
            }
        });

        if (status.equals("mahasiswa") || status.equals("admin_jurusan")){

            fab.setVisibility(View.GONE);

        }


    }
    public void SetHasilBimbingan(final String id_riwayatBimbingan) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ubah Keterangan Hasil Bimbingan");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik keterangan di sini ...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String isiKeterangan = input.getText().toString();

                        if (isiKeterangan.isEmpty()) {
                            Toast.makeText(HasilBimbinganActivity.this, "Isi Hasil Bimbingan Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(HasilBimbinganActivity.this);
                            progressSubmit.setTitle("Submit Hasil Bimbingan");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            PostEditHasilBimbingan(id_riwayatBimbingan, isiKeterangan);
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


    public void PostEditHasilBimbingan(final String id_riwayat, final String hasil_bimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONFIRMASIHASILBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("konfirmasi");

                            progressSubmit.dismiss();

                            Toast.makeText(HasilBimbinganActivity.this, "Konfirmasi " + konfirmasi, Toast.LENGTH_LONG).show();


                            onBackPressed();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HasilBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressSubmit.dismiss();
                        Toast.makeText(HasilBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_riwayat", id_riwayat);
                params.put("konfirm_mhs", "1");
                params.put("konfirm_dosen", "1");
                params.put("hasil_bimbingan", hasil_bimbingan);

                return params;
            }
        };

        MySingleton.getInstance(HasilBimbinganActivity.this).addToRequestQueue(postRequest);
    }

}

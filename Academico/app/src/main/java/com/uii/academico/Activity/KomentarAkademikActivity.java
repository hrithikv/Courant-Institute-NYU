package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class KomentarAkademikActivity extends AppCompatActivity {

    String idDataAkademik, semester, ip, sks, kp, ta, cuti, btaq, kkn, cept, kegiatan, komentar, status;
    TextView vSemester, vIp, vSks, vKp, vTa, vCuti, vBtaq, vKkn, vCept, vKegiatan, vKomentar;

    ProgressDialog progressSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentar_akademik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Utils.init(this);
        status = Utils.sharedPreferences.getString("status", "");

        idDataAkademik = getIntent().getExtras().getString("id_dataAkademik");
        ip = getIntent().getExtras().getString("ip");
        semester = getIntent().getExtras().getString("semester");
        sks = getIntent().getExtras().getString("sks");
        kp = getIntent().getExtras().getString("kp");
        ta = getIntent().getExtras().getString("ta");
        cuti = getIntent().getExtras().getString("cuti");
        btaq = getIntent().getExtras().getString("btaq");
        kkn = getIntent().getExtras().getString("kkn");
        cept = getIntent().getExtras().getString("cept");
        kegiatan = getIntent().getExtras().getString("kegiatan");
        komentar = getIntent().getExtras().getString("komentar");

        vSemester = (TextView) findViewById(R.id.komentar_Semester);
        vIp = (TextView) findViewById(R.id.komentar_IP);
        vSks = (TextView) findViewById(R.id.komentar_SKS);
        vKp = (TextView) findViewById(R.id.komentar_KP);
        vTa = (TextView) findViewById(R.id.komentar_TA);
        vCuti = (TextView) findViewById(R.id.komentar_Cuti);
        vBtaq = (TextView) findViewById(R.id.komentar_BTAQ);
        vKkn = (TextView) findViewById(R.id.komentar_KKN);
        vCept = (TextView) findViewById(R.id.komentar_CEPT);
        vKegiatan = (TextView) findViewById(R.id.komentar_Kegiatan);
        vKomentar = (TextView) findViewById(R.id.kolom_komentar);


        vSemester.setText(semester);
        vIp.setText(ip);
        vSks.setText(sks);
        vKp.setText(kp);
        vTa.setText(ta);
        vCuti.setText(cuti);
        vBtaq.setText(btaq);
        vKkn.setText(kkn);
        vCept.setText(cept);
        vKegiatan.setText(kegiatan);
        vKomentar.setText(komentar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetKomentarDataAkademik(idDataAkademik);
            }
        });

        if (status.equals("mahasiswa") || status.equals("admin_jurusan")){
            fab.setVisibility(View.GONE);
        }
    }

    public void SetKomentarDataAkademik(final String id_dataAkademik) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ubah Komentar Data Akademik");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik komentar di sini ...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String isiKomentar = input.getText().toString();

                        if (isiKomentar.isEmpty()) {
                            Toast.makeText(KomentarAkademikActivity.this, "Isi Komentar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(KomentarAkademikActivity.this);
                            progressSubmit.setTitle("Submit Komentar Data Akademik");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            PostKomentarDataAkademi(idDataAkademik, isiKomentar);
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

    public void PostKomentarDataAkademi(final String id_dataAkademik, final String komentar) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.VALIDASIDATAAKADEMIK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String validasi = obj.getString("validasi");

                            // hilangkan loading progress konfirmasi
                            progressSubmit.dismiss();

                            Toast.makeText(KomentarAkademikActivity.this, "validasi " + validasi, Toast.LENGTH_LONG).show();

                            onBackPressed();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(KomentarAkademikActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressSubmit.dismiss();
                        Toast.makeText(KomentarAkademikActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_dataAkademik", id_dataAkademik);
                params.put("komentar", komentar);

                return params;
            }
        };
        MySingleton.getInstance(KomentarAkademikActivity.this).addToRequestQueue(postRequest);
    }
}

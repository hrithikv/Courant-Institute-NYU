package com.uii.academico.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.R;
import com.uii.academico.Utility.BlurBuilder;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuatDataActivity extends AppCompatActivity {

    EditText et_semester, et_IP, et_SKS, et_kegiatan;
    RadioGroup radioGroupKP, radioGroupTA, radioGroupCuti, radioGroupBTAQ, radioGroupKKN, radioGroupCEPT;

    Button simpanData;

    ProgressBar progressBarSimpanData;

    String id_mhs;
    public String value_Semester, value_IP, value_SKS, value_Kegiatan, value_KP, value_TA, value_Cuti,
            value_BTAQ, value_KKN, value_CEPT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_data_akademik);

        id_mhs = Utils.sharedPreferences.getString("id_user","");

        // PANGGIL VIEW
        et_semester = (EditText) findViewById(R.id.isi_Semester);
        et_IP = (EditText) findViewById(R.id.isi_IP);
        et_SKS = (EditText) findViewById(R.id.isi_SKS);
        et_kegiatan = (EditText) findViewById(R.id.isi_Kegiatan);
        simpanData = (Button) findViewById(R.id.BT_simpan_DataAkademik);
        progressBarSimpanData = (ProgressBar) findViewById(R.id.PB_simpan_DataAkademik);

        progressBarSimpanData.setVisibility(View.GONE);

        radioGroupKP= (RadioGroup) findViewById(R.id.radioGroup_KP);
        radioGroupTA= (RadioGroup) findViewById(R.id.radioGroup_TA);
        radioGroupCuti= (RadioGroup) findViewById(R.id.radioGroup_Cuti);
        radioGroupBTAQ= (RadioGroup) findViewById(R.id.radioGroup_BTAQ);
        radioGroupKKN= (RadioGroup) findViewById(R.id.radioGroup_KKN);
        radioGroupCEPT= (RadioGroup) findViewById(R.id.radioGroup_CEPT);



        radioGroupKP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.KP_sudah:
                            value_KP = "1";
                        break;

                    case R.id.KP_belum:
                            value_KP = "0";
                        break;

                }
            }
        });


        radioGroupTA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.TA_sudah:
                        value_TA = "1";
                        break;

                    case R.id.TA_belum:
                        value_TA = "0";
                        break;

                }
            }
        });


        radioGroupCuti.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cuti_ya:
                        value_Cuti = "1";
                        break;

                    case R.id.cuti_tidak:
                        value_Cuti = "0";
                        break;

                }
            }
        });

        radioGroupBTAQ.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btaq_lulus:
                        value_BTAQ = "1";
                        break;

                    case R.id.btaq_belum:
                        value_BTAQ = "0";
                        break;

                }
            }
        });

        radioGroupKKN.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.kkn_sudah:
                        value_KKN = "1";
                        break;

                    case R.id.kkn_belum:
                        value_KKN = "0";
                        break;

                }
            }
        });

        radioGroupCEPT.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cept_lulus:
                        value_CEPT = "1";
                        break;

                    case R.id.cept_belum:
                        value_CEPT = "0";
                        break;

                }
            }
        });

        simpanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                simpanData.setVisibility(View.GONE);
                progressBarSimpanData.setVisibility(View.VISIBLE);

                value_Semester = et_semester.getText().toString();
                value_IP = et_IP.getText().toString();
                value_SKS = et_SKS.getText().toString();
                value_Kegiatan = et_kegiatan.getText().toString();

                if (!value_Semester.isEmpty() && !value_IP.isEmpty() && !value_SKS.isEmpty() && !value_Kegiatan.isEmpty()
                        && !value_KP.isEmpty() && !value_TA.isEmpty() && !value_Cuti.isEmpty()) {

                    PostDataAkademik(id_mhs, value_Semester, value_IP, value_SKS, value_Kegiatan,
                            value_KP, value_TA, value_Cuti);

                } else {
                    Toast.makeText(BuatDataActivity.this, "Mohon isi semua form", Toast.LENGTH_SHORT).show();
                    simpanData.setVisibility(View.VISIBLE);
                    progressBarSimpanData.setVisibility(View.GONE);
                }


            }
        });

    }



    public void PostDataAkademik (final String id_mhs, final String value_Semester, final String value_IP, final String value_SKS, final String value_Kegiatan,
                                  final String value_KP, final  String value_TA, final  String value_Cuti) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.BUATDATAAKADEMIK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject res = new JSONObject(response);
                            boolean jsonResponse = res.getBoolean("error");

                            if (!jsonResponse){

                                Toast.makeText(BuatDataActivity.this, "Berhasil Tersimpan", Toast.LENGTH_SHORT).show();

                                Intent keDataAkademik = new Intent(BuatDataActivity.this, DataAkademikActivity.class);
                                startActivity(keDataAkademik);



                            }else{
                                Toast.makeText(BuatDataActivity.this, "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show();
                            }

                            simpanData.setVisibility(View.VISIBLE);
                            progressBarSimpanData.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            Toast.makeText(BuatDataActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        simpanData.setVisibility(View.VISIBLE);
                        progressBarSimpanData.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(BuatDataActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                // the POST parameters:
                params.put("id_mhs", id_mhs);
                params.put("semester", value_Semester);
                params.put("ip", value_IP);
                params.put("sks", value_SKS);
                params.put("kegiatan", value_Kegiatan);
                params.put("kp", value_KP);
                params.put("ta", value_TA);
                params.put("cuti", value_Cuti);
                params.put("btaq", value_BTAQ);
                params.put("kkn", value_KKN);
                params.put("cept", value_CEPT);

                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }
}

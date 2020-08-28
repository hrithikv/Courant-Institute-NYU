package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class PostInformasiActivity extends AppCompatActivity {


    Button tombolPilihGambar;
    Button tombolUnggah;
    ImageView IV_gambarInformasi;
    TextView TV_topik, TV_deskripsi;
    LinearLayout LL_EditTopik, LL_EditDeskripsi;
    ProgressBar PB_unggahInformasi;

    String jurusanSaya;

    Bitmap bitmap;
    String gambar;

    String tempTopik, tempDeskripsi;

    int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_informasi);

        jurusanSaya = Utils.sharedPreferences.getString("id_jurusan", "");

        TV_topik = (TextView) findViewById(R.id.TV_topikInformasi);
        TV_deskripsi = (TextView) findViewById(R.id.TV_deskripsiInformasi);

        PB_unggahInformasi = (ProgressBar) findViewById(R.id.PB_unggahInfo);
        PB_unggahInformasi.setVisibility(View.GONE);

        IV_gambarInformasi  = (ImageView) findViewById(R.id.imageView);

        LL_EditTopik = (LinearLayout) findViewById(R.id.LL_EditTopik);
        LL_EditDeskripsi = (LinearLayout) findViewById(R.id.LL_EditDeskripsi);

        LL_EditTopik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTopikInformasi();
            }
        });

        LL_EditDeskripsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDeskripsiInformasi();
            }
        });

        tombolPilihGambar = (Button) findViewById(R.id.BT_pilih_gambar);
        tombolUnggah = (Button) findViewById(R.id.BT_unggah_informasi);

        tombolPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PilihGambar();
            }
        });

        tombolUnggah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String topiknya = TV_topik.getText().toString();
                String deskripsinya = TV_deskripsi.getText().toString();

                if (!topiknya.isEmpty() && !deskripsinya.isEmpty()) {
                    tombolUnggah.setVisibility(View.GONE);
                    PB_unggahInformasi.setVisibility(View.VISIBLE);
                    UnggahInformasi(topiknya, deskripsinya);
                }else {
                    Toast.makeText(PostInformasiActivity.this, "Isi topik dan deskripsi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOS);
        byte[] imageBytes = byteArrayOS.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    private void UnggahInformasi (final String topik, final  String deskripsi) {

        final ProgressDialog loading = ProgressDialog.show(this,"Mengunggah Informasi","Silahkan Tunggu ...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.UPLOADINFORMASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        Toast.makeText(PostInformasiActivity.this, "Informasi Berhasil Diunggah", Toast.LENGTH_LONG).show();

                        tombolUnggah.setVisibility(View.VISIBLE);
                        PB_unggahInformasi.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();

                        Toast.makeText(PostInformasiActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                        tombolUnggah.setVisibility(View.VISIBLE);
                        PB_unggahInformasi.setVisibility(View.GONE);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (bitmap != null) {
                    gambar = getStringImage(bitmap);
                }else {
                    gambar = "kosong";
                }
                Log.e("GAMBARNYA ", gambar);

                Map<String,String> params = new Hashtable<String, String>();

                params.put("topik", topik);
                params.put("deskripsi", deskripsi);
                params.put("gambar", gambar);
                params.put("id_jurusan",jurusanSaya); // sesuaikan dengan jurusan pada saat login

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }
    private void PilihGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri lokasiFile = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), lokasiFile);
                IV_gambarInformasi.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setTopikInformasi () {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Masukkan Topik Informasi");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik disini ...");

        input.setText(tempTopik);

        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String topiknya = input.getText().toString();
                        TV_topik.setText(topiknya);
                        tempTopik = topiknya;

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

    public void setDeskripsiInformasi () {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Masukkan Deskripsi Informasi");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik disini ...");
        input.setText(tempDeskripsi);

        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String deskripsinya = input.getText().toString();
                        TV_deskripsi.setText(deskripsinya);
                        tempDeskripsi = deskripsinya;

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
}

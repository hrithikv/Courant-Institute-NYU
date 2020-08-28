package com.uii.academico.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterChat;
import com.uii.academico.Model.ChatObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    EditText kolomPesan;
    Button kirim;
    private String pesan, idSaya, namaSaya, idKontak, namaKontak, statusPenerima, statusSaya, fotoProfil;

    BroadcastReceiver terimaBroadcast;
    List<ChatObject> itemChat = new ArrayList<>();
    ListView daftarChatBubble;

    LinearLayout ll_progress_pesan;
    ProgressBar progressBar;

    ImageLoader imageLoader = MySingleton.getInstance(ChatActivity.this).getImageLoader();

    ProgressBar loadFotoProfil;
    NetworkImageView IV_fotoProfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_progress_pesan = (LinearLayout) findViewById(R.id.LL_untuk_progres_pesan);
        progressBar = (ProgressBar) findViewById(R.id.PB_lihat_pesan);
        progressBar.setVisibility(View.VISIBLE);


        namaSaya = Utils.sharedPreferences.getString("nama", "");
        idSaya = Utils.sharedPreferences.getString("id_user", "");
        statusSaya = Utils.sharedPreferences.getString("status", "");

        idKontak = getIntent().getExtras().getString("idKontak");
        namaKontak = getIntent().getExtras().getString("kirim ke");
        statusPenerima = getIntent().getExtras().getString("statusKontak");
        fotoProfil = getIntent().getExtras().getString("fotoProfil");


        Log.i("MANA ERRORNYA ? ", ""+idKontak + " " +namaKontak +" pengirim "+statusSaya+ " penerima "+statusPenerima);

        LihatPesan(idSaya, idKontak);


        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.titlebar_custom_chat, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        abar.setCustomView(viewActionBar, params);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        TextView namaTitle = (TextView) findViewById(R.id.namaTitlebar);
        TextView idTitle = (TextView) findViewById(R.id.idTitlebar);
        IV_fotoProfil = (NetworkImageView) findViewById(R.id.fotoProfilChatPersonal);
        loadFotoProfil = (ProgressBar) findViewById(R.id.PB_loadFotoProfil);

        namaTitle.setText(namaKontak);
        idTitle.setText(idKontak);

        IV_fotoProfil.setVisibility(View.GONE);

        String imageUrl = fotoProfil;

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    loadFotoProfil.setVisibility(View.GONE);

                }
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        IV_fotoProfil.setVisibility(View.VISIBLE);
                        IV_fotoProfil.setImageBitmap(response.getBitmap());
                        loadFotoProfil.setVisibility(View.GONE);
                    }

                }
            });

            IV_fotoProfil.setImageUrl(imageUrl, imageLoader);
        }



        daftarChatBubble = (ListView) findViewById(R.id.list_pesan);

        daftarChatBubble.setDivider(null);


        kolomPesan = (EditText) findViewById(R.id.ET_pesan);

        kirim = (Button) findViewById(R.id.BT_kirim);
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String waktuAndroid = df.format(c.getTime());

                //ambil teks
                pesan = kolomPesan.getText().toString();

                if (!pesan.matches("")) {
                    KirimPesan(idSaya, idKontak, namaSaya, pesan, statusSaya, statusPenerima);

                    TampilkanPesan(pesan, idSaya, "Saya", waktuAndroid);

                    kolomPesan.setText(null);
                } else {
                    Toast.makeText(ChatActivity.this, "Ketik Pesan Dulu", Toast.LENGTH_SHORT).show();
                }
            }
        });


        terimaBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String pesannya = intent.getStringExtra("pesannya");
                String idPengirim = intent.getStringExtra("idPengirim");
                String namaPengirim = intent.getStringExtra("namaPengirim");
                String waktuPesan = intent.getStringExtra("waktu");

                TampilkanPesan(pesannya, idPengirim, namaPengirim, waktuPesan);


            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(terimaBroadcast, new IntentFilter("personal"));

    }


    public void TampilkanPesan(String pesannya, String tipe, String namaPengirim, String waktuPesan) {

        itemChat.add(new ChatObject(pesannya, tipe, namaPengirim, waktuPesan));

        AdapterChat adapterChat = new AdapterChat(ChatActivity.this, R.layout.chat_bubble, itemChat, idKontak);

        daftarChatBubble.setAdapter(adapterChat);
        adapterChat.notifyDataSetChanged();
    }


    public void KirimPesan(final String id_saya, final String id_tujuan, final String nama_saya,
                           final String pesan, final String statusPengirim, final String statusPenerima) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KIRIMPESANPERSONAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(ChatActivity.this, "Pesan Terkirim", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(ChatActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pengirim", id_saya);
                params.put("id_penerima", id_tujuan);
                params.put("nama_pengirim", nama_saya);
                params.put("pesan", pesan);
                params.put("statusPengirim", statusPengirim);
                params.put("statusPenerima", statusPenerima);


                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }


    public void LihatPesan(final String id_saya, final String id_tujuan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LIHATPESAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemChat.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                String pengirim = obj.getString("pengirim");
                                String pesan = obj.getString("pesan");
                                String tanggal = obj.getString("tanggal");

                                TampilkanPesan(pesan, pengirim, namaKontak, tanggal);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        ll_progress_pesan.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(ChatActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                        ll_progress_pesan.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pengirim", id_saya);
                params.put("id_penerima", id_tujuan);


                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }
}

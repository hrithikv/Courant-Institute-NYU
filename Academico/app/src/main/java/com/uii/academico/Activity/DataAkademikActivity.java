package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterDataAkademik;
import com.uii.academico.Model.DataAkademikObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAkademikActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterDataAkademik adapterData;
    private List<DataAkademikObject> itemDataAkademik = new ArrayList<>();

    ProgressDialog progressValidasi;
    ProgressDialog progressHapus;

    ListView listDataAkademik;
    TextView namaProfil;

    String id_mhs, status, namaMhs, imageUrl;
    NetworkImageView fotoProfil;
    ProgressBar progressBar;

    LinearLayout infoBelumAdaData;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_akademik);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent keBuatData = new Intent(DataAkademikActivity.this, BuatDataActivity.class);
                startActivity(keBuatData);
            }
        });

        status = Utils.sharedPreferences.getString("status", "");

        if (status.equals("mahasiswa")) {

            // Ambil ID user dari SP
            id_mhs = Utils.sharedPreferences.getString("id_user", "");
            namaMhs = Utils.sharedPreferences.getString("nama", "");

        } else if (status.equals("dosen") || status.equals("admin_jurusan")) {
            id_mhs = getIntent().getExtras().getString("id", "");
            namaMhs = getIntent().getExtras().getString("nama", "");
            fab.setVisibility(View.GONE);
        }

        namaProfil = (TextView) findViewById(R.id.TV_namaProfilMhs);
        namaProfil.setText(namaMhs);

        fotoProfil = (NetworkImageView) findViewById(R.id.profilAkademik);
        progressBar = (ProgressBar) findViewById(R.id.PB_profil_akademik);

        infoBelumAdaData = (LinearLayout) findViewById(R.id.belumAdaData_dataAkadmik);
        infoBelumAdaData.setVisibility(View.GONE);


        ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        if (status.equals("mahasiswa")) {
            imageUrl = Utils.sharedPreferences.getString("foto", "");
        } else if (status.equals("dosen") || status.equals("admin_jurusan")) {
            imageUrl = getIntent().getExtras().getString("foto", "");
        }


        if (imageLoader != null && imageUrl != null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoProfil.setVisibility(View.VISIBLE);
                        fotoProfil.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });

            // SET Foto Profil Mahasiswa di DataAkademikActivity
            fotoProfil.setImageUrl(imageUrl, imageLoader);
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_data_akademik);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataAkademik(id_mhs);
            }
        });


        //ListView
        listDataAkademik = (ListView) findViewById(R.id.list_data_akademik);
        adapterData = new AdapterDataAkademik(DataAkademikActivity.this, itemDataAkademik);
        listDataAkademik.setAdapter(adapterData);

        //hapus border listview
        listDataAkademik.setDivider(null);

        // Event Klik pada setiap item data akademik
        listDataAkademik.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //panggil method validasi dan input posisi item data akademik
                Validasi(position);
            }
        });

        listDataAkademik.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //panggi method hapus data akadmeik
                HapusData(position);
                return true;        // jika false maka long klik akan dianggap klik biasa (tidak ada)
            }
        });

    }


    public void RequestDataAkademik(final String id_mhs) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.DATAAKADEMIK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("DATA AKADEMIK ", response);

                        if (response.equals("[]")){
                              infoBelumAdaData.setVisibility(View.VISIBLE);
                        }


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // Agar data tidak terduplikasi
                            itemDataAkademik.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                DataAkademikObject dataAkademik = new DataAkademikObject();

                                dataAkademik.setIdDataAkademik(obj.getString("id_dataAkademik"));
                                dataAkademik.setSemester(obj.getString("Semester"));
                                dataAkademik.setIp(obj.getString("IP"));
                                dataAkademik.setSks(obj.getString("SKS"));
                                dataAkademik.setKp(obj.getString("KP"));
                                dataAkademik.setTa(obj.getString("TA"));
                                dataAkademik.setCuti(obj.getString("Cuti"));
                                dataAkademik.setBtaq(obj.getString("BTAQ"));
                                dataAkademik.setKkn(obj.getString("KKN"));
                                dataAkademik.setCept(obj.getString("CEPT"));
                                dataAkademik.setKegiatan(obj.getString("Kegiatan"));
                                dataAkademik.setValidasi_dosen(obj.getString("Validasi"));
                                dataAkademik.setKomentar(obj.getString("Komentar"));

                                //menambahkan dokumen kedalam array dokumen
                                itemDataAkademik.add(dataAkademik);

                                infoBelumAdaData.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DataAkademikActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapterData.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(DataAkademikActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_mhs", id_mhs);

                return params;
            }
        };

        MySingleton.getInstance(DataAkademikActivity.this).addToRequestQueue(postRequest);
    }


    private void Validasi(final int position) {


        String indikatorValidasi = itemDataAkademik.get(position).getValidasi_dosen();


        if (indikatorValidasi.equals("0")) {

            // JIKA INDIKATOR BERWARNA BIRU

            if (status.equals("mahasiswa")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Info");
                builder.setMessage("Data akademik belum divalidasi oleh dosen");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (status.equals("dosen")) {


                SetKomentar(itemDataAkademik.get(position).getIdDataAkademik());
            }


        } else {

            Intent keKomentar =  new Intent(DataAkademikActivity.this, KomentarAkademikActivity.class);

            keKomentar.putExtra("id_dataAkademik", itemDataAkademik.get(position).getIdDataAkademik());
            keKomentar.putExtra("ip", itemDataAkademik.get(position).getIp());
            keKomentar.putExtra("semester", itemDataAkademik.get(position).getSemester());
            keKomentar.putExtra("sks", itemDataAkademik.get(position).getSks());
            keKomentar.putExtra("kp", itemDataAkademik.get(position).getKp());
            keKomentar.putExtra("ta", itemDataAkademik.get(position).getTa());
            keKomentar.putExtra("cuti", itemDataAkademik.get(position).getCuti());
            keKomentar.putExtra("btaq", itemDataAkademik.get(position).getBtaq());
            keKomentar.putExtra("kkn", itemDataAkademik.get(position).getKkn());
            keKomentar.putExtra("cept", itemDataAkademik.get(position).getCept());
            keKomentar.putExtra("kegiatan", itemDataAkademik.get(position).getKegiatan());
            keKomentar.putExtra("komentar", itemDataAkademik.get(position).getKomentar());

            startActivity(keKomentar);

        }


    }

    public void SetKomentar(final String id_dataAkademik) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Komentar dan Validasi");

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

                        String isiKeterangan = input.getText().toString();

                        if (isiKeterangan.isEmpty()) {
                            Toast.makeText(DataAkademikActivity.this, "Isi Komentar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressValidasi = new ProgressDialog(DataAkademikActivity.this);
                            progressValidasi.setTitle("Submit Hasil Bimbingan");
                            progressValidasi.setMessage("Tunggu Sebentar ...");
                            progressValidasi.show();

                            PostKomentarDataAkademik(id_dataAkademik, isiKeterangan);
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


    public void PostKomentarDataAkademik(final String id_dataAkademik, final String komentar) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.VALIDASIDATAAKADEMIK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String validasi = obj.getString("validasi");

                            progressValidasi.dismiss();

                            Toast.makeText(DataAkademikActivity.this, "Validasi " + validasi, Toast.LENGTH_LONG).show();

                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestDataAkademik(id_mhs);
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DataAkademikActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressValidasi.dismiss();
                        Toast.makeText(DataAkademikActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_dataAkademik", id_dataAkademik);
                params.put("komentar", komentar);

                return params;
            }
        };

        MySingleton.getInstance(DataAkademikActivity.this).addToRequestQueue(postRequest);
    }


    private void HapusData(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Data");
        builder.setMessage("Apakah anda ingin menghapus data akademik ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(DataAkademikActivity.this);
                progressHapus.setTitle("Progress Hapus");
                progressHapus.setMessage("Tunggu Sebentar ...");
                progressHapus.show();

                PostHapusData(itemDataAkademik.get(position).getIdDataAkademik());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }



    public void PostHapusData(final String id_dataAkademik) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.HAPUSDATAAKADEMIK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("hapusDataAkademik");

                            progressHapus.dismiss();

                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestDataAkademik(id_mhs);
                                }
                            });


                            Toast.makeText(DataAkademikActivity.this, "Data " + konfirmasi + " dihapus", Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DataAkademikActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }

                        adapterData.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressHapus.dismiss();
                        Toast.makeText(DataAkademikActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_dataAkademik", id_dataAkademik);

                return params;
            }
        };

        MySingleton.getInstance(DataAkademikActivity.this).addToRequestQueue(postRequest);
    }


    @Override
    public void onRefresh() {
        RequestDataAkademik(id_mhs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataAkademik(id_mhs);
            }
        });
    }
}

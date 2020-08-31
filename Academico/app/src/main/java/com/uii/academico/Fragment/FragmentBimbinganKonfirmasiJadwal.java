package com.uii.academico.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterDaftarBimbingan;
import com.uii.academico.Model.DaftarBimbinganObject;
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
import java.util.Set;

public class FragmentBimbinganKonfirmasiJadwal extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private AdapterDaftarBimbingan adapter;
    private List<DaftarBimbinganObject> itemDaftarBimbingan = new ArrayList<>();

    String status, idSaya;


    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressSubmit, progressHapus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status = Utils.sharedPreferences.getString("status", "");
        idSaya = Utils.sharedPreferences.getString("id_user", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewKonfirmasiJadwal = inflater.inflate(R.layout.fragment_bimbingan_konfirmasi_jadwal, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) viewKonfirmasiJadwal.findViewById(R.id.swipe_refresh_konfirmasi);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestAgendaBimbingan(idSaya, status);
            }
        });
        ListView listRiwayat = (ListView) viewKonfirmasiJadwal.findViewById(R.id.daftar_konfirmasi);

        adapter = new AdapterDaftarBimbingan(getContext(), itemDaftarBimbingan);
        listRiwayat.setAdapter(adapter);

        listRiwayat.setDivider(null);

        listRiwayat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SetHasilBimbingan(position);
            }
        });

        listRiwayat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (status.equals("dosen")){    
                    HapusRiwayat(position);
                    return true;
                }

                return false;
            }
        });


        return viewKonfirmasiJadwal;
    }


    public void RequestAgendaBimbingan(final String idSaya, final String status) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LIHATDAFTARBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("DAFTAR BIMBINGAN ", response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemDaftarBimbingan.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                DaftarBimbinganObject daftarBimbinganObject = new DaftarBimbinganObject();

                                daftarBimbinganObject.setId_agenda(obj.getString("id_jadwal"));
                                daftarBimbinganObject.setWaktu(obj.getString("waktu_bimbingan"));
                                daftarBimbinganObject.setTanggal(obj.getString("tanggal_bimbingan"));
                                daftarBimbinganObject.setLokasi(obj.getString("lokasi_bimbingan"));
                                daftarBimbinganObject.setTopiknya(obj.getString("topik_bimbingan"));

                                daftarBimbinganObject.setNama_mhs(obj.getString("nama_mhs"));
                                daftarBimbinganObject.setId_mhs(obj.getString("nim_mhs"));
                                daftarBimbinganObject.setProfil_mhs(obj.getString("foto_mhs"));
                                itemDaftarBimbingan.add(daftarBimbinganObject);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_user", idSaya);
                params.put("status", status);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }



    public void SetHasilBimbingan(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Keterangan Hasil Bimbingan");

        final EditText input = new EditText(getContext());
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
                            Toast.makeText(getContext(), "Isi Hasil Bimbingan Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(getContext());
                            progressSubmit.setTitle("Submit Hasil Bimbingan");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            PostKonfirmasiBimbingan(idSaya, itemDaftarBimbingan.get(position).getId_agenda(), "1", "1", isiKeterangan);
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

    public void PostKonfirmasiBimbingan(String idDosen, final String id_riwayat, final String indikator_mhs, final String indikator_dosen, final String hasil_bimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONFIRMASIHASILBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("konfirmasi");

                            progressSubmit.dismiss();

                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestAgendaBimbingan(idSaya,status);
                                }
                            });


                            Toast.makeText(getContext(), "Konfirmasi " + konfirmasi, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                            progressSubmit.dismiss();
                        }

                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressSubmit.dismiss();
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_riwayat", id_riwayat);
                params.put("konfirm_mhs", indikator_mhs);
                params.put("konfirm_dosen", indikator_dosen);
                params.put("hasil_bimbingan", hasil_bimbingan);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }

    private void HapusRiwayat(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Hapus Konfirmasi Bimbingan");
        builder.setMessage("Apakah anda ingin menghapus konfirmasi bimbingan ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(getContext());
                progressHapus.setTitle("Progress Hapus");
                progressHapus.setMessage("Tunggu Sebentar ...");
                progressHapus.show();

                PostHapusRiwayat(itemDaftarBimbingan.get(position).getId_agenda());

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

    public void PostHapusRiwayat(final String id_riwayatBimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.HAPUSRIWAYATBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("hapusRiwayatBimbingan");

                            progressHapus.dismiss();

                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestAgendaBimbingan(idSaya, status);
                                }
                            });


                            Toast.makeText(getContext(), "Konfirmasi Bimbingan di batalkan", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressHapus.dismiss();
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_riwayatBimbingan", id_riwayatBimbingan);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }


    @Override
    public void onRefresh() {
        RequestAgendaBimbingan(idSaya, status);
    }

}

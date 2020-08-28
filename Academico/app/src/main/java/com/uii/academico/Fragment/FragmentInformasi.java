package com.uii.academico.Fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Activity.DetailInformasiActivity;
import com.uii.academico.Activity.PostInformasiActivity;
import com.uii.academico.Adapter.AdapterInformasi;
import com.uii.academico.Model.InformasiObject;
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

/**
 * Created by Fakhrus on 4/10/16.
 */
public class FragmentInformasi extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterInformasi adapter;
    private List<InformasiObject> itemInformasi = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshPetunjuk;

    LinearLayout petunjukInfo;

    String id_jurusan, status;

    ProgressDialog progressHapus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.init(getActivity());

        id_jurusan = Utils.sharedPreferences.getString("id_jurusan", "");
        status = Utils.sharedPreferences.getString("status", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewInformasi = inflater.inflate(R.layout.fragment_informasi, container, false);

        FloatingActionButton fab = (FloatingActionButton) viewInformasi.findViewById(R.id.fab);

        if (status.equals("mahasiswa") || status.equals("dosen")){

            // Tidak dapat mengunggah informasi
            fab.setVisibility(View.GONE);

        }else {

            // Float Button di klik utk ke Post Informasi
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent kePostInfo = new Intent(getActivity(), PostInformasiActivity.class);
                    startActivity(kePostInfo);
                }
            });

        }


        // untuk petunjuk info
        petunjukInfo = (LinearLayout) viewInformasi.findViewById(R.id.petunjuk_info);

        swipeRefreshPetunjuk = (SwipeRefreshLayout) viewInformasi.findViewById(R.id.swipe_refresh_petunjuk_Info);
        swipeRefreshPetunjuk.setOnRefreshListener(this);

        swipeRefreshPetunjuk.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshPetunjuk.setRefreshing(true);
                RequestDataInformasi(id_jurusan);
            }
        });

        // untuk refresh informasi
        swipeRefreshLayout = (SwipeRefreshLayout) viewInformasi.findViewById(R.id.swipe_refresh_info);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataInformasi(id_jurusan);
            }
        });

        //panggil listview
        ListView listInformasi = (ListView) viewInformasi.findViewById(R.id.list_informasi);

        //hapus border listview
        listInformasi.setDivider(null);


        //inisialisasi adapter dan set adapter
        adapter = new AdapterInformasi(getActivity(), itemInformasi);
        listInformasi.setAdapter(adapter);

        //fungsi klik masing2 item
        listInformasi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //lakukan sesuatu
//                Toast.makeText(getContext(), "ID "+ itemInformasi.get(position).getIdInfo()+" Topik : " + itemInformasi.get(position).getTopik(), Toast.LENGTH_SHORT).show();

                Intent keDetailInformasi = new Intent(getContext(), DetailInformasiActivity.class);
                keDetailInformasi.putExtra("urlGambarInfo", itemInformasi.get(position).getGambarInformasi());
                keDetailInformasi.putExtra("titleInfo", itemInformasi.get(position).getTopik());
                startActivity(keDetailInformasi);
            }
        });

        listInformasi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                HapusInformasi(position);
                return true;
            }
        });


        return viewInformasi;
    }


    // Refresh Swipe
    @Override
    public void onRefresh() {
        RequestDataInformasi(id_jurusan);
    }


    public void RequestDataInformasi (final String id_jurusan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.INFORMASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // Agar data tidak terduplikasi
                            itemInformasi.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                InformasiObject informasi = new InformasiObject();

                                informasi.setIdInfo(obj.getString("id_info"));
                                informasi.setTopik(obj.getString("topik"));
                                informasi.setTanggal(obj.getString("tanggal"));
                                informasi.setDeskripsi(obj.getString("deskripsi"));
                                informasi.setGambarInformasi(obj.getString("gambar"));

                                //menambahkan dokumen kedalam array dokumen
                                itemInformasi.add(informasi);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Terjadi Kesalahan Input: ", Toast.LENGTH_LONG).show();
                        }

                        // lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();

                        // tutup refresh info
                        swipeRefreshLayout.setRefreshing(false);

                        // hapus petunjuk info
                        swipeRefreshPetunjuk.setRefreshing(false);
                        swipeRefreshPetunjuk.setVisibility(View.GONE);
                        petunjukInfo.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshPetunjuk.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_jurusan", id_jurusan);

                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(postRequest);
    }


    // ================================== HAPUS INFORMASI (admin jurusan) ======================================

    private void HapusInformasi(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Hapus Informasi");
        builder.setMessage("Apakah anda ingin menghapus informasi ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(getContext());
                progressHapus.setTitle("Progress Hapus");
                progressHapus.setMessage("Tunggu Sebentar ...");
                progressHapus.show();

                // input id informasi ke method PostHapusInfo
                PostHapusInfo(itemInformasi.get(position).getIdInfo());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    // ===================================== PROSES HAPUS INFORMASI ===================================

    public void PostHapusInfo(final String id_informasi) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.HAPUSINFORMASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("hapusInfo");

                            // hilangkan loading progress konfirmasi
                            progressHapus.dismiss();

                            // reload jadwal bimbingan
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestDataInformasi(id_jurusan);
                                }
                            });


                            Toast.makeText(getContext(), "Data " + konfirmasi + " dihapus", Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
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

                // the POST parameters:
                params.put("id_informasi", id_informasi);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }


}

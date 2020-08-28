package com.uii.academico.Fragment;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Activity.ChatActivity;
import com.uii.academico.Activity.MainActivity;
import com.uii.academico.Adapter.AdapterKontak;
import com.uii.academico.Model.KontakObject;
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
public class FragmentKontak extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    AdapterKontak adapter;
    SwipeRefreshLayout swipeRefreshLayout, swipeRefreshPetunjuk;

    String idSaya, idParent, status, idJurusan;

    private List<KontakObject> itemKontak = new ArrayList<>();

    LinearLayout ll_petunjuk_kontak;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Utils.init(getContext());

        idSaya = Utils.sharedPreferences.getString("id_user", "");
        idParent = Utils.sharedPreferences.getString("id_parent", "");
        status = Utils.sharedPreferences.getString("status", "");
        idJurusan = Utils.sharedPreferences.getString("id_jurusan", "");

        Log.e("ID SAYA", idSaya);
        Log.e("ID PARENT", idParent);
        Log.e("status", status);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewKontak = inflater.inflate(R.layout.fragment_kontak, container, false);

        // Petunjuk kontak

        swipeRefreshPetunjuk = (SwipeRefreshLayout) viewKontak.findViewById(R.id.swipe_refresh_petunjuk_kontak);
        swipeRefreshPetunjuk.setOnRefreshListener(this);

        swipeRefreshPetunjuk.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataKontak(idSaya, idParent, status, idJurusan);
            }
        });

        ll_petunjuk_kontak = (LinearLayout) viewKontak.findViewById(R.id.petunjuk_kontak);


        // Refresh Kontak

        swipeRefreshLayout = (SwipeRefreshLayout) viewKontak.findViewById(R.id.swipe_refresh_kontak);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataKontak(idSaya, idParent, status, idJurusan);
            }
        });

        //panggil listview
        ListView listKontak = (ListView) viewKontak.findViewById(R.id.list_kontak);

        //hapus border listview
        listKontak.setDivider(null);

        //inisialisasi adapter dan set adapter
        adapter = new AdapterKontak(getContext(), itemKontak);
        listKontak.setAdapter(adapter);


        return viewKontak;

    }

    public void RequestDataKontak(final String idSaya, final String idParent, final String Status, final String idJurusan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONTAK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemKontak.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                KontakObject kontak = new KontakObject();

                                kontak.setNama(obj.getString("nama"));
                                kontak.setId(obj.getString("noinduk"));
                                kontak.setStatus(obj.getString("status"));
                                kontak.setUrlFotoKontak(obj.getString("foto"));

                                //menambahkan list kontak
                                itemKontak.add(kontak);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input: ", Toast.LENGTH_LONG).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);

                        swipeRefreshPetunjuk.setRefreshing(false);
                        swipeRefreshPetunjuk.setVisibility(View.GONE);
                        ll_petunjuk_kontak.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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
                params.put("id", idSaya);
                params.put("id_parent", idParent);
                params.put("status", Status);
                params.put("id_jurusan", idJurusan);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);

    }


    @Override
    public void onRefresh() {
        RequestDataKontak(idSaya, idParent, status, idJurusan);
    }


//    ========================  BUAT SEARCH VIEW PER FRAGMENT ============================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.toString());
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                          }
                                      }
        );
    }



}

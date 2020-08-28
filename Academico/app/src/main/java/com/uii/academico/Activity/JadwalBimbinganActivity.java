package com.uii.academico.Activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterBimbingan;
import com.uii.academico.Model.BimbinganObject;
import com.uii.academico.R;
import com.uii.academico.Utility.BlurBuilder;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JadwalBimbinganActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener{

    String idSaya, status, idJurusan;

    AdapterBimbingan adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    SearchView searchView;

    private List<BimbinganObject> itemBimbingan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_bimbingan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView bgDataMhs = (ImageView) findViewById(R.id.backgroundMhsJadwalbimbingan);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ulil2);
        Bitmap blurredBitmap = BlurBuilder.blur(this, bitmap);
        bgDataMhs.setImageBitmap(blurredBitmap);


        LinearLayout trigerAgendaBImbingan = (LinearLayout) findViewById(R.id.lihat_semua_jadwal);
        trigerAgendaBImbingan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keAgendaBimbingan = new Intent(JadwalBimbinganActivity.this, LogBimbinganActivity.class);
                startActivity(keAgendaBimbingan);
            }
        });

        Utils.init(this);

        idSaya = Utils.sharedPreferences.getString("id_user", "");
        status = Utils.sharedPreferences.getString("status","");
        idJurusan = Utils.sharedPreferences.getString("id_jurusan","");


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_jadwal_bimbingan);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataBimbingan(idSaya,status,idJurusan);
            }
        });
        ListView listBimbingan = (ListView) findViewById(R.id.list_mahasiswa_bimbingan);

        listBimbingan.setDivider(null);

        adapter = new AdapterBimbingan(this, itemBimbingan, "Jadwal Bimbingan");
        listBimbingan.setAdapter(adapter);
    }



    public void RequestDataBimbingan(final String idSaya, final String status, final String idJurusan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.BIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemBimbingan.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                BimbinganObject bimbingan = new BimbinganObject();

                                bimbingan.setNama(obj.getString("nama"));
                                bimbingan.setId(obj.getString("noinduk"));
                                bimbingan.setUrlFoto(obj.getString("foto"));

                                itemBimbingan.add(bimbingan);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JadwalBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }

                        adapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(JadwalBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id", idSaya);
                params.put("status", status);
                params.put("id_jurusan", idJurusan);

                Log.e("ID SAYA", idSaya);


                return params;
            }
        };

        MySingleton.getInstance(JadwalBimbinganActivity.this).addToRequestQueue(postRequest);
    }

    @Override
    public void onRefresh() {
        RequestDataBimbingan(idSaya, status, idJurusan);
    }

    private void setupSearchView() {

        searchView.setIconifiedByDefault(true);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());

            searchView.setSearchableInfo(info);
        }

        searchView.setOnQueryTextListener(this);

    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        setupSearchView();

        return true;

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

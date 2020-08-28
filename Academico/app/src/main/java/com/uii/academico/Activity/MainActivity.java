package com.uii.academico.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.PagerAdapter;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    TextView nama, nomor;
    NetworkImageView fotoProfil;
    ProgressBar progressBar;
    ProgressDialog progressLogout;

    String status, id_user, nama_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.init(this);

        status = Utils.sharedPreferences.getString("status", "");
        id_user = Utils.sharedPreferences.getString("id_user","");
        nama_user = Utils.sharedPreferences.getString("nama", "");


        setupNavigationDrawer();

        setupViewPager();

        setupTabLayout();

        setupTabIcons();


        View header = navigationView.getHeaderView(0);

        nama = (TextView) header.findViewById(R.id.namaprofil);
        nomor = (TextView) header.findViewById(R.id.nomor);
        fotoProfil = (NetworkImageView) header.findViewById(R.id.foto_profil);
        progressBar = (ProgressBar) header.findViewById(R.id.PB_progress_profil);


        fotoProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keProfil = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(keProfil);
            }
        });

        nomor.setText(id_user);
        nama.setText(nama_user);

        ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();

        String imageUrl = Utils.sharedPreferences.getString("foto", "");

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {

                        fotoProfil.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });


            fotoProfil.setImageUrl(imageUrl, imageLoader);
        }

    }




    private void setupNavigationDrawer(){

        //Panggil Toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (status.equals("admin_jurusan")){
            Menu menuItem = navigationView.getMenu();
            menuItem.findItem(R.id.jadwal_bimbingan).setVisible(false);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Closing Navigation Drawer on item click
                drawerLayout.closeDrawers();

                switch (item.getItemId()) {

                    case R.id.jadwal_bimbingan:

                        // Baca Status User

                        if (status.equals("mahasiswa") ){

                            Intent keRiwayatBimbingan = new Intent(getApplicationContext(), RiwayatBimbinganActivity.class);
                            startActivity(keRiwayatBimbingan);

                        }else if (status.equals("dosen") ){

                            Intent keJadwal = new Intent(getApplicationContext(), JadwalBimbinganActivity.class);
                            startActivity(keJadwal);
                        }
                        return true;

                    case R.id.data_akademik:

                        // Baca Status User
                        if (status.equals("mahasiswa") ){

                            Intent keIP = new Intent(getApplicationContext(), DataAkademikActivity.class);
                            startActivity(keIP);
                            return true;

                        }else if (status.equals("dosen") || status.equals("admin_jurusan")){

                            Intent keDataMahasiswa = new Intent(getApplicationContext(), DataMahasiswaActivity.class);
                            startActivity(keDataMahasiswa);
                            return true;
                        }

                    case R.id.logout:

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Log Out");
                        builder.setMessage("Apakah Anda Ingin Logout ?");
                        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                progressLogout = new ProgressDialog(MainActivity.this);
                                progressLogout.setTitle("Progress Logout");
                                progressLogout.setMessage("Tunggu Sebentar ...");
                                progressLogout.show();

                                ProgressLogoutApps (id_user, status);

                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    default:
//                        Toast.makeText(getApplicationContext(), "Aneh...??", Toast.LENGTH_SHORT).show();
                        return true;
                }

            }
        });

        // Inisialisasi Drawer Layout dan ActionBarToggle (icon utk menampilkan drawer)
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state
        actionBarDrawerToggle.syncState();

    }


    private void setupViewPager(){

        // Panggil dan pasang adapter ke viewpager
        viewPager = (ViewPager) findViewById(R.id.viewPagerku);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getApplicationContext()));

        // Menjaga Fragment view InMemory (tersimpan) jadi tidak selalu refresh ketika di klik tabnya
        viewPager.setOffscreenPageLimit(3);

        // Custom View
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // Ubah Title Aplikasi saat page di scroll
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("\t Kontak");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("\t Obrolan");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("\t Informasi");
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void setupTabLayout (){

        // Panggil dan pasang tablayout sesuai viewpager
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutku);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }



    private void setupTabIcons() {

        int[] tabIcons = {
                R.drawable.academico_kontak,
                R.drawable.academico_obrolan,
                R.drawable.academico_info
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }



    public void ProgressLogoutApps (final String id_user, final String status){

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject obj = new JSONObject(response);
                            String logout = obj.getString("Logout");

                            if (logout.equals("sukses")){
                                // hilangkan loading progress konfirmasi
                                progressLogout.dismiss();

                                Utils.editSP.remove("id_user");
                                Utils.editSP.remove("nama");
                                Utils.editSP.remove("pass");
                                Utils.editSP.remove("id_parent");
                                Utils.editSP.remove("status");
                                Utils.editSP.remove("foto");
                                Utils.editSP.remove("id_jurusan");
                                Utils.editSP.remove("nama_jurusan");
                                Utils.editSP.remove("nama_parent");
                                Utils.editSP.remove("nama_kajur");

                                Utils.editSP.commit();


                                Intent keLogin = new Intent(MainActivity.this,LoginActivity.class);
                                keLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(keLogin);
                                Toast.makeText(MainActivity.this, "Logout " + logout, Toast.LENGTH_LONG).show();

                            }else {

                                Toast.makeText(MainActivity.this, "Logout " + logout, Toast.LENGTH_LONG).show();
                                progressLogout.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressLogout.dismiss();
                            Toast.makeText(MainActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressLogout.dismiss();
                        Toast.makeText(MainActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id_user", id_user);
                params.put("status", status);

                return params;
            }
        };

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(postRequest);
    }

}

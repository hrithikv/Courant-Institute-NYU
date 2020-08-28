package com.uii.academico.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.DaftarBimbinganObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.List;

public class AdapterPilihanJadwal extends BaseAdapter {

    Context activity;
    List<DaftarBimbinganObject> itemDaftarBimbingan;

    ImageLoader imageLoader;

    public AdapterPilihanJadwal(Context activity, List<DaftarBimbinganObject> itemDaftarBimbingan) {
        this.activity = activity;
        this.itemDaftarBimbingan = itemDaftarBimbingan;
    }

    @Override
    public int getCount() { return itemDaftarBimbingan.size(); }

    @Override
    public Object getItem(int position) {
        return itemDaftarBimbingan.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(activity, R.layout.item_pilihan_jadwal_tatap_muka, null);

        imageLoader = MySingleton.getInstance(activity).getImageLoader();


        TextView waktu = (TextView) v.findViewById(R.id.waktu_bimbingan);
        TextView tanggal = (TextView) v.findViewById(R.id.tanggal_bimbingan);
        TextView lokasi = (TextView) v.findViewById(R.id.lokasi_bimbingan);
        ImageView warna = (ImageView) v.findViewById(R.id.indikatorWarna);
        ImageView icon = (ImageView) v.findViewById(R.id.iconIndikator);

        TextView nama = (TextView) v.findViewById(R.id.nama_mhs_agenda);
        TextView nim = (TextView) v.findViewById(R.id.nim_mhs_agenda);
        final NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.profilAgendaBimbingan);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.PB_loadProfilAgenda);

        final DaftarBimbinganObject daftarBimbinganObject = itemDaftarBimbingan.get(position);

        waktu.setText(daftarBimbinganObject.getWaktu());
        tanggal.setText(daftarBimbinganObject.getTanggal());
        lokasi.setText(daftarBimbinganObject.getLokasi());
        nama.setText(daftarBimbinganObject.getNama_mhs());
        nim.setText(daftarBimbinganObject.getId_mhs());
        String imageUrl = daftarBimbinganObject.getProfil_mhs();
        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);

                }
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        foto.setVisibility(View.VISIBLE);
                        foto.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });
            foto.setImageUrl(imageUrl, imageLoader);
        }
        return v;
    }

}

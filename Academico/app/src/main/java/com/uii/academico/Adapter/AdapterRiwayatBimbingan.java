package com.uii.academico.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Model.BimbinganObject;
import com.uii.academico.Model.RiwayatObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRiwayatBimbingan extends BaseAdapter {

    Context activity;
    List<RiwayatObject> itemRiwayat;
    String status;


    public AdapterRiwayatBimbingan(Context activity, List<RiwayatObject> itemRiwayat) {
        this.activity = activity;
        this.itemRiwayat = itemRiwayat;
    }

    @Override
    public int getCount() { return itemRiwayat.size(); }

    @Override
    public Object getItem(int position) {
        return itemRiwayat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(activity, R.layout.item_riwayat_bimbingan, null);

        TextView waktu = (TextView) v.findViewById(R.id.waktu_bimbingan);
        TextView tanggal = (TextView) v.findViewById(R.id.tanggal_bimbingan);
        TextView lokasi = (TextView) v.findViewById(R.id.lokasi_bimbingan);
        ImageView warna = (ImageView) v.findViewById(R.id.indikatorWarna);
        ImageView icon = (ImageView) v.findViewById(R.id.iconIndikator);
        TextView topiknya = (TextView) v.findViewById(R.id.TV_topik_bimbingan);

        final RiwayatObject riwayat = itemRiwayat.get(position);

        waktu.setText(riwayat.getWaktu());
        tanggal.setText(riwayat.getTanggal());
        lokasi.setText(riwayat.getLokasi());
        topiknya.setText(riwayat.getTopiknya());


        String indikatorDosen = riwayat.getKonfirm_dosen();
        String indikatorMhs = riwayat.getKonfirm_mhs();

        if (indikatorMhs.equals("1") && indikatorDosen.equals("0")){
            warna.setBackgroundColor(Color.parseColor("#ffd16c"));
            icon.setImageResource(R.drawable.sendtopic);

        }else if (indikatorDosen.equals("1")){
            warna.setBackgroundColor(Color.parseColor("#2ecc71"));
            icon.setImageResource(R.drawable.confirmed);

        }else {
            warna.setBackgroundColor(Color.parseColor("#6fbeff"));
            icon.setImageResource(R.drawable.ask);
        }

        return v;
    }

}

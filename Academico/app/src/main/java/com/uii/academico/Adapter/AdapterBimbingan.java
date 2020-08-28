package com.uii.academico.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Activity.BuatJadwalActivity;
import com.uii.academico.Activity.DataAkademikActivity;
import com.uii.academico.Activity.JadwalBimbinganActivity;
import com.uii.academico.Model.BimbinganObject;
import com.uii.academico.Model.KontakObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import java.util.ArrayList;
import java.util.List;


public class AdapterBimbingan extends BaseAdapter {

    Context activity;
    List<BimbinganObject> itemBimbingan;
    List<BimbinganObject> searchBimbingan;
    String posisiAktivitas;


    public AdapterBimbingan(Context activity, List<BimbinganObject> itemBimbingan, String posisiAktivitas) {
        this.activity = activity;
        this.itemBimbingan = itemBimbingan;
        this.posisiAktivitas = posisiAktivitas;
    }

    @Override
    public int getCount() { return itemBimbingan.size(); }

    @Override
    public Object getItem(int position) {
        return itemBimbingan.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = View.inflate(activity, R.layout.item_bimbingan, null);

        ImageLoader imageLoader = MySingleton.getInstance(activity).getImageLoader();

        final NetworkImageView fotoBimbingan = (NetworkImageView) v.findViewById(R.id.foto_bimbingan);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.PB_foto_bimbingan);


        if (posisiAktivitas.equals("Data Akademik")) {
            LinearLayout layoutBimbingan = (LinearLayout) v.findViewById(R.id.layoutBimbingan);
            layoutBimbingan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //lakukan sesuatu
                    Toast.makeText(activity, "USER : " + itemBimbingan.get(position).getNama(), Toast.LENGTH_SHORT).show();
                    Intent keDataAkademik = new Intent(activity, DataAkademikActivity.class);

                    // bawa id dan nama ke class chat
                    keDataAkademik.putExtra("id", itemBimbingan.get(position).getId());
                    keDataAkademik.putExtra("nama", itemBimbingan.get(position).getNama());
                    keDataAkademik.putExtra("foto", itemBimbingan.get(position).getUrlFoto());

                    activity.startActivity(keDataAkademik);

                }
            });
        }else {
                LinearLayout layoutBimbingan = (LinearLayout) v.findViewById(R.id.layoutBimbingan);
                layoutBimbingan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                Toast.makeText(activity, "USER : " + itemBimbingan.get(position).getNama(), Toast.LENGTH_SHORT).show();
                Intent keBuatJadwal = new Intent(activity, BuatJadwalActivity.class);

                // bawa id dan nama ke class chat
                keBuatJadwal.putExtra("id", itemBimbingan.get(position).getId());
                keBuatJadwal.putExtra("nama", itemBimbingan.get(position).getNama());
                keBuatJadwal.putExtra("foto", itemBimbingan.get(position).getUrlFoto());
                activity.startActivity(keBuatJadwal);
            }
        });
        }



        fotoBimbingan.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        BimbinganObject dataBimbingan = itemBimbingan.get(position);

        String imageUrl = dataBimbingan.getUrlFoto();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoBimbingan.setVisibility(View.VISIBLE);
                        fotoBimbingan.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });


            fotoBimbingan.setImageUrl(imageUrl, imageLoader);
        }

        TextView nama = (TextView) v.findViewById(R.id.nama);
        TextView id_user = (TextView) v.findViewById(R.id.nim);

        BimbinganObject data = itemBimbingan.get(position);

        nama.setText(data.getNama());
        id_user.setText(data.getId());

        return v;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<BimbinganObject> results = new ArrayList<BimbinganObject>();
                if (searchBimbingan == null)
                    searchBimbingan = itemBimbingan;
                if (constraint != null) {
                    if (searchBimbingan != null && searchBimbingan.size() > 0) {
                        for (final BimbinganObject bo : searchBimbingan) {
                            if (bo.getId().toLowerCase()
                                    .contains(constraint.toString())){
                                results.add(bo);
                            }else if (bo.getNama().toLowerCase().contains(constraint.toString())){
                                results.add(bo);
                            }

                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                itemBimbingan = (ArrayList<BimbinganObject>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

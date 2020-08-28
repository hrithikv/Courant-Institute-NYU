package com.uii.academico.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.ChatGroupObject;
import com.uii.academico.Model.ChatObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import java.util.List;


public class AdapterChatGroup extends ArrayAdapter<ChatGroupObject> {

    List<ChatGroupObject> isiChatGroup;
    Context context;
    int resource;
    String idPengirim;
    String idSaya = Utils.sharedPreferences.getString("id_user", "");

    ImageLoader imageLoader;

    public AdapterChatGroup(Context context, int resource, List<ChatGroupObject> isiChatGroup, String idPengirim) {
        super(context, resource, isiChatGroup);

        this.isiChatGroup = isiChatGroup;
        this.context = context;
        this.resource = resource;
        this.idPengirim = idPengirim;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.chat_bubble_group, null);

        imageLoader = MySingleton.getInstance(context).getImageLoader();

        final NetworkImageView fotoKanan = (NetworkImageView) v.findViewById(R.id.IV_fotoKananGroup);
        final NetworkImageView fotoKiri = (NetworkImageView) v.findViewById(R.id.IV_fotoKiriGroup);
;

        TextView chatKiriDiterima = (TextView) v.findViewById(R.id.chat_kiri_diterimaGroup);
        TextView namaPengirim = (TextView) v.findViewById(R.id.nama_pengirimGroup);
        TextView chatKananDikirim = (TextView) v.findViewById(R.id.chat_kanan_dikirimGroup);
        TextView waktuPengirim = (TextView) v.findViewById(R.id.waktu_pengirimGroup);
        TextView waktuSaya = (TextView) v.findViewById(R.id.waktu_sayaGroup);

        LinearLayout chatKiri = (LinearLayout) v.findViewById(R.id.LL_chatKiriGroup);
        LinearLayout chatKanan = (LinearLayout) v.findViewById(R.id.LL_chatKananGroup);


        if(isiChatGroup.get(position).getIdPengirim().equals(idSaya)){

            chatKananDikirim.setText(isiChatGroup.get(position).getPesan());
            waktuSaya.setText(isiChatGroup.get(position).getWaktuPesan());

            chatKiri.setVisibility(View.GONE);
            chatKanan.setVisibility(View.VISIBLE);

            String imageUrl = isiChatGroup.get(position).getFotoProfil();

            if(imageLoader !=null && imageUrl!=null) {
                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            fotoKanan.setVisibility(View.VISIBLE);
                            fotoKanan.setImageBitmap(response.getBitmap());
                        }

                    }
                });
                fotoKanan.setImageUrl(imageUrl, imageLoader);
            }


        }else {
            chatKiriDiterima.setText(isiChatGroup.get(position).getPesan());
            namaPengirim.setText(isiChatGroup.get(position).getNamaPengirim());
            waktuPengirim.setText(isiChatGroup.get(position).getWaktuPesan());

            chatKiri.setVisibility(View.VISIBLE);
            chatKanan.setVisibility(View.GONE);

            String imageUrl = isiChatGroup.get(position).getFotoProfil();

            if(imageLoader !=null && imageUrl!=null) {
                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  progressImageLoad.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            fotoKiri.setVisibility(View.VISIBLE);
                            fotoKiri.setImageBitmap(response.getBitmap());
                            //progressImageLoad.setVisibility(View.GONE);
                        }

                    }
                });
                fotoKiri.setImageUrl(imageUrl, imageLoader);
            }
        }

        return v;
    }
}

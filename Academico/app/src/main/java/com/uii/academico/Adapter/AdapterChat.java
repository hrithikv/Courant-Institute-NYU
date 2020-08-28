package com.uii.academico.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uii.academico.Model.ChatObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;

import java.util.List;


public class AdapterChat extends ArrayAdapter<ChatObject> {

    List<ChatObject> isiChat;
    Context context;
    int resource;
    String idKontak;
    String idSaya = Utils.sharedPreferences.getString("id_user", "");


    public AdapterChat(Context context, int resource, List<ChatObject> isiChat, String idKontak) {
        super(context, resource, isiChat);

        this.isiChat = isiChat;
        this.context = context;
        this.resource = resource;
        this.idKontak = idKontak;
    }

    public class ViewHolder{

        TextView chatKiriDiterima;
        TextView chatKananDikirim;
        TextView namaPengirim;
        TextView waktuPengirim;
        TextView waktuSaya;

        LinearLayout chatKiri, chatKanan;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.chat_bubble,null);
            holder = new ViewHolder();

            holder.chatKiriDiterima = (TextView) convertView.findViewById(R.id.chat_kiri_diterima);
            holder.namaPengirim = (TextView) convertView.findViewById(R.id.nama_pengirim);
            holder.chatKananDikirim = (TextView) convertView.findViewById(R.id.chat_kanan_dikirim);
            holder.waktuPengirim = (TextView) convertView.findViewById(R.id.waktu_pengirim);
            holder.waktuSaya = (TextView) convertView.findViewById(R.id.waktu_saya);

            holder.chatKiri = (LinearLayout) convertView.findViewById(R.id.LL_chatKiri);
            holder.chatKanan = (LinearLayout) convertView.findViewById(R.id.LL_chatKanan);

            convertView.setTag(holder);
        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        if(isiChat.get(position).getPengirimPesan().equals(idKontak)){

            holder.chatKiriDiterima.setText(isiChat.get(position).getPesan());
            holder.namaPengirim.setText(isiChat.get(position).getNamaPengirim());
            holder.waktuPengirim.setText(isiChat.get(position).getWaktuPesan());

            holder.chatKiri.setVisibility(View.VISIBLE);
            holder.chatKanan.setVisibility(View.GONE);
        }else if (isiChat.get(position).getPengirimPesan().equals(idSaya)){

            holder.chatKananDikirim.setText(isiChat.get(position).getPesan());
            holder.waktuSaya.setText(isiChat.get(position).getWaktuPesan());

            holder.chatKiri.setVisibility(View.GONE);
            holder.chatKanan.setVisibility(View.VISIBLE);
        }else {
            holder.chatKiri.setVisibility(View.GONE);
            holder.chatKanan.setVisibility(View.GONE);
        }

        return convertView;
    }
}

package com.uii.academico.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.KontakObject;
import com.uii.academico.Model.MemberObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.List;


public class AdapterMember extends BaseAdapter{

    Context activity;
    List<MemberObject> itemMember;


    public AdapterMember( Context activity, List<MemberObject> itemMember) {
        this.activity = activity;
        this.itemMember = itemMember;
    }


    @Override
    public int getCount() {
        return itemMember.size();
    }

    @Override
    public Object getItem(int position) {
        return itemMember.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(activity, R.layout.item_member, null);

        ImageLoader imageLoader = MySingleton.getInstance(activity).getImageLoader();

        TextView nama = (TextView) v.findViewById(R.id.namaMember);
        TextView id_user = (TextView) v.findViewById(R.id.idMember);

        final NetworkImageView fotoMember = (NetworkImageView) v.findViewById(R.id.IV_fotoMember);
        final ProgressBar progressMember = (ProgressBar) v.findViewById(R.id.PB_progresMember);

        fotoMember.setVisibility(View.GONE);
        progressMember.setVisibility(View.VISIBLE);

        MemberObject dataMember = itemMember.get(position);

        nama.setText(dataMember.getNamaMember());
        id_user.setText(dataMember.getIdMember());

        String imageUrl = dataMember.getProfilMember();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressMember.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoMember.setVisibility(View.VISIBLE);
                        fotoMember.setImageBitmap(response.getBitmap());
                        progressMember.setVisibility(View.GONE);
                    }

                }
            });


            fotoMember.setImageUrl(imageUrl, imageLoader);
        }

        return v;
    }
}

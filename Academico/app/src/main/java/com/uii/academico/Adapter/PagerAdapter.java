package com.uii.academico.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uii.academico.Fragment.FragmentKontak;
import com.uii.academico.Fragment.FragmentObrolan;
import com.uii.academico.Fragment.FragmentInformasi;

public class PagerAdapter extends FragmentPagerAdapter {


    public PagerAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {


        switch (position){
            case 0:
                return new FragmentKontak();
            case 1:
                return new FragmentObrolan();
            case 2:
                return new FragmentInformasi();
            default:
                return null;

        }
    }


    @Override
    public int getCount() {
        return 3;
    }


}

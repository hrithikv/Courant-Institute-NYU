package com.uii.academico.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

import com.uii.academico.Fragment.FragmentBimbinganKonfirmasiJadwal;
import com.uii.academico.Fragment.FragmentBimbinganPilihanJadwal;
import com.uii.academico.Fragment.FragmentBimbinganRiwayatTatapMuka;

public class PagerAdapterLogBimbingan extends FragmentPagerAdapter {


    public PagerAdapterLogBimbingan(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                return new FragmentBimbinganPilihanJadwal();
            case 1:
                return new FragmentBimbinganKonfirmasiJadwal();
            case 2:
                return new FragmentBimbinganRiwayatTatapMuka();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

}

package com.hadaba.retailxcafe.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hadaba.retailxcafe.TablesFragment;
import com.hadaba.retailxcafe.module.ResponseKeyValue;

import java.util.List;

/**
 * Created by LUBAIB on 22-Jul-20.
 */
public class FloorAdapter extends FragmentPagerAdapter {

    private Context myContext;
    List<ResponseKeyValue> floorresponse;

    public FloorAdapter(Context context, FragmentManager fm, List<ResponseKeyValue> floorresponse) {
        super(fm);
        myContext = context;
        this.floorresponse = floorresponse;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {

        TablesFragment tblFragment = new TablesFragment(floorresponse.get(position),position);
         return tblFragment;

    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return floorresponse.size();
    }
}


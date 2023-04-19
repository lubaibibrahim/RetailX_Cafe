package com.hadaba.retailxcafe.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.Addon.AddonResponse;

import java.util.List;


public class AddonGridAdapter extends BaseAdapter {

    Context context;
    List<AddonResponse> addonlist;
    LayoutInflater layoutInflater;

    public AddonGridAdapter(Context context, List<AddonResponse> addonlist) {
        this.context = context;
        this.addonlist = addonlist;

    }

    @Override
    public int getCount() {
        return addonlist.size();
    }

    @Override
    public AddonResponse getItem(int position) {
        return addonlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.addon_item, null);
        holder.button = rowView.findViewById(R.id.button);
        holder.button.setText(addonlist.get(position).getName());

        return rowView;
    }

    public class Holder
    {
        Button button;
    }


}

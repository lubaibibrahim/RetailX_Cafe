package com.hadaba.retailxcafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.Item.ItemResponse;

import java.util.List;


public class ItemUnitsGridAdapter extends BaseAdapter {

    Context context;
    List<ItemResponse> itemUnitlist;
    LayoutInflater layoutInflater;

    public ItemUnitsGridAdapter(Context context, List<ItemResponse> itemUnitlist) {
        this.context = context;
        this.itemUnitlist = itemUnitlist;

    }

    @Override
    public int getCount() {
        return itemUnitlist.size();
    }

    @Override
    public ItemResponse getItem(int position) {
        return itemUnitlist.get(position);
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
        holder.button.setText(itemUnitlist.get(position).getName());

        return rowView;
    }

    public class Holder
    {
        Button button;
    }


}

package com.hadaba.retailxcafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.hadaba.retailxcafe.CategoryItemActivity;
import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.ResponseKeyValue;
import java.util.List;

/**
 * Created by LUBAIB on 09-Aug-20.
 */
public class CategoryAdapter extends BaseAdapter {

    Context context;
    List<ResponseKeyValue> categorylist;
    LayoutInflater layoutInflater;


    public CategoryAdapter(Context context, List<ResponseKeyValue> categorylist) {
        this.context = context;
        this.categorylist = categorylist;
    }

    @Override
    public int getCount() {
        return categorylist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.category_item, null);
        holder.button = rowView.findViewById(R.id.button);

        holder.button.setText(categorylist.get(position).getName());
        holder.button.setTag(categorylist.get(position).getId());
        if(CategoryItemActivity.selected_category==position)
            {holder.button.setBackgroundResource(R.color.base_gray); }
        else
            {holder.button.setBackgroundResource(R.color.transparentgreen);
                holder.button.setTextColor(context.getResources().getColor(R.color.productname_color));}

        rowView.setTag(holder);
        return rowView;
    }

    public class Holder
    {
        Button button;
    }

}


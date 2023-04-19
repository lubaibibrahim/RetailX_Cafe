package com.hadaba.retailxcafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.Bill.BillPorduct;

import java.util.ArrayList;
import java.util.List;

import static com.hadaba.retailxcafe.CategoryItemActivity.billAdapter;
import static com.hadaba.retailxcafe.adapter.BillAdapter.addonpaid_list;
import static com.hadaba.retailxcafe.adapter.BillAdapter.updatePrice;

/**
 * Created by LUBAIB on 08-Sep-20.
 */

class AddedpaidAddonsAdapter extends ArrayAdapter<BillPorduct>{

    private List<BillPorduct> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView delete_btn;
    }

    public AddedpaidAddonsAdapter(List<BillPorduct> data, Context context) {
        super(context, R.layout.addon_paid_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BillPorduct dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.addon_paid_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.delete_btn=(ImageView) convertView.findViewById(R.id.delete_btn);

            viewHolder.delete_btn.setOnClickListener(view -> {
                addonpaid_list.remove(position);
                notifyDataSetChanged();

            });
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getItemName());
        return convertView;
    }
}

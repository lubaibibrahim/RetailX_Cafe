package com.hadaba.retailxcafe.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.TableActivity;
import com.hadaba.retailxcafe.module.Table.TableResponse;

import java.util.List;

/**
 * Created by LUBAIB on 22-Jul-20.
 */
public class TablesTransferAdapter extends BaseAdapter {

    Context context;
    List<TableResponse> tableresponse;
    LayoutInflater layoutInflater;
    String identification;
    int selected_invoice_pos = -1;

    public TablesTransferAdapter(Context context, List<TableResponse> tableresponse, String identification) {
        this.context = context;
        this.tableresponse = tableresponse;
        this.identification = identification;
    }

    @Override
    public int getCount() {
        return tableresponse.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.table_item, null);

        holder.table_icon = rowView.findViewById(R.id.table_icon);
        holder.tableNo = rowView.findViewById(R.id.table_no);
        holder.table = rowView.findViewById(R.id.table);
        holder.table_time = rowView.findViewById(R.id.table_time);

        TableResponse tables = tableresponse.get(position);
        holder.tableNo.setText(tables.getName());
        holder.table_time.setText(tables.getTime());

        if (tables.getStatus().equals("1")) {
            holder.table_icon.setImageResource(R.drawable.busy);
            if (tables.getId().equals(TableActivity.fromtableselected) && identification.equals("fromtable"))
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.base_gray));
            else if (tables.getId().equals(TableActivity.totableselected) && identification.equals("totable"))
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.base_gray));
            else
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.product_bg));
            holder.table_time.setVisibility(View.VISIBLE);
        } else {
            holder.table_icon.setImageResource(R.drawable.chair);
            if (tables.getId().equals(TableActivity.fromtableselected) && identification.equals("fromtable"))
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.base_gray));
            else if (tables.getId().equals(TableActivity.totableselected) && identification.equals("totable"))
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.base_gray));
            else
                holder.table.setBackgroundColor(context.getResources().getColor(R.color.normal_table));
            holder.table_time.setVisibility(View.INVISIBLE);
        }

        holder.table.setOnClickListener(view -> {
            if (identification.equals("fromtable")) {
                TableActivity.fromtableselected = tables.getId();

                selected_invoice_pos = -1;
                if (tables.getInvno().size() > 1) // invoice number more than 1 , just display for selection: >1
                {
                    Dialog dialogView = new Dialog(context);
                    dialogView.setContentView(R.layout.dialog_invoice_selection);
                    ListView InvoiceList = dialogView.findViewById(R.id.invoice_list);
                    Button split_btn = dialogView.findViewById(R.id.split_btn);
                    split_btn.setVisibility(View.GONE);

                    ArrayAdapter<String> invoiceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, tables.getInvno());
                    InvoiceList.setAdapter(invoiceAdapter);
                    InvoiceList.setOnItemClickListener((adapterView, view1, i, l) -> {
                        dialogView.cancel();
                        TableActivity.from_invoice_no =tables.getInvno().get(i) + "";

                    });
                    dialogView.show();
                } else {
                    if (!tables.getInvno().isEmpty())
                        TableActivity.from_invoice_no = tables.getInvno().get(0);
                    else
                        TableActivity.from_invoice_no ="";
                }
            }
            else
                TableActivity.totableselected = tables.getId();

            notifyDataSetChanged();
        });
        return rowView;
    }




    private static class Holder
    {
        TextView tableNo,table_time;
        LinearLayout table;
        ImageView table_icon;
    }

}


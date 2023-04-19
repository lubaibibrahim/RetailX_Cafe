package com.hadaba.retailxcafe.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hadaba.retailxcafe.CategoryItemActivity;
import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.LockResponse;
import com.hadaba.retailxcafe.module.Table.TableResponse;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LUBAIB on 22-Jul-20.
 */
public class TablesAdapter extends BaseAdapter {

    Context context;
    List<TableResponse> tableresponse;
    LayoutInflater layoutInflater;
    AppPreference appPreference;
    int selected_invoice_pos=-1;
    public TablesAdapter(Context context, List<TableResponse> tableresponse) {
        this.context = context;
        this.tableresponse = tableresponse;
        appPreference = ObjectFactory.getInstance(context).getAppPreference();
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.table_item, null);

        holder.table_icon= rowView.findViewById(R.id.table_icon);
        holder.tableNo = rowView.findViewById(R.id.table_no);
        holder.table= rowView.findViewById(R.id.table);
        holder.table_time= rowView.findViewById(R.id.table_time);

        TableResponse tables = tableresponse.get(position);
        holder.tableNo.setText(tables.getName());
        holder.table_time.setText(tables.getTime());

        if(tables.getStatus().equals("1")){
            holder.table_icon.setImageResource(R.drawable.busy);
            holder.table.setBackgroundColor(context.getResources().getColor(R.color.product_bg));
            holder.table_time.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.table_icon.setImageResource(R.drawable.chair);
            holder.table.setBackgroundColor(context.getResources().getColor(R.color.normal_table));
            holder.table_time.setVisibility(View.INVISIBLE);
        }
        holder.table.setOnClickListener(view -> {

             selected_invoice_pos=-1;
            if(tables.getInvno().size()>1) // invoice number more than 1 , just display for selection: >1
            {
                Dialog dialogView = new Dialog(context);
                dialogView.setContentView(R.layout.dialog_invoice_selection);
                ListView InvoiceList = dialogView.findViewById(R.id.invoice_list);
                Button split_btn=dialogView.findViewById(R.id.split_btn);

                split_btn.setOnClickListener(view12 -> {
                    dialogView.cancel();

                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Are you sure you want to split this table?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                selected_invoice_pos=0;
                                postTablelock(tables,selected_invoice_pos); //lock table ,before order
                            })
                            .setNegativeButton("No", null)
                            .show();

                });
                ArrayAdapter<String> invoiceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, tables.getInvno());
                InvoiceList.setAdapter(invoiceAdapter);
                InvoiceList.setOnItemClickListener((adapterView, view1, i, l) -> {
                    dialogView.cancel();
                    selected_invoice_pos=i;
                    postTablelock(tables,selected_invoice_pos); //lock table ,before order

                });
                dialogView.show();
            }
            else {
                if(!tables.getInvno().isEmpty())
                    selected_invoice_pos=0; //  default first position
                postTablelock(tables,selected_invoice_pos); //lock table ,before order
            }
        });

        return rowView;
    }

    public class Holder
    {
        TextView tableNo,table_time;
        LinearLayout table;
        ImageView table_icon;
    }
    private ProgressDialog dialog;
    private void postTablelock(TableResponse tables,int selected_invoice_pos) {
        String TabName = appPreference.getTab_name();
        dialog = new ProgressDialog(context,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("TabName", TabName);
            jsonObj_.put("TableStatus", "1");
            jsonObj_.put("TableId", tables.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());


        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<LockResponse> call;
        call = apiService.postTablelockUnlock(gsonObject);

        call.enqueue(new Callback<LockResponse>() {
            @Override
            public void onResponse(Call<LockResponse> call, Response<LockResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResult().equals("1")) {
                        Toast.makeText(context, "Table Locked.", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(context, CategoryItemActivity.class);
                        intent.putExtra("id",tables.getId());
                        intent.putExtra("name",tables.getName());
                        intent.putExtra("status",tables.getStatus());
                        intent.putExtra("time",tables.getTime());
                        if(selected_invoice_pos<0)
                            intent.putExtra("invoicenumber","");// no invoice in table
                        else
                            intent.putExtra("invoicenumber",tables.getInvno().get(selected_invoice_pos));
                        intent.putExtra("flag","table");
                        context.startActivity(intent);

                    } else {
                        Toast.makeText(context, response.body().getResultText().toUpperCase()+", please check.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<LockResponse> call, Throwable t) {
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                dialog.cancel();

            }
        });

    }
}


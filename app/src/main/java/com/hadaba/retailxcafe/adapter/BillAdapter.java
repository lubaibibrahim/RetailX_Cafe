package com.hadaba.retailxcafe.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.TableActivity;
import com.hadaba.retailxcafe.module.Addon.AddonList;
import com.hadaba.retailxcafe.module.Addon.AddonResponse;
import com.hadaba.retailxcafe.module.Bill.BillPorduct;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hadaba.retailxcafe.CategoryItemActivity.Billlist;
import static com.hadaba.retailxcafe.CategoryItemActivity.net_amount;
import static com.hadaba.retailxcafe.CategoryItemActivity.selectedInvoiceNo;
import static com.hadaba.retailxcafe.CategoryItemActivity.tax_amount;
import static com.hadaba.retailxcafe.CategoryItemActivity.total_amount;

/**
 * Created by LUBAIB on 03-Aug-20.
 */
public class BillAdapter extends ArrayAdapter<BillPorduct> {

    private List<BillPorduct> dataSet;
    Context mContext;
    GridView grid_view_free;
    GridView grid_view_extra;
    private ProgressDialog dialog;
    List<AddonResponse> addonfree;
    List<AddonResponse> addonpaid;
    AddonGridAdapter addonfreeGridAdapter;
    AddonGridAdapter addonpaidGridAdapter;
    EditText edit_extra;
    TextView item_name;
    public static List<BillPorduct> addonpaid_list;
    private ListView addedPaidaddonsList;
    Button remove_btn, item_transfer;
    private AddedpaidAddonsAdapter addedpaidAddonsAdapter;
    AppPreference appPreference;
    private String TabName, userName;
    private Dialog dialogView, addondialogView;
    private int selectedItemPosition;

    private static class ViewHolder {
        TextView txtSl_no, txtName, txtQty, txtAmount;
    }

    public BillAdapter(List<BillPorduct> data, Context context) {
        super(context, R.layout.item_bill_list, data);
        dataSet = new ArrayList<>();
        dataSet.addAll(data);
        this.mContext = context;
        appPreference = ObjectFactory.getInstance(mContext).getAppPreference();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final BillPorduct dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_bill_list, parent, false);
            viewHolder.txtSl_no = convertView.findViewById(R.id.sl_no);
            viewHolder.txtName = convertView.findViewById(R.id.name);
            viewHolder.txtQty = convertView.findViewById(R.id.qty);
            viewHolder.txtAmount = convertView.findViewById(R.id.amount);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        assert dataModel != null;
        viewHolder.txtSl_no.setText(dataModel.getSlno() + ".");
        if (!dataModel.getAddonId().equals("0"))
            viewHolder.txtName.setText("---- " + dataModel.getItemName());
        else {
            viewHolder.txtName.setText(dataModel.getItemName());
        }
        viewHolder.txtQty.setText(dataModel.getQty());
        float total;
        if (dataModel.getAddonId().equals("0"))
            total = Float.parseFloat(dataModel.getQty()) * Float.parseFloat(dataModel.getRate());
        else
            total = 1.0f * Float.parseFloat(dataModel.getRate());
        viewHolder.txtAmount.setText(total + "");


        result.setOnClickListener(view -> {
            if (dataModel.getAddonId().equals("0")) {//&& dataModel.getKotPrinted().equals("0")

                selectedItemPosition = position;
                addondialogView = new Dialog(mContext);
                addondialogView.setContentView(R.layout.addons_grid_layout);

                item_name = addondialogView.findViewById(R.id.item_name);
                grid_view_extra = addondialogView.findViewById(R.id.grid_view_extra);
                grid_view_free = addondialogView.findViewById(R.id.grid_view_free);
                edit_extra = addondialogView.findViewById(R.id.edit_extra);
                addedPaidaddonsList = addondialogView.findViewById(R.id.added_paid_addons);
                remove_btn = addondialogView.findViewById(R.id.remove_btn);
                item_transfer = addondialogView.findViewById(R.id.item_transfer);
                ImageView txtminus = addondialogView.findViewById(R.id.txtminus);
                ImageView txtplus = addondialogView.findViewById(R.id.txtplus);
                TextView edtqty = addondialogView.findViewById(R.id.edtqty);
                Button submitbtn = addondialogView.findViewById(R.id.submitbtn);

                if (dataModel.getKotPrinted().equals("0")) {
                    remove_btn.setVisibility(View.VISIBLE);
                    item_transfer.setVisibility(View.GONE);
                } else {
                    remove_btn.setVisibility(View.GONE);
                    int count = 0;
                    for (int i = 0; i < Billlist.size(); i++) {
                        if (Billlist.get(i).getBatchno().equals("0")) {
                            count = count + 1;
                        }
                    }
                    if (count == 1) {
                        item_transfer.setVisibility(View.GONE);
                    } else {
                        item_transfer.setVisibility(View.VISIBLE);
                    }
                }
                addonpaid_list = new ArrayList<>();
                //  paidAddonstoAdd = new ArrayList<>();
                //   addonfree = response.body().getFree();
                if (addonfree != null) {
                    addonfreeGridAdapter = new AddonGridAdapter(mContext, addonfree);
                    addonpaidGridAdapter = new AddonGridAdapter(mContext, addonpaid);
                    grid_view_extra.setAdapter(addonpaidGridAdapter);
                    grid_view_free.setAdapter(addonfreeGridAdapter);
                } else
                    getAddon(mContext);

                item_name.setText(dataModel.getItemName());
                edit_extra.setText(dataModel.getRemarks());
                edit_extra.setSelection(edit_extra.getText().length());
                edtqty.setText(dataModel.getQty());
                // List<BillPorduct> addonList = new ArrayList<>();
                for (int i = 0; i < dataSet.size(); i++) {
                    if (dataSet.get(i).getBatchno().equals(dataModel.getSlno())) {
                        addonpaid_list.add(dataSet.get(i));
                    }

                }
                addedpaidAddonsAdapter = new AddedpaidAddonsAdapter(addonpaid_list, mContext);
                addedPaidaddonsList.setAdapter(addedpaidAddonsAdapter);

                txtplus.setOnClickListener(v ->
                        {
                            if (addonpaid_list.size() > 0 || !edit_extra.getText().toString().isEmpty())
                                Toast.makeText(mContext, "Multiple stock not available for addon list added items", Toast.LENGTH_LONG).show();
                            else {
                                int qty = Integer.parseInt(edtqty.getText().toString()) + 1;
                                edtqty.setText(qty + "");
                                Log.e("qty changed", edtqty.getText().toString());
                            }
                        }
                );

                txtminus.setOnClickListener(v ->
                        {
                            if (Integer.parseInt(edtqty.getText().toString()) > 1) {
                                int qty = Integer.parseInt(edtqty.getText().toString()) - 1;
                                edtqty.setText(qty + "");
                                Log.e("qty changed", edtqty.getText().toString());
                            }

                        }
                );
                grid_view_extra.setOnItemClickListener((parent1, view12, position1, id) -> {
                    if (Integer.parseInt(edtqty.getText().toString()) > 1) {
                        Toast.makeText(mContext, "Multiple stock not available for addon list added items", Toast.LENGTH_LONG).show();
                    } else {
                        AddonResponse addon = addonpaid.get(position1);
                        BillPorduct bill_pro = new BillPorduct("", addon.getBarcode(), "0", "0", addon.getSp(),
                                "", addon.getName(), addon.getTaxRate(), addon.getTax(), addon.getId(), addon.getName());
                        addonpaid_list.add(bill_pro);
                        addedpaidAddonsAdapter.notifyDataSetChanged();
                    }

                });


                grid_view_free.setOnItemClickListener((parent1, view12, position1, id) -> {
                    AddonResponse addon = addonfree.get(position1);
                    String free_text = addon.getName();
                    String append_text;
                    if (edit_extra.getText().toString().equals("")) {
                        append_text = free_text;
                    } else {
                        append_text = edit_extra.getText().toString() + "," + free_text;
                    }
                    edit_extra.setText(append_text);
                    edit_extra.setSelection(edit_extra.getText().length());
                });


                submitbtn.setOnClickListener(view1 -> {
                    int slno = Integer.parseInt(dataModel.getSlno());
                    Billlist.get(position).setRemarks(edit_extra.getText().toString());
                    Billlist.get(position).setQty(edtqty.getText().toString());

                    if (addonpaid_list.size() > 0) {
                        for (Iterator<BillPorduct> i = Billlist.iterator(); i.hasNext(); ) {
                            BillPorduct value = i.next();
                            if (value.getBatchno().equals(dataModel.getSlno())) {
                                i.remove();
                            }
                        }

                        StringBuilder extra_paidon = new StringBuilder();
                        for (int i = 0; i < addonpaid_list.size(); i++) {
                            BillPorduct billaddon = addonpaid_list.get(i);
                            billaddon.setSlno("" + (slno + 1)); // addon batch number and sl no will update only after main product
                            billaddon.setBatchno(dataModel.getSlno());
                            if (Billlist.size() > (position + 1))
                                Billlist.add((position + 1), billaddon);
                            else
                                Billlist.add(billaddon);
                            slno++;
                            if (extra_paidon.toString().equals("")) {
                                extra_paidon = new StringBuilder(billaddon.getItemName());
                            } else {
                                extra_paidon.append(",").append(billaddon.getItemName());
                            }
                        }
                        //add paid addon also in remarks
                        if (edit_extra.getText().toString().isEmpty())
                            Billlist.get(position).setRemarks(extra_paidon.toString());
                        else
                            Billlist.get(position).setRemarks(edit_extra.getText().toString() + "," + extra_paidon);
                        updateSlno(position);
                        dataSet = new ArrayList<>();
                        dataSet.addAll(Billlist);
                    }
                    notifyDataSetChanged();
                    updatePrice();
                    addondialogView.dismiss();
                });

                item_transfer.setOnClickListener(view14 -> {

                    TableActivity.fromtableselected="";
                    TableActivity.totableselected="";
                    TableActivity.from_invoice_no="";
                    TableActivity.to_invoice_no="";
                    if (addondialogView != null)
                        addondialogView.dismiss();
                    int slno = Integer.parseInt(dataModel.getSlno());
                    TabName = appPreference.getTab_name();
                    userName = appPreference.getUserName("");
                    String fromInvoiceNo = selectedInvoiceNo;


                    dialogView = new Dialog(mContext);
                    dialogView.setContentView(R.layout.dialog_itemtransfer);
                    Spinner ToFloor = dialogView.findViewById(R.id.tofloor);
                    GridView to_table_grid = dialogView.findViewById(R.id.to_table_grid);
                    TextView cancel_btn = dialogView.findViewById(R.id.cancel_btn);
                    TextView submit_btn = dialogView.findViewById(R.id.submit_btn);

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, TableActivity.floor_list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ToFloor.setAdapter(dataAdapter);

                    ToFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent12, View view14, int position12, long id) {
                            if (TableActivity.table_details.size() > position12) {
                                TableMergeAdapter tablesAdapter = new TableMergeAdapter(mContext, TableActivity.table_details.get(position12), "totable");
                                to_table_grid.setAdapter(tablesAdapter);
                            } else {
                                Toast.makeText(mContext, "Selected Floor not get any table from server", Toast.LENGTH_SHORT).show();
                                dialogView.dismiss();
                            }
                        }


                        @Override
                        public void onNothingSelected(AdapterView<?> parent12) {

                        }
                    });
                    cancel_btn.setOnClickListener(v -> dialogView.dismiss());
                    submit_btn.setOnClickListener(v ->
                    {
                        if(TableActivity.totableselected.isEmpty())
                        {
                            Toast.makeText(mContext, "Please Select 'To Table' ", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(fromInvoiceNo.equals(TableActivity.to_invoice_no))
                        {
                            Toast.makeText(mContext, "Item transfer not possible to same invoice. ", Toast.LENGTH_LONG).show();
                            return;
                        }
                        postItemTransfer(fromInvoiceNo, slno + "", TableActivity.totableselected, TableActivity.to_invoice_no, TabName, userName);
                    });

                    dialogView.show();
                    Window window = dialogView.getWindow();
                    int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
                    int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.90);
                    assert window != null;
                    window.setLayout(width, height);

                });

                remove_btn.setOnClickListener(view13 -> new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to remove this product with addons?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {

                            Billlist.remove(position);
                            for (Iterator<BillPorduct> ind = Billlist.iterator(); ind.hasNext(); ) {
                                BillPorduct value = ind.next();
                                if (value.getBatchno().equals(dataModel.getSlno())) {
                                    ind.remove();
                                }
                            }
                            updateSlnoRemove();
                            dataSet = new ArrayList<>();
                            dataSet.addAll(Billlist);
                            notifyDataSetChanged();
                            updatePrice();
                            addondialogView.dismiss();
                        })
                        .show());

                addondialogView.show();
                Window window = addondialogView.getWindow();
                int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.90);
                assert window != null;
                window.setLayout(width, height);
            }
            // else
            //   Toast.makeText(mContext,"KOT printed product| Add on product , not possible to update",Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    private void postItemTransfer(String fromInvoice, String Slno, String totable, String toInvoice, String tabName, String userName) {

        dialog = new ProgressDialog(mContext,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("transfering item...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("FromInvNo", fromInvoice);
            jsonObj_.put("Slno", Slno);
            jsonObj_.put("Totable", totable);
            jsonObj_.put("ToInvNo", toInvoice);
            jsonObj_.put("tabName", tabName);
            jsonObj_.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call;
        call = apiService.postItemTransfer(gsonObject);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().toUpperCase().equals("SUCCESS")) {
                        Toast.makeText(mContext, "Successfully transfered.", Toast.LENGTH_SHORT).show();

                        Billlist.remove(selectedItemPosition);

                        for (Iterator<BillPorduct> ind = Billlist.iterator(); ind.hasNext(); ) {
                            BillPorduct value = ind.next();
                            if (value.getBatchno().equals(Slno)) {
                                ind.remove();
                            }
                        }

                        updateSlnoRemove();
                        dataSet = new ArrayList<>();
                        dataSet.addAll(Billlist);
                        notifyDataSetChanged();
                        updatePrice();
                        dialog.dismiss();
                        dialogView.dismiss();
                        // TableActivity.table_details.clear();

                    } else {
                        Toast.makeText(mContext, response.body() + ", please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }


    private void updateSlno(int position) {
        if (Billlist.size() > (position + 1))//update all slno after the changed position item
        {
            String slno = Billlist.get(position).getSlno();
            for (int i = (position + 1); i < Billlist.size(); i++) {
                BillPorduct bill = Billlist.get(i);
                bill.setSlno((i + 1) + "");
                if (!bill.getBatchno().equals("0")) {
                    bill.setBatchno(slno);
                } else {
                    slno = bill.getSlno();
                }
            }

        }
    }

    private void updateSlnoRemove() {
        String slno = "1";
        for (int i = 0; i < Billlist.size(); i++) {
            BillPorduct bill = Billlist.get(i);
            bill.setSlno((i + 1) + "");
            if (!bill.getBatchno().equals("0")) {
                bill.setBatchno(slno);
            } else {
                slno = bill.getSlno();
            }
        }


    }

    @SuppressLint("DefaultLocale")
    public static void updatePrice() {

        float totalVatAmount = 0, totalAmount = 0;
        float VatAmount, Rate;
        for (int i = 0; i < Billlist.size(); i++) {
            if (Billlist.get(i).getAddonId().equals("0")) {
                VatAmount = Float.parseFloat(Billlist.get(i).getVatAmt()) * Float.parseFloat(Billlist.get(i).getQty());
                Rate = Float.parseFloat(Billlist.get(i).getRate()) * Float.parseFloat(Billlist.get(i).getQty());
            } else {
                VatAmount = Float.parseFloat(Billlist.get(i).getVatAmt()) * 1.0f;
                Rate = Float.parseFloat(Billlist.get(i).getRate()) * 1.0f;
            }
            totalVatAmount = totalVatAmount + VatAmount;
            totalAmount = totalAmount + Rate;

        }
        tax_amount.setText(String.format("%.2f", totalVatAmount));
        total_amount.setText(String.format("%.2f", totalAmount));
        Float netAmount = totalAmount - totalVatAmount;
        net_amount.setText(String.format("%.2f", netAmount));

    }

    private void getAddon(Context context) {

        dialog = new ProgressDialog(context,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<AddonList> call;
        call = apiService.getAddon();

        call.enqueue(new Callback<AddonList>() {
            @Override
            public void onResponse(Call<AddonList> call, Response<AddonList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getPaid().isEmpty() || !response.body().getFree().isEmpty()) {
                        addonfree = response.body().getFree();
                        addonpaid = response.body().getPaid();

                        addonfreeGridAdapter = new AddonGridAdapter(context, addonfree);
                        addonpaidGridAdapter = new AddonGridAdapter(context, addonpaid);
                        grid_view_extra.setAdapter(addonpaidGridAdapter);
                        grid_view_free.setAdapter(addonfreeGridAdapter);

                    } else {
                        Toast.makeText(context, "Addon empty , please check.", Toast.LENGTH_SHORT).show();
                        //direct add to cart
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<AddonList> call, Throwable t) {
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }

}

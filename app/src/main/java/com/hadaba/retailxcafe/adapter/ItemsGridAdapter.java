package com.hadaba.retailxcafe.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hadaba.retailxcafe.R;
import com.hadaba.retailxcafe.module.Addon.AddonList;
import com.hadaba.retailxcafe.module.Addon.AddonResponse;

import com.hadaba.retailxcafe.module.Bill.BillPorduct;
import com.hadaba.retailxcafe.module.Item.ItemResponse;
import com.hadaba.retailxcafe.module.ItemUnits.ItemUnitList;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hadaba.retailxcafe.CategoryItemActivity.Billlist;
import static com.hadaba.retailxcafe.CategoryItemActivity.billAdapter;
import static com.hadaba.retailxcafe.CategoryItemActivity.bill_list;
import static com.hadaba.retailxcafe.adapter.BillAdapter.updatePrice;

/**
 * Created by LUBAIB on 20-Jul-20.
 */
public class ItemsGridAdapter extends BaseAdapter {

    Context context;
    List<ItemResponse> itemslist;
    private List<ItemResponse> Items_filter;
    LayoutInflater layoutInflater;
    GridView grid_view_free;
    GridView grid_view_extra;
    private ProgressDialog dialog;
    List<AddonResponse> addonfree;
    List<AddonResponse> addonpaid;
    AddonGridAdapter addonfreeGridAdapter;
    AddonGridAdapter addonpaidGridAdapter;
    //EditText edit_extra;
    TextView item_name;
    List<BillPorduct> addonpaid_list;
    private ListView addedPaidaddonsList;
    private Dialog unit_dialogView;
    private ItemUnitsGridAdapter itemUnitsGridAdapter;
    private List<ItemResponse> item_unit_list;
    private GridView units_grid;

    public ItemsGridAdapter(Context context, List<ItemResponse> itemslist) {
        this.context = context;
        this.itemslist = itemslist;
        Items_filter = new ArrayList<>();
        this.Items_filter.addAll(itemslist);
    }

    @Override
    public int getCount() {
        return itemslist.size();
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

        rowView = layoutInflater.inflate(R.layout.product_item, null);

        holder.button = rowView.findViewById(R.id.button);
        holder.button.setText(itemslist.get(position).getName());
        holder.button.setTag(itemslist.get(position).getId());
        rowView.setOnClickListener(view -> {

            /*******************************************************************/


            getItemUnits(itemslist.get(position));


            /**************************************************************************/

            //ItemResponse selectedPro = itemslist.get(position);

        });

        return rowView;
    }

    public class Holder {
        Button button;
    }

    public void filter(String charText) {

        itemslist.clear();
        if (charText.length() == 0) {
            itemslist.addAll(Items_filter);
        } else {
            for (ItemResponse items : Items_filter) {
                if (items.getName().toUpperCase().contains(charText.toUpperCase())) {
                    itemslist.add(items);
                }
            }
        }
        notifyDataSetChanged();

    }

    public void filterSerachAll(List<ItemResponse> Items_filterAll) {

        itemslist.clear();
        itemslist.addAll(Items_filterAll);

        notifyDataSetChanged();

    }

    /**********************************************************************************/

    private void getItemUnits(ItemResponse selectedPro) {

        dialog = new ProgressDialog(context,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        Call<ItemUnitList> call;
        call = apiService.getItemUnits(selectedPro.getId());

        call.enqueue(new Callback<ItemUnitList>() {
            @Override
            public void onResponse(Call<ItemUnitList> call, Response<ItemUnitList> response) {
                if (response.isSuccessful()) {
                    item_unit_list = response.body().getItemUnits();
                    if (item_unit_list.size() > 1) {
                        unit_dialogView = new Dialog(context);
                        unit_dialogView.setContentView(R.layout.dialog_item_units);
                        units_grid = unit_dialogView.findViewById(R.id.units_grid);
                        Button cancel_btn = unit_dialogView.findViewById(R.id.cancel_btn);
                        TextView item_name = unit_dialogView.findViewById(R.id.item_name);

                        item_name.setText(selectedPro.getName());
                        itemUnitsGridAdapter = new ItemUnitsGridAdapter(context, item_unit_list);
                        units_grid.setAdapter(itemUnitsGridAdapter);

                        units_grid.setOnItemClickListener((parent1, view12, position1, id) -> {
                            int slno = 1;
                            if (Billlist.size() > 0)
                                slno = Billlist.size() + 1;
                            BillPorduct bill_pro = new BillPorduct(slno + "", selectedPro.getId(), "0", "1", selectedPro.getSellingPrice(),
                                    "0", "", selectedPro.getTaxRate(), selectedPro.getTax(), "0", selectedPro.getName());

                            Billlist.add(bill_pro);

                            if (billAdapter == null) {
                                billAdapter = new BillAdapter(Billlist, context);
                                bill_list.setAdapter(billAdapter);
                                notifyDataSetChanged();
                            } else {
                                billAdapter.notifyDataSetChanged();
                            }
                            updatePrice();
                            unit_dialogView.dismiss();
                        });


                        cancel_btn.setOnClickListener(view1 -> unit_dialogView.dismiss());
                        unit_dialogView.show();
                    } else {
                        int slno = 1;
                        if (Billlist.size() > 0)
                            slno = Billlist.size() + 1;
                        BillPorduct bill_pro = new BillPorduct(slno + "", selectedPro.getId(), "0", "1", selectedPro.getSellingPrice(),
                                "0", "", selectedPro.getTaxRate(), selectedPro.getTax(), "0", selectedPro.getName());
                        Billlist.add(bill_pro);

                        if (billAdapter == null) {
                            billAdapter = new BillAdapter(Billlist, context);
                            bill_list.setAdapter(billAdapter);
                            notifyDataSetChanged();
                        } else {
                            billAdapter.notifyDataSetChanged();
                        }
                        updatePrice();
                    }


                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<ItemUnitList> call, Throwable t) {
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }


    /**********************************************************************************/
}

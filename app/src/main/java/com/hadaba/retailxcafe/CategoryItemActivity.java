package com.hadaba.retailxcafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hadaba.retailxcafe.adapter.BillAdapter;
import com.hadaba.retailxcafe.adapter.CategoryAdapter;
import com.hadaba.retailxcafe.module.Bill.BillPorduct;
import com.hadaba.retailxcafe.module.Bill.BillResponse;
import com.hadaba.retailxcafe.module.CategoryList;
import com.hadaba.retailxcafe.module.Item.ItemList;
import com.hadaba.retailxcafe.module.Item.ItemResponse;
import com.hadaba.retailxcafe.module.LockResponse;
import com.hadaba.retailxcafe.module.ResponseKeyValue;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hadaba.retailxcafe.adapter.BillAdapter.updatePrice;
import static com.hadaba.retailxcafe.retrofit.ApiClient.BASE_URL;

public class CategoryItemActivity extends AppCompatActivity {

    ViewPager viewPager;
    public static ListView bill_list;
    private String tableid;
    private String tableName;
    private String tableStatus;
    private String tableTime, flag;
    public static String selectedInvoiceNo;
    private GridView grid_view;
    List<ResponseKeyValue> catogerylist;
    public static List<BillPorduct> Billlist;
    private EditText searchbar;
    private CheckBox searchall_check;
    CategoryAdapter categoryAdapter_;
    ItemFragment fragment__;
    public static int selected_category = 0;
    AppPreference appPreference;
    private ProgressDialog dialog, dialog_bill;
    TextView bill_number, table_number;
    @SuppressLint("StaticFieldLeak")
    public static TextView net_amount, tax_amount, total_amount;
    public static BillAdapter billAdapter;
    private JSONArray ItemstoSendArray;
    private String TableNo;
    private String InvoiceDate;
    private String customerName = "", cusotomerPhone = "", customerAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPreference = ObjectFactory.getInstance(CategoryItemActivity.this).getAppPreference();

        if(appPreference.getOrientation().equals("LANDSCAPE")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        tableid = getIntent().getStringExtra("id");
        tableName = getIntent().getStringExtra("name");
        tableStatus = getIntent().getStringExtra("status");
        tableTime = getIntent().getStringExtra("time");
        selectedInvoiceNo = getIntent().getStringExtra("invoicenumber");
        flag = getIntent().getStringExtra("flag");

        assert flag != null;
        if (flag.equals("take_away")) {
            setTitle("Take Away");
        } else if (flag.equals("delivery")) {
            setTitle("Delivery");
        } else {
            setTitle(tableName);
        }
        TableNo = tableid;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        bill_list = findViewById(R.id.bill_list);
        Button kot_btn = findViewById(R.id.kot_btn);
        searchbar = findViewById(R.id.searchbar);
        searchall_check = findViewById(R.id.searchall_check);
        grid_view = findViewById(R.id.grid_view);
        bill_number = findViewById(R.id.bill_number);
        table_number = findViewById(R.id.table_number);
        total_amount = findViewById(R.id.total_amount);
        tax_amount = findViewById(R.id.tax_amount);
        net_amount = findViewById(R.id.net_amount);

        getCategories();
        Billlist = new ArrayList<>(); // if bill not exist , have to add
        billAdapter = null;
        if (!selectedInvoiceNo.equals("")) // invoice not exisit
            getBill(selectedInvoiceNo);

        grid_view.setOnItemClickListener((adapterView, view, i, l) -> {

            selected_category = i;
            categoryAdapter_.notifyDataSetChanged();
            ResponseKeyValue selectedCat = catogerylist.get(i);
            fragment__ = new ItemFragment(selectedCat);
            FragmentTransaction fragmentTransaction__ = getSupportFragmentManager().beginTransaction();
            fragmentTransaction__.replace(R.id.frame, fragment__).addToBackStack(null).commit();

        });
        searchall_check.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) (buttonView, isChecked) ->
                {
                    String text = searchbar.getText().toString();
                    if (isChecked) {
                        getItemsSearch(text);
                    } else
                        fragment__.itemsGridAdapter.filter(text);
                }
        );
        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String text = searchbar.getText().toString();
                if (searchall_check.isChecked()) {
                    getItemsSearch(text);
                } else
                    fragment__.itemsGridAdapter.filter(text);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        kot_btn.setOnClickListener(view -> new AlertDialog.Builder(CategoryItemActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to send this order?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {

                    ItemstoSendArray = new JSONArray();
                    for (int j = 0; j < Billlist.size(); j++) {
                        BillPorduct billProduct = Billlist.get(j);
                        JSONObject jsonObj = new JSONObject();

                        try {
                            jsonObj.put("slno", billProduct.getSlno());
                            jsonObj.put("barcode", billProduct.getBarcode());
                            jsonObj.put("kotPrinted", billProduct.getKotPrinted());
                            jsonObj.put("qty", billProduct.getQty());
                            jsonObj.put("rate", billProduct.getRate());
                            jsonObj.put("batchno", billProduct.getBatchno());
                            jsonObj.put("remarks", billProduct.getRemarks());
                            jsonObj.put("vatPer", billProduct.getVatPer());
                            jsonObj.put("vatAmt", billProduct.getVatAmt());
                            jsonObj.put("addonId", billProduct.getAddonId());
                            ItemstoSendArray.put(jsonObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ItemstoSendArray.length() == 0) {
                        Toast.makeText(CategoryItemActivity.this, "No new items are added!", Toast.LENGTH_SHORT).show();
                    } else {

                        Date currentDate = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                        InvoiceDate = formatter.format(currentDate);

                        if (flag.equals("delivery")) {

                            AlertDialog.Builder alertadd = new AlertDialog.Builder(CategoryItemActivity.this);
                            LayoutInflater factory = LayoutInflater.from(CategoryItemActivity.this);
                            final View alert_view = factory.inflate(R.layout.dialog_address, null);

                            EditText customer_name = alert_view.findViewById(R.id.customer_name);
                            EditText customer_phone = alert_view.findViewById(R.id.customer_phone);
                            EditText customer_address = alert_view.findViewById(R.id.customer_address);
                            Button submit_btn = alert_view.findViewById(R.id.submit_btn);

                            submit_btn.setOnClickListener(view1 -> {

                                customerName = customer_name.getText().toString();
                                cusotomerPhone = customer_phone.getText().toString();
                                customerAddress = customer_address.getText().toString();

                                BackgroundKotTask backgroundKotTask = new BackgroundKotTask();
                                backgroundKotTask.execute();
                            });


                            alertadd.setView(alert_view);
                            alertadd.show();

                        } else {
                            BackgroundKotTask backgroundKotTask = new BackgroundKotTask();
                            backgroundKotTask.execute();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                postTableUnlock(tableid); // unlock table and finish
            case R.id.split:
                new AlertDialog.Builder(CategoryItemActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to split this table?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            finish();
                            Intent intent = new Intent(CategoryItemActivity.this, CategoryItemActivity.class);
                            intent.putExtra("id", tableid);
                            intent.putExtra("name", tableName);
                            intent.putExtra("status", tableStatus);
                            intent.putExtra("time", tableTime);
                            intent.putExtra("invoicenumber", "");
                            intent.putExtra("flag", "table");

                            startActivity(intent);
                        })
                        .setNegativeButton("No", null)
                        .show();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        postTableUnlock(tableid); // unlock table and finish
       // TableActivity.table_details.clear();
    }

    private void postTableUnlock(String TableId) {
        String tabName = appPreference.getTab_name();
        dialog = new ProgressDialog(CategoryItemActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("TabName", tabName);
            jsonObj_.put("TableStatus", "0");
            jsonObj_.put("TableId", TableId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());


        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<LockResponse> call;
        call = apiService.postTablelockUnlock(gsonObject);

        call.enqueue(new Callback<LockResponse>() {
            @Override
            public void onResponse(Call<LockResponse> call, Response<LockResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResult().equals("1")) {
                        Toast.makeText(getApplicationContext(), "Table Unlocked.", Toast.LENGTH_SHORT).show();
                        TableActivity.async.refreshTable();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), response.body() + ", please check.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<LockResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                dialog.cancel();

            }
        });

    }


    private void getCategories() {

        dialog = new ProgressDialog(CategoryItemActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("Categories loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(CategoryItemActivity.this).create(ApiInterface.class);
        Call<CategoryList> call;
        call = apiService.getCategories();

        call.enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {


                if (response.isSuccessful()) {
                    if (!response.body().getCategories().isEmpty()) {
                        catogerylist = response.body().getCategories();
                        categoryAdapter_ = new CategoryAdapter(CategoryItemActivity.this, catogerylist);
                        grid_view.setAdapter(categoryAdapter_);

                        if (catogerylist.size() > 0) {
                            ResponseKeyValue selectedCat = catogerylist.get(0); //display first category items
                            fragment__ = new ItemFragment(selectedCat);
                            FragmentTransaction fragmentTransaction__ = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction__.replace(R.id.frame, fragment__).addToBackStack(null).commit();
                        }


                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Category empty , please check.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CategoryItemActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                Toast.makeText(CategoryItemActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }

    private void getBill(String invoice) {

        dialog_bill = new ProgressDialog(CategoryItemActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog_bill.setMessage("Bill loading...");
        dialog_bill.setCanceledOnTouchOutside(false);
        dialog_bill.show();

        ApiInterface apiService = ApiClient.getClient(CategoryItemActivity.this).create(ApiInterface.class);
        Call<BillResponse> call;
        call = apiService.getBills(invoice);

        call.enqueue(new Callback<BillResponse>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(Call<BillResponse> call, Response<BillResponse> response) {

                if (response.isSuccessful()) {

                    TableNo = response.body().getTableNo();
                    InvoiceDate = response.body().getInvDate();
                    bill_number.setText("Bill number: " + response.body().getInvno());
                    table_number.setText("Table No: " + response.body().getTableNo());
                    total_amount.setText(String.format("%.2f", Float.parseFloat(response.body().getTotAmount())));

                    if (!response.body().getSalesTab().isEmpty()) {
                        Billlist = response.body().getSalesTab();
                        billAdapter = new BillAdapter(Billlist, CategoryItemActivity.this);
                        bill_list.setAdapter(billAdapter);

                        float totalVatAmount = 0;
                        float VatAmount;
                        for (int i = 0; i < Billlist.size(); i++) {
                            if (Billlist.get(i).getAddonId().equals("0"))
                                VatAmount = Float.parseFloat(Billlist.get(i).getVatAmt()) * Float.parseFloat(Billlist.get(i).getQty());
                            else
                                VatAmount = Float.parseFloat(Billlist.get(i).getVatAmt()) * 1.0f;
                            totalVatAmount = totalVatAmount + VatAmount;
                        }
                        tax_amount.setText(String.format("%.2f", totalVatAmount));
                        Float netAmount = Float.parseFloat(response.body().getTotAmount()) - totalVatAmount;
                        net_amount.setText(String.format("%.2f", netAmount));

                        updatePrice();
                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Bill is empty , please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryItemActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog_bill.cancel();

            }

            @Override
            public void onFailure(Call<BillResponse> call, Throwable t) {
                Toast.makeText(CategoryItemActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog_bill.cancel();

            }
        });

    }

    private class BackgroundKotTask extends AsyncTask<Void, Void, JSONObject> {

        private String totalAmt;
        private String jsonData;

        @Override
        protected void onPreExecute() {
            dialog_bill = new ProgressDialog(CategoryItemActivity.this,
                    ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            dialog_bill.setMessage("Sending KOT...");
            dialog_bill.setCanceledOnTouchOutside(false);
            dialog_bill.show();

            totalAmt = total_amount.getText().toString();


            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            String urlText = BASE_URL + "api/Sales";
            try {
                JSONObject postdata = new JSONObject();
                try {
                    if (!selectedInvoiceNo.equals("")) {
                        postdata.put("invno", selectedInvoiceNo);
                    } else {
                        postdata.put("invno", "0");
                    }
                    postdata.put("invDate", InvoiceDate);
                    postdata.put("totAmount", totalAmt);
                    postdata.put("tabName", appPreference.getTab_name());
                    postdata.put("userName", appPreference.getUserName(""));

                    if (TableNo.equals("")) {
                        postdata.put("tableNo", "0");
                    } else {
                        postdata.put("tableNo", TableNo);
                    }


                    // postdata.put("tableNo", TableNo);
                    if (flag.equals("delivery")) {
                        postdata.put("delivery", "1");
                    } else {
                        postdata.put("delivery", "0");
                    }

                    if (flag.equals("take_away")) {
                        postdata.put("takeAway", "1");
                    } else {
                        postdata.put("takeAway", "0");
                    }
                    postdata.put("name", customerName);
                    postdata.put("address", customerAddress);
                    postdata.put("phoneNo", cusotomerPhone);
                    postdata.put("salesTab", ItemstoSendArray);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
                Request request = new Request.Builder().url(urlText).post(body).build();
                okhttp3.Response response = client.newCall(request).execute();
                jsonData = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (jsonData.equals("success")) {
                postTableUnlock(tableid); // unlock table and finish
               /* TableActivity.async.refreshTable();
                finish();*/

                Toast.makeText(CategoryItemActivity.this, "KOT send successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CategoryItemActivity.this, jsonData, Toast.LENGTH_LONG).show();
            }

            dialog_bill.dismiss();
        }

    }
    ProgressDialog dialog_search;
    private void getItemsSearch(String searchText) {

        dialog_search = new ProgressDialog(CategoryItemActivity.this,
                    ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog_search.setMessage("loading...");
        dialog_search.show();
        dialog_search.setIndeterminate(false);
        dialog_search.setCanceledOnTouchOutside(true);

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("SearchKey", searchText);
            jsonObj_.put("CategoryId", "");
            jsonObj_.put("isallcategories", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());

        ApiInterface apiService = ApiClient.getClient(CategoryItemActivity.this).create(ApiInterface.class);
        Call<ItemList> call;
        call = apiService.getSearchItems(gsonObject);

        call.enqueue(new Callback<ItemList>() {
            @Override
            public void onResponse(Call<ItemList> call, Response<ItemList> response) {

                dialog_search.dismiss();
                if (response.isSuccessful()) {
                    if (!response.body().getItems().isEmpty()) {
                        List<ItemResponse> itemlist = response.body().getItems();

                        fragment__.itemsGridAdapter.filterSerachAll(itemlist);
                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Product empty , please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryItemActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ItemList> call, Throwable t) {
                Toast.makeText(CategoryItemActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog_search.dismiss();

            }
        });

    }

}

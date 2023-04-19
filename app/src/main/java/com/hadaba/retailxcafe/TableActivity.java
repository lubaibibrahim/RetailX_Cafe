package com.hadaba.retailxcafe;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hadaba.retailxcafe.adapter.FloorAdapter;
import com.hadaba.retailxcafe.adapter.TableMergeAdapter;
import com.hadaba.retailxcafe.adapter.TablesTransferAdapter;
import com.hadaba.retailxcafe.interfa.AsyncResponse;
import com.hadaba.retailxcafe.module.FloorList;
import com.hadaba.retailxcafe.module.ResponseKeyValue;
import com.hadaba.retailxcafe.module.Table.TableResponse;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableActivity extends AppCompatActivity implements AsyncResponse{

    TabLayout tabLayout;
    ViewPager viewPager;
    Spinner FromFloor, ToFloor;
    private GridView from_tablesGrid, to_table_grid;
    List<ResponseKeyValue> floorresponse ;
    public static List<String> floor_list;
    FloorAdapter floorAdapter;
    boolean doubleBackToExitPressedOnce = false;
    public static List<List<TableResponse>> table_details;
     List<List<TableResponse>> table_engaged;
    Dialog dialogView;
    public static String fromtableselected,totableselected, from_invoice_no,to_invoice_no;
    //public static String merge_toinvoice_no;//merge_totableselected,merge_frominvoice_no
    AppPreference appPreference;
    public static AsyncResponse async;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appPreference = ObjectFactory.getInstance(TableActivity.this).getAppPreference();
        if(appPreference.getOrientation().equals("LANDSCAPE")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        async=TableActivity.this;
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);//avoid to refresh the tab
        Button tbl_transfer = findViewById(R.id.tbl_transfer);
        Button delivery = findViewById(R.id.delivery);
        Button tbl_merge = findViewById(R.id.tbl_merge);
        Button take_away = findViewById(R.id.take_away);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        getFloor();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        delivery.setOnClickListener(view -> {
            Intent intent=new Intent(TableActivity.this, CategoryItemActivity.class);
            intent.putExtra("id","");
            intent.putExtra("name","");
            intent.putExtra("status","");
            intent.putExtra("time","");
            intent.putExtra("invoicenumber","");
            intent.putExtra("flag","delivery");
            startActivity(intent);
        });
        take_away.setOnClickListener(view -> {
            Intent intent=new Intent(TableActivity.this, CategoryItemActivity.class);
            intent.putExtra("id","");
            intent.putExtra("name","");
            intent.putExtra("status","");
            intent.putExtra("time","");
            intent.putExtra("invoicenumber","");
            intent.putExtra("flag","take_away");
            startActivity(intent);
        });

        tbl_merge.setOnClickListener(view ->  {
            fromtableselected="";
            from_invoice_no="";
            totableselected="";
            to_invoice_no="";
            table_engaged= new ArrayList<>();
            for(int i=0;i<table_details.size();i++)
            {
                List<TableResponse> list_item = new ArrayList<>();
                for(TableResponse response :table_details.get(i) )
                {
                    if(response.getStatus().equals("1"))
                        list_item.add(response);
                }
                table_engaged.add(list_item);
            }
            dialogView = new Dialog(TableActivity.this);
            dialogView.setContentView(R.layout.dialog_tabletransfer);
            TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            FromFloor = dialogView.findViewById(R.id.fromfloor);
            ToFloor = dialogView.findViewById(R.id.tofloor);
            from_tablesGrid = dialogView.findViewById(R.id.from_table_grid);
            to_table_grid = dialogView.findViewById(R.id.to_table_grid);
            TextView cancel_btn = dialogView.findViewById(R.id.cancel_btn);
            TextView submit_btn = dialogView.findViewById(R.id.submit_btn);
            submit_btn.setText("Merge");
            dialog_title.setText("Merge Invoice");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TableActivity.this, android.R.layout.simple_spinner_item, floor_list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            FromFloor.setAdapter(dataAdapter);
            ToFloor.setAdapter(dataAdapter);

            FromFloor.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(table_engaged.size()>position) {
                        TableMergeAdapter tablesAdapter = new TableMergeAdapter(TableActivity.this, table_engaged.get(position),"fromtable");
                        from_tablesGrid.setAdapter(tablesAdapter);
                    }
                    else {
                        Toast.makeText(TableActivity.this, "Selected Floor not get any table from server", Toast.LENGTH_SHORT).show();
                        dialogView.dismiss();
                    }
                    }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ToFloor.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(table_engaged.size()>=position) {
                        TableMergeAdapter tablesAdapter = new TableMergeAdapter(TableActivity.this, table_engaged.get(position),"totable");
                        to_table_grid.setAdapter(tablesAdapter);
                    }
                    else
                        Toast.makeText(TableActivity.this, "Selected Floor not get any table from server", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cancel_btn.setOnClickListener(v -> dialogView.dismiss());
            submit_btn.setOnClickListener(v ->
                    {

                        if(from_invoice_no.isEmpty())
                        {
                            Toast.makeText(TableActivity.this, "Please Select 'From Table' invoice", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(totableselected.isEmpty())
                        {
                            Toast.makeText(TableActivity.this, "Please Select 'To Table' ", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(fromtableselected.equals(totableselected))
                        {
                            Toast.makeText(TableActivity.this, "From and To Table should not same ,please choose different.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        postTableMerge(from_invoice_no,totableselected,to_invoice_no) ;
                    }

                    );

            dialogView.show();
            Window window = dialogView.getWindow();
            assert window != null;
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        });


        tbl_transfer.setOnClickListener(view ->  {
             fromtableselected="";
             totableselected="";
             from_invoice_no="";
            to_invoice_no="";
            table_engaged= new ArrayList<>();
            for(int i=0;i<table_details.size();i++)
            {
                List<TableResponse> list_item = new ArrayList<>();
                for(TableResponse response :table_details.get(i) )
                {
                    if(response.getStatus().equals("1"))
                        list_item.add(response);
                }
                table_engaged.add(list_item);
            }
            dialogView = new Dialog(TableActivity.this);
            dialogView.setContentView(R.layout.dialog_tabletransfer);
            TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            FromFloor = dialogView.findViewById(R.id.fromfloor);
            ToFloor = dialogView.findViewById(R.id.tofloor);
            from_tablesGrid = dialogView.findViewById(R.id.from_table_grid);
            to_table_grid = dialogView.findViewById(R.id.to_table_grid);
            TextView cancel_btn = dialogView.findViewById(R.id.cancel_btn);
            TextView submit_btn = dialogView.findViewById(R.id.submit_btn);

            dialog_title.setText("Table Transfer");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TableActivity.this, android.R.layout.simple_spinner_item, floor_list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            FromFloor.setAdapter(dataAdapter);
            ToFloor.setAdapter(dataAdapter);

            FromFloor.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (table_engaged.size() > position) {
                        TablesTransferAdapter tablesAdapter = new TablesTransferAdapter(TableActivity.this, table_engaged.get(position), "fromtable");
                        from_tablesGrid.setAdapter(tablesAdapter);
                    } else {
                        Toast.makeText(TableActivity.this, "Selected Floor not get any table from server", Toast.LENGTH_SHORT).show();
                        dialogView.dismiss();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ToFloor.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(table_details.size()>=position) {
                    TablesTransferAdapter tablesAdapter = new TablesTransferAdapter(TableActivity.this, table_details.get(position),"totable");
                    to_table_grid.setAdapter(tablesAdapter);
                    }
                    else
                        Toast.makeText(TableActivity.this, "Selected Floor not get any table from server", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cancel_btn.setOnClickListener(v -> dialogView.dismiss());
            submit_btn.setOnClickListener(v ->
            {

                if(from_invoice_no.isEmpty())
                {
                    Toast.makeText(TableActivity.this, "Please Select 'From Table' invoice / Invoice not found.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(totableselected.isEmpty())
                {
                    Toast.makeText(TableActivity.this, "Please Select 'To Table' ", Toast.LENGTH_LONG).show();
                    return;
                }
                if(fromtableselected.equals(totableselected))
                {
                    Toast.makeText(TableActivity.this, "From and To Table should not same ,please choose different.", Toast.LENGTH_LONG).show();
                    return;
                }
                postTableChange(fromtableselected,totableselected, from_invoice_no);
            }
            );

            dialogView.show();
            Window window = dialogView.getWindow();
            assert window != null;
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                table_details = new ArrayList<>();
                floorAdapter = new FloorAdapter(getApplicationContext(), getSupportFragmentManager(), floorresponse);
                viewPager.setAdapter(floorAdapter);
                break;
            case R.id.logout:
                appPreference.setInitially("");
                appPreference.setLogout("yes");
                Intent i2 = new Intent(TableActivity.this, LoginActivity.class);
                startActivity(i2);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
    private ProgressDialog dialog;
    private void getFloor() {

        dialog = new ProgressDialog(TableActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<FloorList> call;
        call = apiService.getFloor();

        call.enqueue(new Callback<FloorList>() {
            @Override
            public void onResponse(Call<FloorList> call, Response<FloorList> response) {


                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getFloors().isEmpty()) {
                        floorresponse = response.body().getFloors();
                        floor_list=new ArrayList<>();
                        for(ResponseKeyValue floor : floorresponse)
                        {
                            tabLayout.addTab(tabLayout.newTab().setText(floor.getName()));
                            floor_list.add(floor.getName());
                        }
                        table_details=new ArrayList<>();
                       floorAdapter = new FloorAdapter(getApplicationContext(), getSupportFragmentManager(), floorresponse);
                        viewPager.setAdapter(floorAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Floor empty , please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<FloorList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }
    private void postTableChange(String fromtable,String totable,String fromInvoice) {

        dialog = new ProgressDialog(TableActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("Fromtable", fromtable);
            jsonObj_.put("Totable", totable);
            jsonObj_.put("InvNo", fromInvoice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());


        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<String> call;
        call = apiService.postTableChange(gsonObject);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().toUpperCase().equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Successfully transfer.", Toast.LENGTH_SHORT).show();
                        dialogView.dismiss();
                        TableActivity.table_details.clear();
                        viewPager.setAdapter(floorAdapter);//refresh tables
                    } else {
                        Toast.makeText(getApplicationContext(), response.body()+", please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }

    private void postTableMerge(String fromInvoice,String totable,String toInvoice) {

        dialog = new ProgressDialog(TableActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("FromInvNo", fromInvoice);
            jsonObj_.put("Totable", totable);
            jsonObj_.put("ToInvNo", toInvoice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());

        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
        Call<String> call;
        call = apiService.postTableMerge(gsonObject);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().toUpperCase().equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Successfully Merge the table.", Toast.LENGTH_SHORT).show();
                        dialogView.dismiss();
                        TableActivity.table_details.clear();
                        viewPager.setAdapter(floorAdapter);//refresh tables
                    } else {
                        Toast.makeText(getApplicationContext(), response.body()+", please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }

    @Override
    public void refreshTable() {
        TableActivity.table_details.clear();
        viewPager.setAdapter(floorAdapter);//refresh tables
    }
}


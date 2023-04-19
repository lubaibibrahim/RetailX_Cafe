package com.hadaba.retailxcafe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hadaba.retailxcafe.adapter.TablesAdapter;
import com.hadaba.retailxcafe.module.ResponseKeyValue;
import com.hadaba.retailxcafe.module.Table.TableList;
import com.hadaba.retailxcafe.module.Table.TableResponse;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LUBAIB on 22-Jul-20.
 */
public class TablesFragment extends Fragment {

    ResponseKeyValue responseKeyValue;
    GridView gridView;
    List<TableResponse> tableResponses;
    int position;
    public TablesFragment(ResponseKeyValue responseKeyValue,int position) {
        // Required empty public constructor
        this.responseKeyValue = responseKeyValue;
        this.position=position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_table, container, false);
        setRetainInstance(true);//avoid to refresh the tab
        gridView = (GridView) view.findViewById(R.id.grid_view);
        getFloor(responseKeyValue.getId(),position);
        return view;

    }
    private ProgressDialog dialog;
    private void getFloor(String id,int position) {

        dialog = new ProgressDialog(getActivity(),
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<TableList> call;
        call = apiService.getTables(id);

        call.enqueue(new Callback<TableList>() {
            @Override
            public void onResponse(Call<TableList> call, Response<TableList> response) {


                if (response.isSuccessful()) {
                    if (!response.body().getTables().isEmpty()) {
                        tableResponses = response.body().getTables();
                        if(TableActivity.table_details.size()>position)
                            TableActivity.table_details.add(position,tableResponses);
                        else
                            TableActivity.table_details.add(tableResponses);
                        TablesAdapter tablesAdapter = new TablesAdapter(getActivity(), tableResponses);
                        gridView.setAdapter(tablesAdapter);

                    } else {
                        Toast.makeText(getActivity(), "Table empty , please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<TableList> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }
}


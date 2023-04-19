package com.hadaba.retailxcafe;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hadaba.retailxcafe.adapter.ItemsGridAdapter;
import com.hadaba.retailxcafe.module.Item.ItemList;
import com.hadaba.retailxcafe.module.Item.ItemResponse;
import com.hadaba.retailxcafe.module.ResponseKeyValue;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LUBAIB on 05-Jul-20.
 */
public class ItemFragment extends Fragment {

    GridView gridView;
    ResponseKeyValue selectedCat;
    ItemsGridAdapter itemsGridAdapter;
    List<ItemResponse> itemlist;
    public ItemFragment(ResponseKeyValue selectedCat) {
        // Required empty public constructor
        this.selectedCat=selectedCat;
    }

    public ItemFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        getItems(selectedCat.getId());

        return view;

    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        gridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 4);
//        super.onConfigurationChanged(newConfig);
//    }
    private ProgressDialog dialog;
    private void getItems(String catid) {

        dialog = new ProgressDialog(getActivity(),
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ItemList> call;
        call = apiService.getItems(catid);

        call.enqueue(new Callback<ItemList>() {
            @Override
            public void onResponse(Call<ItemList> call, Response<ItemList> response) {


                if (response.isSuccessful()) {
                    if (!response.body().getItems().isEmpty()) {
                        itemlist = response.body().getItems();

                        itemsGridAdapter = new ItemsGridAdapter(getActivity(), itemlist);
                        gridView.setAdapter(itemsGridAdapter);

                    } else {
                        Toast.makeText(getActivity(), "Product empty , please check.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<ItemList> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.cancel();

            }
        });

    }
}

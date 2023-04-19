package com.hadaba.retailxcafe.retrofit;

import com.google.gson.JsonObject;
import com.hadaba.retailxcafe.module.Addon.AddonList;
import com.hadaba.retailxcafe.module.Bill.BillResponse;
import com.hadaba.retailxcafe.module.CategoryList;
import com.hadaba.retailxcafe.module.FloorList;
import com.hadaba.retailxcafe.module.Item.ItemList;
import com.hadaba.retailxcafe.module.ItemUnits.ItemUnitList;
import com.hadaba.retailxcafe.module.LockResponse;
import com.hadaba.retailxcafe.module.LoginResponse;
import com.hadaba.retailxcafe.module.Table.TableList;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiInterface {

    @POST("api/login_api")
    Call<LoginResponse> postLogin(@Body JsonObject  jsonBody);

    @GET("api/floor_api")
    Call<FloorList> getFloor();

    @GET("api/Tables_api/{floorid}")
    Call<TableList> getTables(@Path("floorid") String floorid);

    @GET("api/Category")
    Call<CategoryList> getCategories();

    @GET("api/items/{catid}")
    Call<ItemList> getItems(@Path("catid") String catid);

    @GET("api/Addons")
    Call<AddonList> getAddon();

    @GET("api/ItemUnit/{ItemID}")
    Call<ItemUnitList> getItemUnits(@Path("ItemID") String ItemID);

    @POST("api/tableChange")
    Call<String> postTableChange(@Body JsonObject  jsonBody);

    @POST("api/Mergetable")
    Call<String> postTableMerge(@Body JsonObject  jsonBody);

    @POST("api/ItemTransfer")
    Call<String> postItemTransfer(@Body JsonObject  jsonBody);

    @POST("api/TableStatus")
    Call<LockResponse> postTablelockUnlock(@Body JsonObject  jsonBody);

    @GET("api/Sales/{invoice}")
    Call<BillResponse> getBills(@Path("invoice") String invoice);

    @POST("api/SearchItems")
    Call<ItemList> getSearchItems(@Body JsonObject  jsonBody);
}

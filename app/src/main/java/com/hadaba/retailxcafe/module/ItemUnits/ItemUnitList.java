package com.hadaba.retailxcafe.module.ItemUnits;

import com.google.gson.annotations.SerializedName;
import com.hadaba.retailxcafe.module.Item.ItemResponse;


import java.util.List;

@SuppressWarnings("unused")
public class ItemUnitList {
    @SerializedName("items")
    private List<ItemResponse> items;


    public List<ItemResponse> getItemUnits() {
        return items;
    }

    public void setItemUnits(List<ItemResponse> items) {
        this.items = items;
    }

}

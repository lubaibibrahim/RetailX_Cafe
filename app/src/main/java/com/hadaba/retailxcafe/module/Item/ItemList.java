package com.hadaba.retailxcafe.module.Item;

import com.google.gson.annotations.SerializedName;
import com.hadaba.retailxcafe.module.ResponseKeyValue;

import java.util.List;

@SuppressWarnings("unused")
public class ItemList {
    @SerializedName("items")
    private List<ItemResponse> items;

    public List<ItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ItemResponse> items) {
        this.items = items;
    }
}

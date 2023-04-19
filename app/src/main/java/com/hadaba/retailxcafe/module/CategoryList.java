package com.hadaba.retailxcafe.module;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class CategoryList {
    @SerializedName("categories")
    private List<ResponseKeyValue> categories;

    public List<ResponseKeyValue> getCategories() {
        return categories;
    }

    public void setCategories(List<ResponseKeyValue> categories) {
        this.categories = categories;
    }
}

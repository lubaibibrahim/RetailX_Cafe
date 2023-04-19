
package com.hadaba.retailxcafe.module.Item;

import com.google.gson.annotations.SerializedName;

public class ItemResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("sellingPrice")
    private String sellingPrice;
    @SerializedName("costPrice")
    private String costPrice;
    @SerializedName("taxRate")
    private String taxRate;
    @SerializedName("tax")
    private String tax;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}

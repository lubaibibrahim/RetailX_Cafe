
package com.hadaba.retailxcafe.module.Addon;

import com.google.gson.annotations.SerializedName;

public class AddonResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("sp")
    private String sp;
    @SerializedName("taxRate")
    private String taxRate;
    @SerializedName("barcode")
    private String barcode;
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
    public String getSp() {
        return sp;
    }
    public void setSp(String sp) {
        this.sp = sp;
    }
    public String getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}

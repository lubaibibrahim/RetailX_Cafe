
package com.hadaba.retailxcafe.module.Bill;

import com.google.gson.annotations.SerializedName;

public class BillPorduct {

    @SerializedName("slno")
    private String slno;
    @SerializedName("barcode")
    private String barcode;
    @SerializedName("kotPrinted")
    private String kotPrinted;
    @SerializedName("qty")
    private String qty;
    @SerializedName("rate")
    private String rate;
    @SerializedName("batchno")
    private String batchno;
    @SerializedName("remarks")
    private String remarks;
    @SerializedName("vatPer")
    private String vatPer;
    @SerializedName("vatAmt")
    private String vatAmt;
    @SerializedName("addonId")
    private String addonId;
    @SerializedName("itemName")
    private String itemName;

    public BillPorduct(String slno, String barcode, String kotPrinted, String qty, String rate, String batchno,
                       String remarks, String vatPer, String vatAmt, String addonId, String itemName)
    {
        this.slno=slno;
        this.barcode=barcode;
        this.kotPrinted=kotPrinted;
        this.qty=qty;
        this.rate=rate;
        this.batchno=batchno;
        this.remarks=remarks;
        this.vatPer=vatPer;
        this.vatAmt=vatAmt;
        this.addonId=addonId;
        this.itemName=itemName;

    }
    public String getSlno() {
        return slno;
    }
    public String getBarcode() {
        return barcode;
    }
    public String getKotPrinted() {
        return kotPrinted;
    }
    public String getQty() {
        return qty;
    }
    public String getRate() {
        return rate;
    }
    public String getBatchno() {
        return batchno;
    }
    public String getRemarks() {
        return remarks;
    }
    public String getVatPer() {
        return vatPer;
    }
    public String getVatAmt() {
        return vatAmt;
    }
    public String getAddonId() {
        return addonId;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public void setAddonId(String addonId) {
        this.addonId = addonId;
    }
    public void setSlno(String slno) {
        this.slno = slno;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public void setKotPrinted(String kotPrinted) {
        this.kotPrinted = kotPrinted;
    }
    public void setQty(String qty) {
        this.qty = qty;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public void setVatPer(String vatPer) {
        this.vatPer = vatPer;
    }
    public void setVatAmt(String vatAmt) {
        this.vatAmt = vatAmt;
    }

}

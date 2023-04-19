
package com.hadaba.retailxcafe.module.Bill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BillResponse {

    public void setInvno(String invno) {
        this.invno = invno;
    }

    public void setInvDate(String invDate) {
        this.invDate = invDate;
    }

    public void setTotAmount(String totAmount) {
        this.totAmount = totAmount;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public void setTakeAway(String takeAway) {
        this.takeAway = takeAway;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setSalesTab(List<BillPorduct> salesTab) {
        this.salesTab = salesTab;
    }

    @SerializedName("invno")
    private String invno;
    @SerializedName("invDate")
    private String invDate;
    @SerializedName("totAmount")
    private String totAmount;
    @SerializedName("tabName")
    private String tabName;
    @SerializedName("userName")
    private String userName;
    @SerializedName("tableNo")
    private String tableNo;
    @SerializedName("takeAway")
    private String takeAway;
    @SerializedName("delivery")
    private String delivery;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("phoneNo")
    private String phoneNo;
     @SerializedName("salesTab")
    private List<BillPorduct> salesTab;

    public String getInvno() {
        return invno;
    }
    public String getInvDate() {
        return invDate;
    }
    public String getTotAmount() {
        return totAmount;
    }
    public String getTabName() {
        return tabName;
    }
    public String getUserName() {
        return userName;
    }
    public String getTableNo() {
        return tableNo;
    }
    public String getTakeAway() {
        return takeAway;
    }
    public String getDelivery() {
        return delivery;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPhoneNo() {
        return phoneNo;
    }
    public List<BillPorduct> getSalesTab() {
        return salesTab;
    }
}

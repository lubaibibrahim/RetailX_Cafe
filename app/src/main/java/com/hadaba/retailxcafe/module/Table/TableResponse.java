
package com.hadaba.retailxcafe.module.Table;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TableResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("status")
    private String status;
    @SerializedName("time")
    private String time;
    @SerializedName("invno")
    private List<String> invno;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getInvno() {
        return invno;
    }

    public void setInvno(List<String> invno) {
        this.invno = invno;
    }
}

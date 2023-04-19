package com.hadaba.retailxcafe.module.Addon;

import com.google.gson.annotations.SerializedName;
import com.hadaba.retailxcafe.module.Table.TableResponse;

import java.util.List;

@SuppressWarnings("unused")
public class AddonList {
    @SerializedName("paid")
    private List<AddonResponse> paid;
    @SerializedName("free")
    private List<AddonResponse> free;

    public List<AddonResponse> getPaid() {
        return paid;
    }

    public void setPaid(List<AddonResponse> paid) {
        this.paid = paid;
    }

    public List<AddonResponse> getFree() {
        return free;
    }

    public void setFree(List<AddonResponse> free) {
        this.free = free;
    }
}

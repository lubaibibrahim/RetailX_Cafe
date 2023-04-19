package com.hadaba.retailxcafe.module;

import com.google.gson.annotations.SerializedName;
import com.hadaba.retailxcafe.module.ResponseKeyValue;

import java.util.List;

@SuppressWarnings("unused")
public class FloorList{
    @SerializedName("floors")
    private List<ResponseKeyValue> floors;

    public List<ResponseKeyValue> getFloors() {
        return floors;
    }

    public void setFloors(List<ResponseKeyValue> floors) {
        this.floors = floors;
    }
}

package com.hadaba.retailxcafe.module.Table;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class TableList {
    @SerializedName("tables")
    private List<TableResponse> tables;

    public List<TableResponse> getTables() {
        return tables;
    }

    public void setTables(List<TableResponse> tables) {
        this.tables = tables;
    }
}

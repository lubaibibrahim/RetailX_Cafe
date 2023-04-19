
package com.hadaba.retailxcafe.module;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class LoginResponse {

    @SerializedName("result")
    private String result;
    @SerializedName("employee_id")
    private String employee_id;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }
}


package com.hadaba.retailxcafe.module;

import com.google.gson.annotations.SerializedName;

public class LockResponse {

    @SerializedName("result")
    private String result;
    @SerializedName("resultText")
    private String resultText;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}

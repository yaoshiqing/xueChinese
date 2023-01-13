package com.gjjy.googlebillinglib.entity;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;

/**
 基础处理结果
 */
public class ResultEntity{
    @BillingClient.BillingResponseCode
    private int code;
    private String debugMessage;

    public ResultEntity() {}

    public ResultEntity(BillingResult result) {
        code = result.getResponseCode();
        debugMessage = result.getDebugMessage();
    }

    @NonNull
    @Override
    public String toString() {
        return "ResultEntity{" +
                "code=" + code +
                ", debugMessage='" + debugMessage + '\'' +
                '}';
    }

    public boolean isSuccess() { return code == BillingClient.BillingResponseCode.OK; }

    @BillingClient.BillingResponseCode
    public int getCode() { return code; }
    public ResultEntity setCode(@BillingClient.BillingResponseCode int code) {
        this.code = code;
        return this;
    }

    public String getDebugMessage() { return debugMessage; }
    public ResultEntity setDebugMessage(String debugMessage ) {
        this.debugMessage = debugMessage;
        return this;
    }
}

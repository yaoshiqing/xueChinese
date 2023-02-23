package com.gjjy.googlebillinglib.entity;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;

/**
 * 基础处理结果
 */
public class BaseResultEntity {
    @BillingClient.BillingResponseCode
    private int code;
    private String debugMessage;

    public BaseResultEntity() {
    }

    public BaseResultEntity(BillingResult result) {
        code = result.getResponseCode();
        debugMessage = result.getDebugMessage();
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseResultEntity{" +
                "code=" + code +
                ", debugMessage='" + debugMessage + '\'' +
                '}';
    }

    public boolean isSuccess() {
        return code == BillingClient.BillingResponseCode.OK;
    }

    @BillingClient.BillingResponseCode
    public int getCode() {
        return code;
    }

    public BaseResultEntity setCode(@BillingClient.BillingResponseCode int code) {
        this.code = code;
        return this;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public BaseResultEntity setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
        return this;
    }
}

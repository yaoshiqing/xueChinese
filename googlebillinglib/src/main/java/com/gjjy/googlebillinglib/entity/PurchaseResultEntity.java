package com.gjjy.googlebillinglib.entity;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingResult;
import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.gjjy.googlebillinglib.annotation.PurchaseType;

/**
 购买结果
 */
public class PurchaseResultEntity extends ResultEntity{
    private String token;
    @PurchaseType
    private int purchaseType;
    @PurchaseState
    private int purchaseState;
    private String purchaseToken;

    public PurchaseResultEntity() { }

    public PurchaseResultEntity(BillingResult result, @PurchaseType int purchaseType) {
        super( result );
        this.purchaseType = purchaseType;
    }

    @NonNull
    @Override
    public String toString() {
        return "PurchaseResultEntity{" +
                "code=" + getCode() +
                ", debugMessage='" + getDebugMessage() + '\'' +
                ", token='" + token + '\'' +
                ", purchaseType=" + purchaseType +
                ", purchaseState=" + purchaseState +
                ", purchaseToken='" + purchaseToken + '\'' +
                '}';
    }

    public String getToken() { return token; }
    public PurchaseResultEntity setToken(String token) {
        this.token = token;
        return this;
    }

    @PurchaseType
    public int getPurchaseType() { return purchaseType; }
    public PurchaseResultEntity setPurchaseType(@PurchaseType int purchaseType) {
        this.purchaseType = purchaseType;
        return this;
    }

    @PurchaseState
    public int getPurchaseState() { return purchaseState; }
    public PurchaseResultEntity setPurchaseState(@PurchaseState int purchaseState) {
        this.purchaseState = purchaseState;
        return this;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public PurchaseResultEntity setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
        return this;
    }
}

package com.gjjy.googlebillinglib.entity;

import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.gjjy.googlebillinglib.annotation.PurchaseType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class PurchaseEntity extends BaseResultEntity {
    @PurchaseType
    private int purchaseType;
    @PurchaseState
    private int purchaseState;
    private String orderId;
    private String packageName;
    private String productId;
    private long purchaseTime;
    private String developerPayload;
    private String purchaseToken;
    private String originalJson;
    private String signature;

    public PurchaseEntity(int purchaseType, String originalJson, String signature) throws JSONException {
        this.purchaseType = purchaseType;
        this.originalJson = originalJson;
        this.signature = signature;
        JSONObject object = new JSONObject(originalJson);
        orderId = object.optString("orderId");
        packageName = object.optString("packageName");
        productId = object.optString("productId");
        purchaseTime = object.optLong("purchaseTime");
        purchaseState = object.optInt("purchaseState");
        developerPayload = object.optString("developerPayload");
        purchaseToken = object.optString(object.optString("purchaseToken"));
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "PurchaseInfo(purchaseType:" + purchaseType + "):" + originalJson + "code=" + getCode() +
                ", debugMessage='" + getDebugMessage() + '\'';
    }
}

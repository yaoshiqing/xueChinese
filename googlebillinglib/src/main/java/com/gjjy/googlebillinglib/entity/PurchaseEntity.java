package com.gjjy.googlebillinglib.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class PurchaseEntity {
    String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    String mOrderId;
    String mPackageName;
    String mProductId;
    long mPurchaseTime;
    int mPurchaseState;
    String mDeveloperPayload;
    String mPurchaseToken;
    String mOriginalJson;
    String mSignature;

    public PurchaseEntity(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        mItemType = itemType;
        mOriginalJson = jsonPurchaseInfo;
        JSONObject object = new JSONObject(mOriginalJson);
        mOrderId = object.optString("orderId");
        mPackageName = object.optString("packageName");
        mProductId = object.optString("productId");
        mPurchaseTime = object.optLong("purchaseTime");
        mPurchaseState = object.optInt("purchaseState");
        mDeveloperPayload = object.optString("developerPayload");
        mPurchaseToken = object.optString("token", object.optString("purchaseToken"));
        mSignature = signature;
    }

    public String getItemType() {
        return mItemType;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getProductId() {
        return mProductId;
    }

    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    public int getPurchaseState() {
        return mPurchaseState;
    }

    public String getDeveloperPayload() {
        return mDeveloperPayload;
    }

    public String getPurchaseToken() {
        return mPurchaseToken;
    }

    public String getOriginalJson() {
        return mOriginalJson;
    }

    public String getSignature() {
        return mSignature;
    }

    @Override
    public String toString() {
        return "PurchaseInfo(type:" + mItemType + "):" + mOriginalJson;
    }
}

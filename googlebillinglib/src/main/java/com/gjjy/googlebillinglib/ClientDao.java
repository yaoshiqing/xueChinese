package com.gjjy.googlebillinglib;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.gjjy.googlebillinglib.annotation.PurchaseType;
import com.gjjy.googlebillinglib.entity.SkuList;

import java.util.List;

public class ClientDao {
    @NonNull
    private final Client mClient;
    @NonNull
    private final BillingClient mBillingClient;
    @BillingClient.SkuType
    private String skuType;

    public ClientDao(@NonNull Client client, @NonNull BillingClient billingClient) {
        mClient = client;
        mBillingClient = billingClient;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    /**
     * 展示可供购买的商品
     *
     * @param call    查询结果
     * @param list sku参数
     */
    public void querySkuDetails(Consumer<SkuList> call, List<String> list) {
        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(list)
                .setType(getSkuType())
                .build();

        //开始查询
        mBillingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                Log.e("GooglePlaySub", "querySkuDetails Size:" + (skuDetailsList != null ? skuDetailsList.size() : -1) + "billingResult: "
                        + billingResult == null ? "null" : "getDebugMessage = " + billingResult.getDebugMessage()
                        + "getResponseCode = " + billingResult.getResponseCode());
                if (call != null) {
                    call.accept(new SkuList(billingResult, skuDetailsList));
                }
            }
        });
    }

    /**
     * 启动购买流程
     *
     * @param activity     页面
     * @param purchaseType 购买类型
     * @param skuDetails   购买的商品
     * @return 响应结果
     */
    public int launchBillingFlow(Activity activity, @PurchaseType int purchaseType, SkuDetails skuDetails, String googleOrderId, String uid) {
        //设置购买类型
        mClient.setPurchaseType(purchaseType);
        if (skuDetails == null) {
            return BillingClient.BillingResponseCode.ERROR;
        }

        String uuid = googleOrderId;

        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .setObfuscatedAccountId(uid)
                .setObfuscatedProfileId(uuid)
                .build();

        // 发起购买因为网络原因可能出现掉单的问题，所以就出现补单逻辑，调起支付之前
        BillingResult billingResult = mBillingClient.launchBillingFlow(activity, billingFlowParams);
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // 未能购买，因为已经拥有此商品
            mClient.queryPurchaseHistoryAsync(getSkuType());
        }

        Log.e("GooglePlaySub", "launchBillingFlow -> responseCode:" + responseCode);
        return responseCode;
    }
}

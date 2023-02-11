package com.gjjy.googlebillinglib;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.gjjy.googlebillinglib.annotation.PurchaseType;
import com.gjjy.googlebillinglib.entity.SkuList;

import java.util.List;

public class ClientDao {
    @NonNull
    private final Client mClient;
    @NonNull
    private final BillingClient mBillingClient;

    public ClientDao(@NonNull Client client, @NonNull BillingClient billingClient) {
        mClient = client;
        mBillingClient = billingClient;
    }

    /**
     展示可供购买的商品
     @param type        Sku类型
     @param call        查询结果
     @param skuList      sku参数
     */
    private void querySkuDetails(@BillingClient.SkuType String type, Consumer<SkuList> call, List<String> skuList) {
//        skuList.add("premium_upgrade");
//        skuList.add("gas");

        SkuDetailsParams p = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(type)
                .build();

        //开始查询
        mBillingClient.querySkuDetailsAsync(p, (result, list) -> {
            Log.e("GooglePlaySub", "querySkuDetails Size:" + (list != null ? list.size() : -1));
            Log.e("GooglePlaySub", "querySkuDetails result: "
                    + result == null ? "null" : "(getDebugMessage = " + result.getDebugMessage()
                    + "getResponseCode = " + result.getResponseCode()+")"
            );
            if (call != null) {
                call.accept(new SkuList(result, list));
            }
        });
    }

    /**
     * 展示可供购买的订阅
     *
     * @param call   查询结果
     * @param skuArr sku参数
     */
    public void querySkuDetailsOfSubs(Consumer<SkuList> call, List<String> skuArr) {
        querySkuDetails(BillingClient.SkuType.SUBS, call, skuArr);
    }

    /**
     展示可供购买的一次性商品
     @param call        查询结果
     @param skuArr      sku参数
     */
    public void querySkuDetailsOfInApp(Consumer<SkuList> call, List<String> skuArr) {
        querySkuDetails(BillingClient.SkuType.INAPP, call, skuArr);
    }

    /**
     启动购买流程
     @param activity        页面
     @param purchaseType    购买类型
     @param skuDetails      购买的商品
     @return                响应结果
     */
    public int launchBillingFlow(Activity activity, @PurchaseType int purchaseType, SkuDetails skuDetails) {
        //设置购买类型
        mClient.setPurchaseType(purchaseType);
        if (skuDetails == null) {
            return BillingClient.BillingResponseCode.ERROR;
        }

        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        //发起购买
        int responseCode = mBillingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        Log.e("GooglePlaySub", "launchBillingFlow -> responseCode:" + responseCode);
        return responseCode;
    }
}

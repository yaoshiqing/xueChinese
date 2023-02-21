package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

/**
 * 谷歌支付订单校验
 */
public class VerifyPayReq {

    private String googleOrderId;   // 谷歌交易流水号
    private String packageName;     // 包名
    private String productId;       // 购买商品第三方id
    private long purchaseTime;      // 购买产品的时间毫秒值
    private String purchaseState;   // 订单购买状态,0. 已购买 1. 已取消 2. 待定
    private String purchaseToken;   // 购买令牌
    private String signture;        // 签名
    private String originalJson;    // 签名

    @NonNull
    @Override
    public String toString() {
        return "VerifyPayReq{" +
                "googleOrderId=" + googleOrderId +
                "packageName=" + packageName +
                "productId=" + productId +
                "purchaseTime=" + purchaseTime +
                "purchaseState=" + purchaseState +
                "purchaseToken=" + purchaseToken +
                "signture=" + signture +
                "originalJson=" + originalJson +
                '}';
    }

    public String getGoogleOrderId() {
        return googleOrderId;
    }

    public void setGoogleOrderId(String googleOrderId) {
        this.googleOrderId = googleOrderId;
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

    public String getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(String purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getSignture() {
        return signture;
    }

    public void setSignture(String signture) {
        this.signture = signture;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }
}

package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

import com.gjjy.googlebillinglib.SkuUtils;
import com.ybear.ybcomponent.base.adapter.IItemData;

public class BuyVipOptionEntity implements IItemData {
    private String skuId;
    private String inDate;
    private String price;
    private String introductoryPrice;
    private String priceUnit;
    private String pricePerMonth;
    private int subscriptionDate;
    private char subscriptionDateSymbol;
    private boolean isHotPrice;
    private boolean isDiscount;
    private boolean isFirstDiscount;
    private boolean isShowPriceInfo;

    @NonNull
    @Override
    public String toString() {
        return "BuyVipOptionEntity{" +
                "skuId='" + skuId + '\'' +
                "inDate='" + inDate + '\'' +
                ", price='" + price + '\'' +
                ", introductoryPrice='" + introductoryPrice + '\'' +
                ", priceUnit='" + priceUnit + '\'' +
                ", pricePerMonth='" + pricePerMonth + '\'' +
                ", subscriptionDate=" + subscriptionDate +
                ", subscriptionDateSymbol=" + subscriptionDateSymbol +
                ", isHotPrice=" + isHotPrice +
                ", isDiscount=" + isDiscount +
                ", isFirstDiscount=" + isFirstDiscount +
                ", isShowPriceInfo=" + isShowPriceInfo +
                '}';
    }

    public String getSkuId() { return skuId; }
    public void setSkuId(String skuId) { this.skuId = skuId; }

    public String getInDate() { return inDate; }
    public void setInDate(String inDate) { this.inDate = inDate; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getIntroductoryPrice() { return introductoryPrice; }
    public void setIntroductoryPrice(String introductoryPrice) {
        this.introductoryPrice = introductoryPrice;
    }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public String getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(String price) { pricePerMonth = price; }

    public int getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(int date) { subscriptionDate = date; }

    @SkuUtils.DateSymbol
    public char getSubscriptionDateSymbol() { return subscriptionDateSymbol; }
    public void setSubscriptionDateSymbol(@SkuUtils.DateSymbol char subscriptionDateSymbol) {
        this.subscriptionDateSymbol = subscriptionDateSymbol;
    }

    public boolean isHotPrice() { return isHotPrice; }
    public void setHotPrice(boolean hotPrice) { isHotPrice = hotPrice; }

    public boolean isDiscount() { return isDiscount; }
    public void setDiscount(boolean discount) { isDiscount = discount; }

    public boolean isFirstDiscount() { return isFirstDiscount; }
    public void setFirstDiscount(boolean firstDiscount) { isFirstDiscount = firstDiscount; }

    public boolean isShowPriceInfo() { return isShowPriceInfo; }
    public void setShowPriceInfo(boolean isShow) { isShowPriceInfo = isShow; }
}

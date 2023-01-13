package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取商品列表实体类
 */
public class BuySubEntity extends BaseReqEntity {
    private int goodsId;                //商品id
    private int affiliatedGoodsId;      //绑定的原价id
    private String subscriptionId;      //支付产品ID
    private String title;               //标题
    private int saleType;               //商品类型。1:订阅，2：一次性商品，3：首次特惠
    private int saleNum;                //购买次数
    private int originalPercent;        //原价百分比
//    private int isHot;                  //是否标记为热门：1是，0否
    private int cornerMarkStatus;       //0:都不显示 1:显示热门 2:显示打折
    private int isShowOriginalPrice;    // 是否显示原价 1是，0否

    @NonNull
    @Override
    public String toString() {
        return "BuySubEntity{" +
                "goodsId=" + goodsId +
                ", affiliatedGoodsId=" + affiliatedGoodsId +
                ", subscriptionId='" + subscriptionId + '\'' +
                ", title='" + title + '\'' +
                ", saleType=" + saleType +
                ", saleNum=" + saleNum +
                ", originalPercent=" + originalPercent +
//                ", isHot=" + isHot +
                ", cornerMarkStatus=" + cornerMarkStatus +
                ", isShowOriginalPrice=" + isShowOriginalPrice +
                '}';
    }

    public int getGoodsId() { return goodsId; }
    public void setGoodsId(int goodsId) { this.goodsId = goodsId; }

    public int getAffiliatedGoodsId() { return affiliatedGoodsId; }
    public void setAffiliatedGoodsId(int goodsId) { affiliatedGoodsId = goodsId; }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getSaleType() { return saleType; }
    public void setSaleType(int saleType) { this.saleType = saleType; }

    public int getSaleNum() { return saleNum; }
    public void setSaleNum(int saleNum) { this.saleNum = saleNum; }

    public int getOriginalPercent() { return originalPercent; }
    public void setOriginalPercent(int originalPercent) { this.originalPercent = originalPercent; }

//    public boolean isHot() { return isHot == 1; }
//    public int getIsHot() { return isHot; }
//    public void setIsHot(int isHot) { this.isHot = isHot; }

    public int getCornerMarkStatus() { return cornerMarkStatus; }
    public void setCornerMarkStatus(int status) { cornerMarkStatus = status; }

    public boolean isShowOriginalPrice() { return isShowOriginalPrice == 1; }
    public int getIsShowOriginalPrice() { return isShowOriginalPrice; }
    public void setIsShowOriginalPrice(int isShow) { isShowOriginalPrice = isShow; }
}

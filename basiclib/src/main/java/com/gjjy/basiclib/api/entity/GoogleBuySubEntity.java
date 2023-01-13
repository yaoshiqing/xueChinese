package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取商品列表实体类 (google 购买)
 */
public class GoogleBuySubEntity extends BaseReqEntity {
    private int id;                 //数据id，即商品id
    private int subPeriod;          //订阅周期 10按月、20每3个月、30按年
    private double price;           //实际价格 单位为美元
    private double originalPrice;   //原价格 单位为美元
    private String tpGoodsId;       //第三方商品id，即谷歌商品购买id

    @NonNull
    @Override
    public String toString() {
        return "GoogleBuySubEntity{" +
                "id=" + id +
                ", subPeriod=" + subPeriod +
                ", price='" + price + '\'' +
                ", originalPrice='" + originalPrice + '\'' +
                ", tpGoodsId=" + tpGoodsId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubPeriod() {
        return subPeriod;
    }

    public void setSubPeriod(int subPeriod) {
        this.subPeriod = subPeriod;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getTpGoodsId() {
        return tpGoodsId;
    }

    public void setTpGoodsId(String tpGoodsId) {
        this.tpGoodsId = tpGoodsId;
    }
}

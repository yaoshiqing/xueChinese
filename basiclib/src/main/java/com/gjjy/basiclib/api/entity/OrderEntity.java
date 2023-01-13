package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 订单号
 */
public class OrderEntity extends BaseReqEntity {
    private String orderId;     //系统生成订单号

    @NonNull
    @Override
    public String toString() {
        return "OrderEntity{" +
                "orderId='" + orderId + '\'' +
                '}';
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

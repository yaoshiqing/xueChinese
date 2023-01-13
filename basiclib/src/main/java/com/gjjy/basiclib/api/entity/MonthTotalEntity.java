package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取指定月份积分统计
 */
public class MonthTotalEntity extends BaseReqEntity {
    private int addTotal;       //增加总数
    private int subTotal;       //消耗总数

    @NonNull
    @Override
    public String toString() {
        return "MonthTotalEntity{" +
                "addTotal=" + addTotal +
                ", subTotal=" + subTotal +
                '}';
    }

    public int getAddTotal() {
        return addTotal;
    }

    public void setAddTotal(int addTotal) {
        this.addTotal = addTotal;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }
}

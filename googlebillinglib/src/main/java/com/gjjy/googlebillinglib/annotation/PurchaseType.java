package com.gjjy.googlebillinglib.annotation;

/**
 商品确认类型
 */
public @interface PurchaseType {
    int UNSPECIFIED_TYPE = 0;       //未知类型
    int CONSUME = 1;                //一次性商品
    int ACKNOWLEDGE = 2;            //非消耗型商品
}
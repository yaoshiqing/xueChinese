package com.gjjy.googlebillinglib.annotation;

/**
 商品确认状态
 */
public @interface PurchaseState{
    int NONE = -2;
    int HAVE_ACKNOWLEDGE = -1;  //非消耗型商品于活动状态（未取消订阅）
    int UNSPECIFIED_STATE = 0;  //未知状态
    int PURCHASED = 1;          //已购买
    int PENDING = 2;            //待定
}
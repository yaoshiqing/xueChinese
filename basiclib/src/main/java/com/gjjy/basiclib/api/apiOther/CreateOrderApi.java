package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 生成订单信息
 */
public class CreateOrderApi extends BaseApiServer {
    @Override
    public String api() { return "study-core/order/createOrder"; }
}

package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * google支付订单校验接口 (google 支付)
 */
public class GooglePayVerifyPayApi extends BaseApiServer {
    @Override
    public String api() { return "study-core/order/googlePay/verifyPay"; }
}

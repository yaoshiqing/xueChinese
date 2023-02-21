package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 开会员接口 用verifyPay 替代
 */
public class NewSubApi extends BaseApiServer {
    @Override
    public String api() { return "study-core/google_pay/newSub"; }
}

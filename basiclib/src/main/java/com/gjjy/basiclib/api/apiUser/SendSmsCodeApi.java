package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 发送手机验证码
 */
public class SendSmsCodeApi extends BaseApiServer {
    @Override
    public String api() { return "study-user/v1/user/sendSmsCode"; }
}

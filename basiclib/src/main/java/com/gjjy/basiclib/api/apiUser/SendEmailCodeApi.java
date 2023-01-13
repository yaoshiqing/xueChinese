package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 邮箱 - 发送邮箱验证码
 */
public class SendEmailCodeApi extends BaseUserServer {
    @Override
    public String api() { return "sendEmailCode"; }
}

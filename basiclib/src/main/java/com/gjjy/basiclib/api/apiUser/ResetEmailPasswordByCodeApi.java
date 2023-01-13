package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 邮箱 - 验证码重置密码
 */
public class ResetEmailPasswordByCodeApi extends BaseUserServer {
    @Override
    public String api() { return "resetEmailPasswordByCode"; }
}

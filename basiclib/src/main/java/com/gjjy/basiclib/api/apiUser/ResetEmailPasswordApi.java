package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 邮箱 - 重置密码
 */
public class ResetEmailPasswordApi extends BaseUserServer {
    @Override
    public String api() { return "resetEmailPassword"; }
}

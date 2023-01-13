package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 登录后获取服务器uid
 */
public class ReqLoginUidApi extends BaseUserServer {
    @Override
    public String api() { return "loginUid"; }
}

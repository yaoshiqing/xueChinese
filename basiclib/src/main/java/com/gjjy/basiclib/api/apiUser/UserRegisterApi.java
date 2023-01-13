package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 用户注册接口
 */
public class UserRegisterApi extends BaseApiServer {
    @Override
    public String api() { return "study-user/v1/user/register"; }
}

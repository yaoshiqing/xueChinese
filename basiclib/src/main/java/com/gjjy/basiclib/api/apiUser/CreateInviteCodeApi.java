package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 生成邀请码
 */
public class CreateInviteCodeApi extends BaseUserServer {
    @Override
    public String api() { return "createInviteCode"; }
}

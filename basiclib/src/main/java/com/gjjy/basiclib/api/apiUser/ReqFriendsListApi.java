package com.gjjy.basiclib.api.apiUser;

import com.gjjy.basiclib.api.BaseUserServer;

/**
 * 我的好友
 */
public class ReqFriendsListApi extends BaseUserServer {
    @Override
    public String api() { return "friendsList"; }
}

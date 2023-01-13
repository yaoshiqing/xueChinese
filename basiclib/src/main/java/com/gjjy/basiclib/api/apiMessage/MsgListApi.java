package com.gjjy.basiclib.api.apiMessage;

import com.gjjy.basiclib.api.BaseMessageServer;

/**
 * 获取消息列表
 */
public class MsgListApi extends BaseMessageServer {
    @Override
    public String api() { return "lists"; }
}

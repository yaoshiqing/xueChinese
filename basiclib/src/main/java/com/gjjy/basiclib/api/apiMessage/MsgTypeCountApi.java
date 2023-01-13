package com.gjjy.basiclib.api.apiMessage;

import com.gjjy.basiclib.api.BaseMessageServer;

/**
 * 获取消息类型未读总数
 */
public class MsgTypeCountApi extends BaseMessageServer {
    @Override
    public String api() { return "msgTypeCount"; }
}

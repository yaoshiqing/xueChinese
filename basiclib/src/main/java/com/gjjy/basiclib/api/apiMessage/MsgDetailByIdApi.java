package com.gjjy.basiclib.api.apiMessage;

import com.gjjy.basiclib.api.BaseMessageServer;

/**
 * 获取消息详情
 */
public class MsgDetailByIdApi extends BaseMessageServer {
    @Override
    public String api() { return "detailByMsgId"; }
}

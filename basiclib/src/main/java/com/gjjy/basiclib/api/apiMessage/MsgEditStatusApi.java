package com.gjjy.basiclib.api.apiMessage;

import com.gjjy.basiclib.api.BaseMessageServer;

/**
 * 更改消息为已读
 */
public class MsgEditStatusApi extends BaseMessageServer {
    @Override
    public String api() { return "editStatus"; }
}

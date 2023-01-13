package com.gjjy.basiclib.api.apiMessage;

import com.gjjy.basiclib.api.BaseMessageServer;

/**
 * 更改所有消息为已读
 */
public class MsgEditStatusAllApi extends BaseMessageServer {
    @Override
    public String api() { return "editStatusAll"; }
}

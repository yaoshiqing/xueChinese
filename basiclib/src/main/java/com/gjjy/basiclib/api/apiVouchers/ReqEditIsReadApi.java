package com.gjjy.basiclib.api.apiVouchers;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 标记弹窗为已读
 */
public class ReqEditIsReadApi extends BaseApiServer {
    @Override
    public String api() { return "user_popup/editIsRead"; }
}

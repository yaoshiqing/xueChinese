package com.gjjy.basiclib.api.apiVouchers;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取弹窗列表
 */
public class ReqVouchersPopupApi extends BaseApiServer {
    @Override
    public String api() { return "user_popup/lists"; }
}

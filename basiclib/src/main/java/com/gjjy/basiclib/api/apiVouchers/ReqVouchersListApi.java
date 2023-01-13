package com.gjjy.basiclib.api.apiVouchers;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取领券中心列表
 */
public class ReqVouchersListApi extends BaseApiServer {
    @Override
    public String api() { return "user_ticket/lists"; }
}

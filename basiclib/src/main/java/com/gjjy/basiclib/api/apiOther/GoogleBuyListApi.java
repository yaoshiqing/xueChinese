package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取商品列表 (google 支付)
 */
public class GoogleBuyListApi extends BaseApiServer {
    @Override
    public String api() { return "study-core/Goods/listPayConfig"; }
}

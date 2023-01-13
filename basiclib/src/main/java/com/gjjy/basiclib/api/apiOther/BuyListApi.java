package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取商品列表
 */
public class BuyListApi extends BaseApiServer {
    @Override
    public String api() { return "Goods/lists"; }
}

package com.gjjy.basiclib.api.apiUserPoint;

import com.gjjy.basiclib.api.BaseUserPointServer;

/**
 * 获取积分记录列表
 */
public class ReqListsApi extends BaseUserPointServer{
    @Override
    public String api() { return "lists"; }
}

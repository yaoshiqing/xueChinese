package com.gjjy.basiclib.api.apiUserPoint;

import com.gjjy.basiclib.api.BaseUserPointServer;

/**
 * 获取有效积分总数
 */
public class ReqTotalApi extends BaseUserPointServer{
    @Override
    public String api() { return "total"; }
}

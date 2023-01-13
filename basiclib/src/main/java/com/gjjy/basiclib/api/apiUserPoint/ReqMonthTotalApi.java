package com.gjjy.basiclib.api.apiUserPoint;

import com.gjjy.basiclib.api.BaseUserPointServer;

/**
 * 获取指定月份积分统计
 */
public class ReqMonthTotalApi extends BaseUserPointServer{
    @Override
    public String api() { return "monthTotal"; }
}

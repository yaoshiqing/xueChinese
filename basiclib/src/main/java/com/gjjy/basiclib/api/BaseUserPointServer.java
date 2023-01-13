package com.gjjy.basiclib.api;

/**
 * 获取指定月份积分统计
 */
public abstract class BaseUserPointServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/user_point/";
    }
}

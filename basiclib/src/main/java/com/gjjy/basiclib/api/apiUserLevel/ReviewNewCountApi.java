package com.gjjy.basiclib.api.apiUserLevel;

import com.gjjy.basiclib.api.BaseUserLevelServer;

/**
 * 获取新复习阶段总数量
 */
public class ReviewNewCountApi extends BaseUserLevelServer {
    @Override
    public String api() { return "reviewNewCount"; }
}

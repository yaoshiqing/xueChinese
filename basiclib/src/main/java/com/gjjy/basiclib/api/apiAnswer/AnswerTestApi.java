package com.gjjy.basiclib.api.apiAnswer;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取阶段测验题目
 */
public class AnswerTestApi extends BaseApiServer {
    @Override
    public String api() { return "question/test"; }
}

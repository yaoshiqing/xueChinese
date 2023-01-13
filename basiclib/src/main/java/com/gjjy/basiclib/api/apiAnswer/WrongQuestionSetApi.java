package com.gjjy.basiclib.api.apiAnswer;

import com.gjjy.basiclib.api.BaseQuestionServer;

/**
 * 获取错题集列表
 */
public class WrongQuestionSetApi extends BaseQuestionServer {
    @Override
    public String api() { return "errorLists"; }
}

package com.gjjy.basiclib.api.apiAnswer;

import com.gjjy.basiclib.api.BaseQuestionServer;

/**
 * 移除错题集
 */
public class RemoveWrongQuestionSetApi extends BaseQuestionServer {
    @Override
    public String api() { return "delErrorQ"; }
}

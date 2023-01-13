package com.gjjy.basiclib.api.apiAnswer;

import com.gjjy.basiclib.api.BaseQuestionServer;

/**
 * 保存错题记录
 */
public class SaveErrorRecordApi extends BaseQuestionServer {
    @Override
    public String api() { return "saveErrorRecord"; }
}

package com.gjjy.basiclib.api;

public abstract class BaseQuestionServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-home/question/";
    }
}
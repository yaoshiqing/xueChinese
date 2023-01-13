package com.gjjy.basiclib.api;

public abstract class BaseWordsExplainServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/word_explain/";
    }
}

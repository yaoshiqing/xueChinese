package com.gjjy.basiclib.api;

/**
 * 关键词
 */
public abstract class BaseWordsExplainServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-home/word_explain/";
    }
}

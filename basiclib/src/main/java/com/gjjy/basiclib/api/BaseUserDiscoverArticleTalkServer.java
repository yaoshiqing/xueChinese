package com.gjjy.basiclib.api;

/**
 评论
 */
public abstract class BaseUserDiscoverArticleTalkServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/user_discover_article_talk/";
    }
}

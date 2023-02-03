package com.gjjy.basiclib.api;

/**
 发现页评论
 */
public abstract class BaseUserDiscoverArticleTalkServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-discover/user_discover_article_talk/";
    }
}

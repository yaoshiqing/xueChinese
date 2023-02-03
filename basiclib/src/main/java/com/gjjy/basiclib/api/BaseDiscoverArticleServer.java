package com.gjjy.basiclib.api;

/**
 * 发现文章
 */
public abstract class BaseDiscoverArticleServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-discover/discover_article/";
    }
}

package com.gjjy.basiclib.api.apiDiscoverArticle;

import com.gjjy.basiclib.api.BaseDiscoverArticleServer;

/**
 * 获取首页展示的文章列表
 */
public class ReqTopListsApi extends BaseDiscoverArticleServer {
    @Override
    public String api() { return "topLists"; }
}

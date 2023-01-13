package com.gjjy.basiclib.api.apiUserDiscoverArticleTalk;

import com.gjjy.basiclib.api.BaseUserDiscoverArticleTalkServer;

/**
 * 获取文章置顶评论
 */
public class TopCommentsApi extends BaseUserDiscoverArticleTalkServer {
    @Override
    public String api() { return "topLists"; }
}

package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取经验值排行榜列表
 */
public class RankingListApi extends BaseApiServer {
    @Override
    public String api() { return "user_exp/rankingLists"; }
}

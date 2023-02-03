package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取经验值排行榜列表
 */
public class RankingListApi extends BaseApiServer {
    @Override
    public String api() { return "study-user/user_exp/rankingLists"; }
}

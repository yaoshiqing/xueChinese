package com.gjjy.basiclib.api.apiStudyDiscover;

import com.gjjy.basiclib.api.BaseStudyDiscoverServer;

/**
 * 根据资源id获取播放授权接口
 */
public class GetVodPlayAuthApi extends BaseStudyDiscoverServer {
    @Override
    public String api() { return "v1/vod/getVodPlayAuth"; }
}

package com.gjjy.basiclib.api.apiFcm;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * FCM - 推送信息解綁
 */
public class PushUnBindApi extends BaseApiServer {
    @Override
    public String url() { return super.url() + "/study-core/fcm/"; }

    @Override
    public String api() { return "unBind"; }
}
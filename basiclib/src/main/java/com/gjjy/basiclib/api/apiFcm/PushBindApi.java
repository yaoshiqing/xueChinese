package com.gjjy.basiclib.api.apiFcm;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * FCM - 推送信息綁定
 */
public class PushBindApi extends BaseApiServer {
    @Override
    public String url() { return super.url() + "/study-core/fcm/"; }

    @Override
    public String api() { return "bind"; }
}
package com.gjjy.basiclib.api.apiVersion;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 检测版本更新信息
 */
public class UpdateCheckApi extends BaseApiServer {
    @Override
    public String url() { return super.url() + "/version/"; }

    @Override
    public String api() { return "check"; }
}

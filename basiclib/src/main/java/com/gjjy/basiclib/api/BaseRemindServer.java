package com.gjjy.basiclib.api;

public abstract class BaseRemindServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-user/remind/";
    }
}

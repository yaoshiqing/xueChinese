package com.gjjy.basiclib.api;

public abstract class BaseUserServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-user/user/";
    }
}

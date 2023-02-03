package com.gjjy.basiclib.api;

public abstract class BaseUserLevelServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-home/user_level/";
    }
}

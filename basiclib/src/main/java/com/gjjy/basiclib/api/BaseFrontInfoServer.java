package com.gjjy.basiclib.api;

public abstract class BaseFrontInfoServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/homepage/";
    }
}

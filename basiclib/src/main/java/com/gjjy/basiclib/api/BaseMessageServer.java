package com.gjjy.basiclib.api;

public abstract class BaseMessageServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/message/";
    }
}

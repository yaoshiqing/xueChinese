package com.gjjy.basiclib.api;

public abstract class BaseStudyDiscoverServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-discover/";
    }
}
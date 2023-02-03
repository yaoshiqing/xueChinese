package com.gjjy.basiclib.api;

public abstract class BaseProvideUnitServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/study-home/provide_unit/";
    }
}

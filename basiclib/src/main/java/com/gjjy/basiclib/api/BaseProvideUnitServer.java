package com.gjjy.basiclib.api;

public abstract class BaseProvideUnitServer extends BaseApiServer {
    @Override
    public String url() {
        return super.url() + "/provide_unit/";
    }
}

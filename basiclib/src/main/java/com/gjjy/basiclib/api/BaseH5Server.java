package com.gjjy.basiclib.api;

import com.gjjy.basiclib.Config;
import com.ybear.ybnetworkutil.request.Request;

public abstract class BaseH5Server extends Request {
    @Override
    public String url() {
        return Config.URL_H5 + "introduce/";
    }
}
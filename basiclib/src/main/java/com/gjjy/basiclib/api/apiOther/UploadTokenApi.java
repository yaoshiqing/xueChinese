package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * STS-token 文件上传
 */
public class UploadTokenApi extends BaseApiServer {
    @Override
    public String api() { return "study-core/upload/getUploadToken"; }
}

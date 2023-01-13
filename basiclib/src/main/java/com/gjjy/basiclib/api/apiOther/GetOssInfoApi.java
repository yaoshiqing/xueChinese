package com.gjjy.basiclib.api.apiOther;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 获取阿里云OSS图片上传信息
 */
public class GetOssInfoApi extends BaseApiServer {
    @Override
    public String api() { return "upload/getOssInfo"; }
}

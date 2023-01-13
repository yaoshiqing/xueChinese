package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 上传图片
 */
public class OSSInfoEntity extends BaseReqEntity {
    private String accessKeyId;         //ID
    private String accessKeySecret;     //秘钥
    private String endpoint;            //访问域名
    private String bucket;              //存储空间
    private String url;

    @NonNull
    @Override
    public String toString() {
        return "OSSInfoEntity{" +
                "accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", bucket='" + bucket + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getAccessKeyId() { return accessKeyId; }
    public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }

    public String getAccessKeySecret() { return accessKeySecret; }
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

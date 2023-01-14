package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 上传图片
 */
public class OSSInfoEntity extends BaseReqEntity {
    private String accessKeyId;         // ID
    private String accessKeySecret;     // 秘钥
    private String securityToken;       // 安全令牌
    private int expiration;             // sts token过期时间
    private String bucketName;          // 存储空间
    private String region;              // 区域名称
    private String endPoint;
    private String url;                 //

    @NonNull
    @Override
    public String toString() {
        return "OSSInfoEntity{" +
                "accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", securityToken='" + securityToken + '\'' +
                ", expiration='" + expiration + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", region='" + region + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getAccessKeyId() { return accessKeyId; }
    public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }

    public String getAccessKeySecret() { return accessKeySecret; }
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

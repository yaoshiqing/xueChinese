package com.gjjy.basiclib.api.entity;

public class UidEntity extends BaseReqEntity {
    private String uid;     //服务器生成唯一uid
    private String token;   //登录唯一标识符

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.api.entity.BaseReqEntity;

/**
 * 新用户注册
 */
public class UserRegisterReq extends BaseReqEntity {

    private String nickname;
    private String email;
    private String password;
    private String phone;
    private String smsCode;
    private int userId;
    private String lang;

    @NonNull
    @Override
    public String toString() {
        return "UserRegisterReq{" +
                "nickname=" + nickname +
                "email=" + email +
                "password=" + password +
                "phone=" + phone +
                "smsCode=" + smsCode +
                "userId=" + userId +
                "lang=" + lang +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}

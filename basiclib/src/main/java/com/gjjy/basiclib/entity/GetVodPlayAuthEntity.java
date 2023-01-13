package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.api.entity.BaseReqEntity;

/**
 * 根据 mediaId 获取播放授权接口
 */
public class GetVodPlayAuthEntity extends BaseReqEntity {
    // 授权有效期，单位为秒
    private int expiredTime;
    // 播放授权
    private String playAuth;

    @NonNull
    @Override
    public String toString() {
        return "GetVodPlayAuthEntity{" +
                "expiredTime=" + expiredTime +
                "playAuth=" + playAuth +
                '}';
    }

    public int getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(int expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getPlayAuth() {
        return playAuth;
    }

    public void setPlayAuth(String playAuth) {
        this.playAuth = playAuth;
    }
}

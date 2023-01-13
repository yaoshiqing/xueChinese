package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

public class DiscoveryDetailJsonRecordEntity extends BaseReqEntity {
    private int playProgressSecond;     //播放资源时长（秒）

    public DiscoveryDetailJsonRecordEntity() { }

    public DiscoveryDetailJsonRecordEntity(int playProgressSecond) {
        this.playProgressSecond = playProgressSecond;
    }

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailJsonRecordEntity{" +
                "playProgressSecond=" + playProgressSecond +
                '}';
    }

    public int getPlayProgressSecond() {
        return playProgressSecond;
    }

    public void setPlayProgressSecond(int playProgressSecond) {
        this.playProgressSecond = playProgressSecond;
    }
}
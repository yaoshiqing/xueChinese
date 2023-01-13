package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

public class DiscoveryDetailJsonContentSentenceEntity extends BaseReqEntity {
    private float second;
    private String zh;
    private String pinyin;
    private String en;

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailJsonContentSentenceEntity{" +
                "second=" + second +
                ", zh='" + zh + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", en='" + en + '\'' +
                '}';
    }

    public float getSecond() {
        return second;
    }

    public long getMillisecond() { return Math.round( second ); }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }
}

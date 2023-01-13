package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscoveryDetailJsonContentTalkEntity extends BaseReqEntity {
    private String role;                                            //标题
    private String[] zhSentence;                                    //内容
    private String enSentence;                                      //翻译
    private String audioUrl;                                        //发音链接
    private List<DiscoveryDetailJsonContentSentenceEntity> keyword;      //关键字

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailJsonContentTalkEntity{" +
                "role='" + role + '\'' +
                ", zhSentence=" + Arrays.toString( zhSentence ) +
                ", enSentence='" + enSentence + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", keyword=" + ( keyword != null ? Arrays.toString( keyword.toArray() ) : "" ) +
                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getZhSentence() {
        return zhSentence != null ? zhSentence : new String[0];
    }

    public void setZhSentence(String[] zhSentence) {
        this.zhSentence = zhSentence;
    }

    public String getEnSentence() {
        return enSentence;
    }

    public void setEnSentence(String enSentence) {
        this.enSentence = enSentence;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public List<DiscoveryDetailJsonContentSentenceEntity> getKeyword() {
        return keyword != null ? keyword : new ArrayList<>();
    }

    public void setKeyword(List<DiscoveryDetailJsonContentSentenceEntity> keyword) {
        this.keyword = keyword;
    }
}

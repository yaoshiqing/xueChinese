package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class DiscoveryDetailJsonContentEntity extends BaseReqEntity {
    private String playUrl;          //音频或者视频
    private int playSecond;          //播放资源时长（秒）
    private List<DiscoveryDetailJsonContentSentenceEntity> sentence;
    private List<DiscoveryDetailJsonContentTalkEntity> talk;
    private List<DiscoveryDetailJsonContentGrammarEntity> grammar;

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailJsonContentEntity{" +
                "playUrl='" + playUrl + '\'' +
                ", playSecond=" + playSecond +
                ", sentence=" + ( sentence != null ? Arrays.toString( sentence.toArray() ) : "" ) +
                ", talk=" + ( talk != null ? Arrays.toString( talk.toArray() ) : "" ) +
                ", grammar=" + ( grammar != null ? Arrays.toString( grammar.toArray() ) : "" ) +
                '}';
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getPlaySecond() {
        return playSecond;
    }

    public void setPlaySecond(int playSecond) {
        this.playSecond = playSecond;
    }

    @Nullable
    public List<DiscoveryDetailJsonContentSentenceEntity> getSentence() {
        return sentence;
    }

    public void setSentence(List<DiscoveryDetailJsonContentSentenceEntity> sentence) {
        this.sentence = sentence;
    }

    public List<DiscoveryDetailJsonContentTalkEntity> getTalk() {
        return talk;
    }

    public void setTalk(List<DiscoveryDetailJsonContentTalkEntity> talk) {
        this.talk = talk;
    }

    public List<DiscoveryDetailJsonContentGrammarEntity> getGrammar() {
        return grammar;
    }

    public void setGrammar(List<DiscoveryDetailJsonContentGrammarEntity> grammar) {
        this.grammar = grammar;
    }
}
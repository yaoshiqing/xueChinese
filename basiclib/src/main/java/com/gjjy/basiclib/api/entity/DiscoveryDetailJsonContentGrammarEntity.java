package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class DiscoveryDetailJsonContentGrammarEntity extends BaseReqEntity {
    private String zhSentence;                                      //内容
    private String[] keyword;                                       //关键字
    private String enSentence;                                      //翻译
    private String remark;                                          //发音链接
    private String explain;                                         //发音链接
    private List<DiscoveryDetailJsonContentSentenceEntity> example;      //关键字

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailJsonContentGrammarEntity{" +
                "zhSentence=" + zhSentence +
                ", keyword=" + Arrays.toString( keyword ) +
                ", enSentence='" + enSentence + '\'' +
                ", remark='" + remark + '\'' +
                ", explain='" + explain + '\'' +
                ", example=" + ( example != null ? Arrays.toString( example.toArray() ) : "" ) +
                '}';
    }

    public String getZhSentence() {
        return zhSentence;
    }

    public void setZhSentence(String zhSentence) {
        this.zhSentence = zhSentence;
    }

    public String[] getKeyword() {
        return keyword;
    }

    public void setKeyword(String[] keyword) {
        this.keyword = keyword;
    }

    public String getEnSentence() {
        return enSentence;
    }

    public void setEnSentence(String enSentence) {
        this.enSentence = enSentence;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public List<DiscoveryDetailJsonContentSentenceEntity> getExample() {
        return example;
    }

    public void setExample(List<DiscoveryDetailJsonContentSentenceEntity> example) {
        this.example = example;
    }
}

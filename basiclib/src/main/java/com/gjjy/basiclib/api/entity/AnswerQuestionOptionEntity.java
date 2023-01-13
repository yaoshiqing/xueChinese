package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 获取模块练习/考试题目的JSON格式题目内容
 */
public class AnswerQuestionOptionEntity extends BaseReqEntity {
    private List<String> lang;
    private List<String> pinyin;
    private String image;
    private String english;
    private String audio;
    private int pinyinSort;

    @NonNull
    @Override
    public String toString() {
        return "AnswerQuestionOptionEntity{" +
                "lang=" + lang +
                ", pinyin=" + pinyin +
                ", image='" + image + '\'' +
                ", english='" + english + '\'' +
                ", audio='" + audio + '\'' +
                ", pinyinSort='" + pinyinSort + '\'' +
                '}';
    }

    public List<String> getLang() { return lang; }
    public void setLang(List<String> lang) { this.lang = lang; }

    public List<String> getPinyin() { return pinyin; }
    public void setPinyin(List<String> pinyin) { this.pinyin = pinyin; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getAudio() { return audio; }
    public void setAudio(String audio) { this.audio = audio; }

    public int getPinyinSort() { return pinyinSort; }
    public void setPinyinSort(int pinyinSort) { this.pinyinSort = pinyinSort; }
}
package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 词语表
 */
public class WordsExplainEntity extends BaseReqEntity {
    private String word;       //词语
    private String pinyin;     //拼音
    private String english;    //英语
    private String korean;     //韩语
    private String audioUrl;   //音频地址

    @NonNull
    @Override
    public String toString() {
        return "WordsExplainEntity{" +
                "word='" + word + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", english='" + english + '\'' +
                ", korean='" + korean + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                '}';
    }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getPinyin() { return pinyin; }
    public void setPinyin(String pinyin) { this.pinyin = pinyin; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getKorean() { return korean; }
    public void setKorean(String korean) { this.korean = korean; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}

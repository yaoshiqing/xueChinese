package com.gjjy.basiclib.api.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 获取模块练习/考试题目的JSON格式题目内容
 */
public class AnswerQuestionContentEntity extends BaseReqEntity {
    private String title;                               //标题
    private List<String> lang;
    private List<String> pinyin;
    private String sentence;                            //句子
    private String english;                             //句子
    private String answer;                              //答案
    private String explain;                             //解析
    private List<AnswerQuestionOptionEntity> option;    //选项
    private String audio;                               //问题音频链接
    private String audioUrl;                            //解析音频链接
    private String video;                               //视频链接
    private int typesetting;                            //布局类型（0：网格，1：列表）

    @NonNull
    @Override
    public String toString() {
        return "AnswerQuestionContentEntity{" +
                "title='" + title + '\'' +
                ", lang=" + lang +
                ", pinyin=" + pinyin +
                ", sentence='" + getSentence() + '\'' +
                ", english='" + english + '\'' +
                ", answer='" + answer + '\'' +
                ", explain='" + explain + '\'' +
                ", option=" + option +
                ", audio='" + audio + '\'' +
                ", video='" + video + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", typesetting='" + typesetting + '\'' +
                '}';
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getLang() { return lang; }
    public void setLang(List<String> lang) { this.lang = lang; }

    public List<String> getPinyin() { return pinyin; }
    public void setPinyin(List<String> pinyin) { this.pinyin = pinyin; }

    public String getSentence() {
        if( TextUtils.isEmpty( sentence ) ) return getEnglish();
        return sentence;
    }
    public void setSentence(String sentence) { this.sentence = sentence; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getExplain() { return explain; }
    public void setExplain(String explain) { this.explain = explain; }

    public List<AnswerQuestionOptionEntity> getOption() { return option; }
    public void setOption(List<AnswerQuestionOptionEntity> option) { this.option = option; }

    public String getAudio() { return audio; }
    public void setAudio(String audio) { this.audio = audio; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

    public int getTypesetting() { return typesetting; }
    public void setTypesetting(int typesetting) { this.typesetting = typesetting; }
}
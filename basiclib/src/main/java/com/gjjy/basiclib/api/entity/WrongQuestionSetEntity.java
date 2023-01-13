package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 错题集实体类
 */
public class WrongQuestionSetEntity extends BaseReqEntity {
    private int progress;
    private WrongQuestionSetQuestionEntity question;

    @NonNull
    @Override
    public String toString() {
        return "WrongQuestionSetEntity{" +
                "progress=" + progress +
                ", question=" + question +
                '}';
    }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public WrongQuestionSetQuestionEntity getQuestion() { return question; }
    public void setQuestion(WrongQuestionSetQuestionEntity question) { this.question = question; }
}

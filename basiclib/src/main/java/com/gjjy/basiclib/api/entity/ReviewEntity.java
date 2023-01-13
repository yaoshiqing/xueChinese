package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 获取分类下的复习题目
 */
public class ReviewEntity extends BaseReqEntity {
    private int levelId;
    private int progress;
    private List<AnswerQuestionEntity> question;

    @NonNull
    @Override
    public String toString() {
        return "ReviewEntity{" +
                "levelId=" + levelId +
                "progress=" + progress +
                ", question=" + question +
                '}';
    }

    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public List<AnswerQuestionEntity> getQuestion() { return question; }
    public void setQuestion(List<AnswerQuestionEntity> question) { this.question = question; }
}

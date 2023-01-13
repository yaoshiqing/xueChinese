package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * 获取模块练习/考试题目
 */
public class AnswerQuestionEntity extends BaseReqEntity {
    private int questionId;                             //题目id
    private String[] keyword;                           //关键词
    private int questionTypeId;                         //题目类型id
    private int optionTypeId;                           //选择题选项类型id
    private AnswerQuestionContentEntity content;        //JSON格式题目内容
    private int index;

    @NonNull
    @Override
    public String toString() {
        return "AnswerQuestionEntity{" +
                "questionId=" + questionId +
                ", keyword='" + Arrays.toString(keyword) + '\'' +
                ", questionTypeId=" + questionTypeId +
                ", optionTypeId=" + optionTypeId +
                ", content='" + content + '\'' +
                ", index=" + index +
                '}';
    }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String[] getKeyword() { return keyword; }
    public void setKeyword(String[] keyword) { this.keyword = keyword; }

    public int getQuestionTypeId() { return questionTypeId; }
    public void setQuestionTypeId(int questionTypeId) { this.questionTypeId = questionTypeId; }

    public int getOptionTypeId() { return optionTypeId; }
    public void setOptionTypeId(int optionTypeId) { this.optionTypeId = optionTypeId; }

    public AnswerQuestionContentEntity getContent() { return content; }
    public void setContent(AnswerQuestionContentEntity content) { this.content = content; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
}

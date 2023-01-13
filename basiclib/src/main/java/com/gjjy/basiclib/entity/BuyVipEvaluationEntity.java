package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

public class BuyVipEvaluationEntity {
    private int userPhotoResId;
    private String userName;
    private String learnedDaysContent;
    private String evalContent;

    @NonNull
    @Override
    public String toString() {
        return "BuyVipEvaluationEntity{" +
                "userPhotoResId=" + userPhotoResId +
                ", userName='" + userName + '\'' +
                ", learnedDaysContent='" + learnedDaysContent + '\'' +
                ", evalContent='" + evalContent + '\'' +
                '}';
    }

    public int getUserPhotoResId() {
        return userPhotoResId;
    }

    public void setUserPhotoResId(int userPhotoResId) {
        this.userPhotoResId = userPhotoResId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLearnedDaysContent() {
        return learnedDaysContent;
    }

    public void setLearnedDaysContent(String learnedDaysContent) {
        this.learnedDaysContent = learnedDaysContent;
    }

    public String getEvalContent() {
        return evalContent;
    }

    public void setEvalContent(String evalContent) {
        this.evalContent = evalContent;
    }
}

package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取经验值
 */
public class AnswerExpEntity extends BaseReqEntity {
    private int exp;             //领取经验值
    private int todayExp;        //今天领取经验值总数
    private int newScore;        //新分数
    private int oldScore;        //原始测验分数

    @NonNull
    @Override
    public String toString() {
        return "AnswerExpEntity{" +
                "exp=" + exp +
                ", todayExp=" + todayExp +
                ", newScore=" + newScore +
                ", oldScore=" + oldScore +
                '}';
    }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getTodayExp() { return todayExp; }
    public void setTodayExp(int todayExp) { this.todayExp = todayExp; }

    public int getNewScore() { return newScore; }
    public void setNewScore(int newScore) { this.newScore = newScore; }

    public int getOldScore() { return oldScore; }
    public void setOldScore(int oldScore) { this.oldScore = oldScore; }
}

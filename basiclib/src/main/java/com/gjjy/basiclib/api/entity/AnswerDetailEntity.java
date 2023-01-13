package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取模块详情
 */
public class AnswerDetailEntity extends BaseReqEntity {
    private int unitStatus;    //模块状态：0未解锁、1已解锁、2已完成
    private int sectionNum;    //当前小节进度
    private String title;      //模块名称
    private String explain;    //课程介绍
    private String explainH;   //课程链接

    @NonNull
    @Override
    public String toString() {
        return "AnswerDetailEntity{" +
                ", unitStatus=" + unitStatus +
                ", sectionNum=" + sectionNum +
                ", title='" + title + '\'' +
                ", explain='" + explain + '\'' +
                ", explainH='" + explainH + '\'' +
                '}';
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExplain() { return explain; }
    public void setExplain(String explain) { this.explain = explain; }

    public String getExplainH() { return explainH; }
    public void setExplainH(String explainH) { this.explainH = explainH; }

    public int getUnitStatus() { return unitStatus; }
    public void setUnitStatus(int unitStatus) { this.unitStatus = unitStatus; }

    public int getSectionNum() { return sectionNum; }
    public void setSectionNum(int sectionNum) { this.sectionNum = sectionNum; }
}

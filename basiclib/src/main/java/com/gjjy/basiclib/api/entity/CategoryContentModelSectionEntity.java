package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取分类内容下的模块
 */
public class CategoryContentModelSectionEntity extends BaseReqEntity {
    private int sectionId;     //小节id
    private String title;      //小节标题

    @NonNull
    @Override
    public String toString() {
        return "CategoryContentModelSectionEntity{" +
                "sectionId=" + sectionId +
                ", title='" + title + '\'' +
                '}';
    }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取所有分类
 */
public class ReviewStatusEntity extends BaseReqEntity {
    private int categoryId;     //分类id
    private String title;       //分类标题
    private String imgUrl;      //分类图片
    private String lockImgUrl;  //分类未解锁图片
    private int newCount;       //新复习阶段数量
    private int allCount;       //所有复习阶段数量

    @NonNull
    @Override
    public String toString() {
        return "ReviewStatusEntity{" +
                "categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", lockImgUrl='" + lockImgUrl + '\'' +
                ", newCount=" + newCount +
                ", allCount=" + allCount +
                '}';
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public String getLockImgUrl() { return lockImgUrl; }
    public void setLockImgUrl(String lockImgUrl) { this.lockImgUrl = lockImgUrl; }

    public int getNewCount() { return newCount; }
    public void setNewCount(int newCount) { this.newCount = newCount; }

    public int getAllCount() { return allCount; }
    public void setAllCount(int allCount) { this.allCount = allCount; }
}

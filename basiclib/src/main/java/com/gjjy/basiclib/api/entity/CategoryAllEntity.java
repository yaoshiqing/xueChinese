package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取所有分类
 */
public class CategoryAllEntity extends BaseReqEntity {
    private int categoryId;     //分类id
    private String title;       //分类标题
    private String imgUrl;      //分类图片

    @NonNull
    @Override
    public String toString() {
        return "CategoryAllEntity{" +
                "categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
}

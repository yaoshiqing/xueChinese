package com.gjjy.frontlib.entity;

import androidx.annotation.NonNull;

/**
 * 首页列表下的Item的子Item实体类
 */
public class HallChildEntity extends BaseEntity {
    private String imgUrl;
    private String title;

    @NonNull
    @Override
    public String toString() {
        return "HallChildEntity{" +
                "id=" + getId() +
                "imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getImgUrl() { return imgUrl; }

    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

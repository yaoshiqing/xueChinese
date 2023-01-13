package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 获取分类下的内容
 */
public class CategoryContentEntity extends BaseReqEntity {
    private int levelId;                                //阶段id
    private String title;                               //阶段标题
    private List<CategoryContentModelEntity> unit;      //模块数组
    private int levelStatus;                            //阶段状态：0未解锁、1已解锁、2已完成
    private int score;                                  //分数

    @NonNull
    @Override
    public String toString() {
        return "CategoryContentEntity{" +
                "levelId=" + levelId +
                ", title='" + title + '\'' +
                ", unit=" + unit +
                ", levelStatus=" + levelStatus +
                ", score=" + score +
                '}';
    }

    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<CategoryContentModelEntity> getUnit() { return unit; }
    public void setUnit(List<CategoryContentModelEntity> unit) { this.unit = unit; }

    public int getLevelStatus() { return levelStatus; }
    public void setLevelStatus(int levelStatus) { this.levelStatus = levelStatus; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}

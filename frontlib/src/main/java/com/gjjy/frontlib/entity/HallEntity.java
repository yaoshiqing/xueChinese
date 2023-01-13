package com.gjjy.frontlib.entity;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 首页列表下的Item实体类
 */
public class HallEntity extends BaseEntity {
    private List<HallChildEntity> data;
    private String testBtnTitle;
    private int type;                       //0~N，表示圆圈数量

    public List<HallChildEntity> getData() { return data; }
    public void setData(List<HallChildEntity> data) { this.data = data; }

    public String getTestBtnTitle() { return testBtnTitle; }
    public void setTestBtnTitle(String testBtnTitle) { this.testBtnTitle = testBtnTitle; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    @NonNull
    @Override
    public String toString() {
        return "HallEntity{" +
                "id=" + getId() +
                "data=" + data +
                ", testBtnTitle='" + testBtnTitle + '\'' +
                ", type=" + type +
                '}';
    }
}

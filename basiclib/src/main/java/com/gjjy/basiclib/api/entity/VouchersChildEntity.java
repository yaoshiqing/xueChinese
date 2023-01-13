package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取领券中心列表
 */
public class VouchersChildEntity {
    private int id;                     //领券中心表自增ID
    private int type;                   //类型：1会员试用
    private String title;               //标题
    private String explain;             //描述
    private int num;                    //数量
    private int status;                 //状态：1已使用，0未使用，2已停用
    private String createTime;          //创建时间
    private String invalidTime;         //失效时间
    private String startTime;           //开始试用时间
    private String endTime;             //结束试用时间
    private int nowStatus;              //当前状态：1待使用、2正在使用、3已过期、4使用结束、5已停用

    @NonNull
    @Override
    public String toString() {
        return "VouchersChildEntity{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", explain='" + explain + '\'' +
                ", num=" + num +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", invalidTime='" + invalidTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", nowStatus=" + nowStatus +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(String invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getNowStatus() {
        return nowStatus;
    }

    public void setNowStatus(int nowStatus) {
        this.nowStatus = nowStatus;
    }
}

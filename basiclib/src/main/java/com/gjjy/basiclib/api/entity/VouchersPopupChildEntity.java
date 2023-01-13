package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取领券中心列表
 */
public class VouchersPopupChildEntity {
    private int id;                     //用户弹窗表自增ID
    private int type;                   //类型：1会员试用
    private int ticketId;               //券表自增ID
    private int role;                   //角色：1邀请人、2被邀请人
    private int status;                 //状态：0未使用，2已结束
    private String createTime;          //创建时间

    @NonNull
    @Override
    public String toString() {
        return "VouchersPopupChildEntity{" +
                "id=" + id +
                ", type=" + type +
                ", ticketId=" + ticketId +
                ", role=" + role +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
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

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
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
}

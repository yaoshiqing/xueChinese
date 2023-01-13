package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取所有分类
 */
public class MsgTypeCountEntity extends BaseReqEntity {
    private int msgType;     //消息类型：1系统通知、2会员消息
    private int all;         //当前消息类型记录总数
    private int unread;      //当前消息类型记录未读总数
    private String picUrl;   //图片链接

    @NonNull
    @Override
    public String toString() {
        return "MsgTypeCountEntity{" +
                "msgType=" + msgType +
                ", all=" + all +
                ", unread=" + unread +
                ", picUrl=" + picUrl +
                '}';
    }

    public int getMsgType() { return msgType; }
    public void setMsgType(int msgType) { this.msgType = msgType; }

    public int getAll() { return all; }
    public void setAll(int all) { this.all = all; }

    public int getUnread() { return unread; }
    public void setUnread(int unread) { this.unread = unread; }

    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }
}

package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取消息列表内容
 */
public class MsgListContentEntity {
    private int id;                 //消息表自增id
    private int msgType;            //消息类型：1系统通知、2会员通知
    private int sendType;           //消息类型：1自动回复、2消息推送
    private String sendKey;         //自动回复表或消息推送表的主键
    private String title;           //消息标题
    private String summary;         //消息摘要
    private String content;         //我是内容
    private String link;            //跳转地址
    private int status;             //状态：0未读、1已读
    private long createTime;      //创建时间

    @NonNull
    @Override
    public String toString() {
        return "MsgListContentEntity{" +
                "id=" + id +
                ", msgType=" + msgType +
                ", sendType=" + sendType +
                ", sendKey='" + sendKey + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", link='" + link + '\'' +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                '}';
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMsgType() { return msgType; }
    public void setMsgType(int msgType) { this.msgType = msgType; }

    public int getSendType() { return sendType; }
    public void setSendType(int sendType) { this.sendType = sendType; }

    public String getSendKey() { return sendKey; }
    public void setSendKey(String sendKey) { this.sendKey = sendKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}

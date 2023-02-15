package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

/**
 * 获取互动消息列表
 */
public class MsgListOfInteractiveChildEntity extends MsgListContentEntity {
    private int discoverTypeId;             //文章类型表自增ID
    private int discoverArticleId;          //文章表自增ID
    private int talkId;                     //评论表自增ID
    private int userId;                     //用户表自增ID
    private int interactType;               //互动类型：1点赞、2回复
    private int interactUserId;             //互动用户的用户表自增ID
    private String interactNickname;        //互动用户的用户昵称
    private String interactAvatarUrl;       //互动用户的头像地址
    private String talkContent;             //评论内容
    private String interactTalkContent;     //互动用户的评论内容
    private String imgUrl;                  //文章小图
    private String bigImgUrl;               //文章大图
    private int isVip;                      //是否为会员
    private String videoId;                 //视频videoId

    @NonNull
    @Override
    public String toString() {
        return "MsgListOfInteractiveChildEntity{" +
                "id=" + getId() +
                ", msgType=" + getMsgType() +
                ", sendType=" + getSendType() +
                ", sendKey='" + getSendKey() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", summary='" + getSummary() + '\'' +
                ", content='" + getContent() + '\'' +
                ", link='" + getLink() + '\'' +
                ", status=" + getStatus() +
                ", content='" + getContent() + '\'' +
                ", discoverTypeId=" + discoverTypeId +
                ", discoverArticleId=" + discoverArticleId +
                ", userId=" + userId +
                ", interactType=" + interactType +
                ", interactUserId=" + interactUserId +
                ", interactNickname='" + interactNickname + '\'' +
                ", interactAvatarUrl='" + interactAvatarUrl + '\'' +
                ", talkContent='" + talkContent + '\'' +
                ", interactTalkContent='" + interactTalkContent + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", bigImgUrl='" + bigImgUrl + '\'' +
                ", talkId=" + talkId +
                ", isVip=" + isVip +
                ", createTime='" + getCreateTime() + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }

    public int getDiscoverTypeId() {
        return discoverTypeId;
    }

    public void setDiscoverTypeId(int discoverTypeId) {
        this.discoverTypeId = discoverTypeId;
    }

    public int getDiscoverArticleId() {
        return discoverArticleId;
    }

    public void setDiscoverArticleId(int discoverArticleId) {
        this.discoverArticleId = discoverArticleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getInteractType() {
        return interactType;
    }

    public void setInteractType(int interactType) {
        this.interactType = interactType;
    }

    public int getInteractUserId() {
        return interactUserId;
    }

    public void setInteractUserId(int interactUserId) {
        this.interactUserId = interactUserId;
    }

    public String getInteractNickname() {
        return interactNickname;
    }

    public void setInteractNickname(String interactNickname) {
        this.interactNickname = interactNickname;
    }

    public String getInteractAvatarUrl() {
        return interactAvatarUrl;
    }

    public void setInteractAvatarUrl(String interactAvatarUrl) {
        this.interactAvatarUrl = interactAvatarUrl;
    }

    public String getTalkContent() {
        return talkContent;
    }

    public void setTalkContent(String talkContent) {
        this.talkContent = talkContent;
    }

    public String getInteractTalkContent() {
        return interactTalkContent;
    }

    public void setInteractTalkContent(String interactTalkContent) {
        this.interactTalkContent = interactTalkContent;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public int getTalkId() {
        return talkId;
    }

    public void setTalkId(int talkId) {
        this.talkId = talkId;
    }

    public int getIsVip() {
        return isVip;
    }
    public boolean isVip() { return ObjUtils.parseBoolean( isVip ); }
    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}

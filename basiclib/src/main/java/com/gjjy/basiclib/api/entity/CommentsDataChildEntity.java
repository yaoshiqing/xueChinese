package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

import java.util.List;

/**
 * 评论列表
 */
public class CommentsDataChildEntity extends BaseReqEntity {
    private int talkId;                                     //评论表自增id
    private int discoverArticleId;                          //文章自增ID
    private int userId;                                     //用户表自增ID
    private String nickname;                                //昵称

    private int targetTalkId;                               //回复目标评论表自增id
    private long targetUserId;                              //回复目标用户表自增ID
    private String targetNickname;                          //回复目标昵称
    private String avatarUrl;                               //头像地址
    private String content;                                 //评论内容
    private int praiseNum;                                  //数量
    private int isPraise;                                   //是否已点赞：1是、0否
    private int targetCount;                                //回复数量
    private String createTime;                              //创建时间
    private int isVip;                                      //是否为会员
    private int vipStatus;                                  //会员状态
    private List<CommentsDataChildEntity> secondLevel;

    @NonNull
    @Override
    public String toString() {
        return "CommentsDataChildEntity{" +
                "talkId=" + talkId +
                ", discoverArticleId=" + discoverArticleId +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", targetTalkId=" + targetTalkId +
                ", targetUserId=" + targetUserId +
                ", targetNickname='" + targetNickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", content='" + content + '\'' +
                ", praiseNum=" + praiseNum +
                ", isPraise=" + isPraise +
                ", targetCount=" + targetCount +
                ", createTime='" + createTime + '\'' +
                ", isVip='" + isVip + '\'' +
                ", vipStatus='" + vipStatus + '\'' +
                ", second_level='" + ( secondLevel == null ? null : secondLevel.toArray() ) + '\'' +
                '}';
    }

    public int getTalkId() { return talkId; }
    public void setTalkId(int talkId) { this.talkId = talkId; }

    public int getDiscoverArticleId() { return discoverArticleId; }
    public void setDiscoverArticleId(int discoverArticleId) { this.discoverArticleId = discoverArticleId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getTargetTalkId() { return targetTalkId; }
    public void setTargetTalkId(int targetTalkId) { this.targetTalkId = targetTalkId; }

    public long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(long targetUserId) { this.targetUserId = targetUserId; }

    public String getTargetNickname() { return targetNickname; }
    public void setTargetNickname(String targetNickname) { this.targetNickname = targetNickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getPraiseNum() { return praiseNum; }
    public void setPraiseNum(int praiseNum) { this.praiseNum = praiseNum; }

    public int getIsPraise() { return isPraise; }
    public void setIsPraise(int isPraise) { this.isPraise = isPraise; }
    public boolean isPraise() { return isPraise == 1; }
    public void setPraise(boolean isPraise) { this.isPraise = isPraise ? 1 : 0; }

    public int getTargetCount() { return targetCount; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public int getIsVip() { return isVip; }
    public boolean isVip() { return ObjUtils.parseBoolean( isVip ); }
    public void setIsVip(int isVip) { this.isVip = isVip; }

    public int getVipStatus() { return vipStatus; }
    public void setVipStatus(int vipStatus) { this.vipStatus = vipStatus; }

    public List<CommentsDataChildEntity> getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(List<CommentsDataChildEntity> secondLevel) {
        this.secondLevel = secondLevel;
    }
}

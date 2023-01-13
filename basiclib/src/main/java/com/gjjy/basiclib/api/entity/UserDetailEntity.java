package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取用户信息
 */
public class UserDetailEntity extends UidEntity {
    private long userId;            //用户表自增id
    private String nickname;        //昵称
    private String avatarUrl;       //头像
    private int gender;             //性别：0未知，1男、2女
    private String phone;           //手机号码
    private String email;           //邮箱
    private int lightning;          //闪电数量
    private int heart;              //心形数量
    private int experience;         //经验值
    private String inviteCode;      //邀请码
    private String friendInviteCode;//好友邀请码
    private boolean isVip;          //是否会员
    private int vipStatus;          //1试用会员、2正式会员、0不是会员
    private String createTime;      //创建时间
    private long lastTime;          //最后访问时间
    private long vipTime;           //开通会员时间
    private long vipEndTime;        //开通会员结束时间
    private boolean isLock;         //是否冻结
    private String bindType;        //登录绑定类型：FACEBOOK、GOOGLE、APPLE
    private String bindUid;         //登录的平台uid
    private int visitCount;         //访问总天数
    private int wordCount;          //已学关键词总数

    @NonNull
    @Override
    public String toString() {
        return "UserDetailEntity{" +
                "uid='" + getUid() + '\'' +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", lightning=" + lightning +
                ", heart=" + heart +
                ", experience=" + experience +
                ", inviteCode=" + inviteCode +
                ", friendInviteCode=" + friendInviteCode +
                ", isVip=" + isVip +
                ", vipStatus=" + vipStatus +
                ", createTime='" + createTime + '\'' +
                ", lastTime=" + lastTime +
                ", vipTime=" + vipTime +
                ", vipEndTime=" + vipEndTime +
                ", isLock=" + isLock +
                ", bindType='" + bindType + '\'' +
                ", bindUid='" + bindUid + '\'' +
                ", visitCount=" + visitCount +
                ", wordCount=" + wordCount +
                '}';
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getGender() { return gender; }
    public void setGender(int gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLightning() { return lightning; }
    public void setLightning(int lightning) { this.lightning = lightning; }

    public int getHeart() { return heart; }
    public void setHeart(int heart) { this.heart = heart; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String code) { inviteCode = code; }

    public String getFriendInviteCode() { return friendInviteCode; }
    public void setFriendInviteCode(String code) { friendInviteCode = code; }

    public boolean isVip() { return isVip; }
    public void setVip(boolean vip) { isVip = vip; }

    public int getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public long getLastTime() { return lastTime; }
    public void setLastTime(long lastTime) { this.lastTime = lastTime; }

    public long getVipTime() { return vipTime; }
    public void setVipTime(long vipTime) { this.vipTime = vipTime; }

    public long getVipEndTime() { return vipEndTime; }
    public void setVipEndTime(long vipEndTime) { this.vipEndTime = vipEndTime; }

    public boolean isLock() { return isLock; }
    public void setLock(boolean lock) { isLock = lock; }

    public String getBindType() { return bindType; }
    public void setBindType(String bindType) { this.bindType = bindType; }

    public String getBindUid() { return bindUid; }
    public void setBindUid(String bindUid) { this.bindUid = bindUid; }

    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
}

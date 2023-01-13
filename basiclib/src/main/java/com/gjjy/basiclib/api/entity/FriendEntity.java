package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

/**
 * 好友信息
 */
public class FriendEntity extends BaseReqEntity {
    private String nickName;                //昵称
    private String avatarUrl;               //头像链接
    private String vipEndTime;              //vip结束毫秒时间戳
    private int isVip;                      //是否VIP：1是、0否

    @NonNull
    @Override
    public String toString() {
        return "FriendEntity{" +
                "nickName='" + nickName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", vipEndTime='" + vipEndTime + '\'' +
                ", isVip=" + isVip +
                '}';
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getVipEndTime() {
        return vipEndTime;
    }

    public void setVipEndTime(String vipEndTime) {
        this.vipEndTime = vipEndTime;
    }

    public boolean isVip() {
        return ObjUtils.parseBoolean( isVip );
    }
    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }
}

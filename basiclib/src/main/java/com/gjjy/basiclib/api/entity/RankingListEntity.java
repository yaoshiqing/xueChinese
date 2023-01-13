package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

import java.util.List;

/**
 * 获取经验值排行榜列表
 */
public class RankingListEntity extends BaseReqEntity {
    private List<RankingEntity> lists;  //排名列表
    private int last;                   //上一次保存的排名

    @NonNull
    @Override
    public String toString() {
        return "RankingListEntity{" +
                "lists=" + lists +
                ", last=" + last +
                '}';
    }

    public List<RankingEntity> getLists() { return lists; }
    public void setLists(List<RankingEntity> lists) { this.lists = lists; }

    public int getLast() { return last; }
    public void setLast(int last) { this.last = last; }

    public static class RankingEntity {
        private long userId;           //用户自增id
        private String nickName;       //昵称
        private String expSum;         //头像
        private String avatarUrl;      //经验值
        private int isVip;

        @NonNull
        @Override
        public String toString() {
            return "RankingEntity{" +
                    "userId=" + userId +
                    ", nickName='" + nickName + '\'' +
                    ", expSum='" + expSum + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", isVip=" + isVip +
                    '}';
        }

        public long getUserId() { return userId; }
        public void setUserId(long userId) { this.userId = userId; }

        public String getNickName() { return nickName; }
        public void setNickName(String nickName) { this.nickName = nickName; }

        public String getExpSum() { return expSum; }
        public void setExpSum(String expSum) { this.expSum = expSum; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

        public int getIsVip() {
            return isVip;
        }
        public boolean isVip() { return ObjUtils.parseBoolean( isVip ); }

        public void setIsVip(int isVip) {
            this.isVip = isVip;
        }
    }
}

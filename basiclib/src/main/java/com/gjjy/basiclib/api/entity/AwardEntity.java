package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取用户奖励信息
 */
public class AwardEntity extends BaseReqEntity {
    private int lightning = 0;               //闪电
    private int heart = 0;                   //心形

    private int oldLightning = 0;
    private int oldHeart = 0;

    public AwardEntity() {}
    public AwardEntity(int lightning, int heart) {
        this.lightning = lightning;
        this.heart = heart;
        oldLightning = lightning;
        oldHeart = heart;
    }

    @NonNull
    @Override
    public String toString() {
        return "AwardEntity{" +
                "lightning=" + lightning +
                ", heart=" + heart +
                ", oldLightning=" + oldLightning +
                ", oldHeart=" + oldHeart +
                '}';
    }


    public int getLightning() { return lightning; }
    public void setLightning(int lightning) { this.lightning = lightning; }

    public int getHeart() { return heart; }
    public void setHeart(int heart) { this.heart = heart; }

    public int getOldLightning() { return oldLightning; }
    public void setOldLightning(int oldLightning) { this.oldLightning = oldLightning; }

    public int getOldHeart() { return oldHeart; }
    public void setOldHeart(int oldHeart) { this.oldHeart = oldHeart; }
}

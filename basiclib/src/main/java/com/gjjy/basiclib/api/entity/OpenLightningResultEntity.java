package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 闪电奖励结果
 */
public class OpenLightningResultEntity extends AnswerExpEntity {
    private int isOpenLightning;     //分类id

    @NonNull
    @Override
    public String toString() {
        return "OpenLightningResultEntity{" +
                "isOpenLightning=" + isOpenLightning +
                '}';
    }

    public int getIsOpenLightning() {
        return isOpenLightning;
    }

    public boolean isOpenLightning() {
        return isOpenLightning == 1;
    }

    public OpenLightningResultEntity setIsOpenLightning(int isOpenLightning) {
        this.isOpenLightning = isOpenLightning;
        return this;
    }

    public OpenLightningResultEntity setOpenLightning(boolean isOpenLightning) {
        this.isOpenLightning = isOpenLightning ? 1 : 0;
        return this;
    }
}

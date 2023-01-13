package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 生成邀请码
 */
public class InviteCodeEntity extends BaseReqEntity {
    private String inviteCode;             //领取经验值

    @NonNull
    @Override
    public String toString() {
        return "InviteCodeEntity{" +
                "inviteCode='" + inviteCode + '\'' +
                '}';
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}

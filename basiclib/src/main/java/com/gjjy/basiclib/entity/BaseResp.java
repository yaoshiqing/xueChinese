package com.gjjy.basiclib.entity;

import androidx.annotation.NonNull;

public class BaseResp {
    // 新字段 返回 errCode 0 正常
    private int errCode = -99;
    // 新字段 返回 errMsg 错误处理
    private String errMsg;
    // 是否成功
    private boolean success;


    @NonNull
    @Override
    public String toString() {
        return "BaseReq{" +
                ", errCode=" + errCode +
                ", errMsg=" + errMsg +
                ", success=" + success +
                '}';
    }


    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

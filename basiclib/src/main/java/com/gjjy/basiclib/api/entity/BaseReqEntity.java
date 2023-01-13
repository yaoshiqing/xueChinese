package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

public class BaseReqEntity {
    private int code;
    private String msg;
    private String data;
    private int errorNumber;
    // 新字段 返回 errCode 0 正常
    private int errCode = -99;
    // 新字段 返回 errMsg 错误处理
    private String errMsg;
    private boolean success;

    @NonNull
    @Override
    public String toString() {
        return "BaseReqEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                ", errorNumber=" + errorNumber +
                ", errCode=" + errCode +
                ", errMsg=" + errMsg +
                ", success=" + success +
                '}';
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public int getErrorNumber() { return errorNumber; }
    public void setErrorNumber(int errorNumber) { this.errorNumber = errorNumber; }

    // 新接口 errCode 0
    public boolean isSuccess() { return code == 1 || errCode == 0; }

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

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

public class StatusEntity extends BaseReqEntity {
    private int status;

    @NonNull
    @Override
    public String toString() {
        return "StatusEntity{" +
                "status=" + status +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

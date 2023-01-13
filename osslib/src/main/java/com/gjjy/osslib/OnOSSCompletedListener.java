package com.gjjy.osslib;

public interface OnOSSCompletedListener {
    void onSuccess(String url, String objectName, int index, int count);
    void onFailure(String url, String objectName, int index, int count);
}
package com.gjjy.osslib.entity;

import androidx.annotation.NonNull;

public class OssData {
    private final String objectName;
    private final String filePath;

    public OssData(String objectName, String filePath) {
        this.objectName = objectName;
        this.filePath = filePath;
    }

    @NonNull
    @Override
    public String toString() {
        return "OssData{" +
                "objectName='" + objectName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getObjectName() { return objectName; }

    public String getFilePath() { return filePath; }
}
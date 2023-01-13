package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 检测版本更新信息
 */
public class CheckUpdateEntity extends BaseReqEntity {
    private String url;             //版本下载地址
    private String description;     //版本描述
    private String newVersion;      //最新版本号
    private int status;             //状态：2强制更新，1可以更新，0不需要更新

    @NonNull
    @Override
    public String toString() {
        return "CheckUpdateEntity{" +
                "url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", newVersion='" + newVersion + '\'' +
                ", status=" + status +
                '}';
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNewVersion() { return newVersion; }
    public void setNewVersion(String newVersion) { this.newVersion = newVersion; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}

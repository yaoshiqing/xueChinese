package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 检测公告信息
 */
public class CheckAnnouncementEntity extends BaseReqEntity {
//    private String content;       //公告信息
    private String url;       //公告链接
    private String updateTime;    //更新时间

    @NonNull
    @Override
    public String toString() {
        return "CheckAnnouncementEntity{" +
                "url='" + url + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

//    public String getContent() {
//        return content;
//    }
//
//    public CheckAnnouncementEntity setContent(String content) {
//        this.content = content;
//        return this;
//    }


    public String getUrl() { return url; }
    public CheckAnnouncementEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }
    public CheckAnnouncementEntity setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}

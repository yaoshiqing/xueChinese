package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 *
 */
public class DiscoveryListEntity extends BaseReqEntity {
    private long discoverArticleId;                         //文章表自增id
    private int discoverTypeId;                             //文章类型表自增id
    private String title;                                   //文章标题
    private String bigImgUrl;                               //文章大图片
    private String imgUrl;                                  //文章图片
    private String summary;                                 //文章摘要
    private String explain;                                 //文章描述
    private int level;                                      //等级：1到6（分别代表：HSK1-HSK6）
    private boolean isVip;                                  //是否为会员
    private boolean isNew;                                  //是否标记为最新：1是、0否
    private double readCount;                               //阅读人数
    private String videoId;                                 //音视频id
    private DiscoveryDetailJsonContentEntity jsonContent;
    private DiscoveryDetailJsonRecordEntity jsonRecord;

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryListEntity{" +
                "discoverArticleId=" + discoverArticleId +
                ", discoverTypeId=" + discoverTypeId +
                ", title='" + title + '\'' +
                ", bigImgUrl='" + bigImgUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", summary='" + summary + '\'' +
                ", explain='" + explain + '\'' +
                ", level=" + level +
                ", isVip=" + isVip +
                ", isNew=" + isNew +
                ", readCount=" + readCount +
                ", videoId=" + videoId +
                ", jsonContent=" + getJsonContent() +
                '}';
    }

    public long getDiscoverArticleId() {
        return discoverArticleId;
    }
    public void setDiscoverArticleId(long discoverArticleId) {
        this.discoverArticleId = discoverArticleId;
    }

    public int getDiscoverTypeId() {
        return discoverTypeId;
    }
    public void setDiscoverTypeId(int discoverTypeId) {
        this.discoverTypeId = discoverTypeId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBigImgUrl() {
        return bigImgUrl;
    }
    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getExplain() {
        return explain;
    }

    public DiscoveryListEntity setExplain(String explain) {
        this.explain = explain;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isVip() { return isVip; }

    public void setVip(boolean vip) { isVip = vip; }

    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public double getReadCount() {
        return readCount;
    }
    public void setReadCount(double readCount) {
        this.readCount = readCount;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public DiscoveryDetailJsonContentEntity getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(DiscoveryDetailJsonContentEntity jsonContent) {
        this.jsonContent = jsonContent;
    }

    public DiscoveryDetailJsonRecordEntity getJsonRecord() {
        return jsonRecord;
    }

    public void setJsonRecord(DiscoveryDetailJsonRecordEntity jsonRecord) {
        this.jsonRecord = jsonRecord;
    }
}
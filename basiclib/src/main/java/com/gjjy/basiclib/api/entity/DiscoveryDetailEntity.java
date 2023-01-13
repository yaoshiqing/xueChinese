package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取文章详情
 */
public class DiscoveryDetailEntity extends BaseReqEntity {
    private long discoverArticleId;                        //文章表自增id
    private int discoverTypeId;                            //文章类型表自增id
    private String title;                                  //文章标题
    private String imgUrl;                                 //文章图片
    private String bigImgUrl;                              //文章大图片地址
    private String summary;                                //文章摘要
    private String introduction;                           //介绍
    private String htmlUrl;                                //文章HTML内容URL
    private DiscoveryDetailJsonContentEntity jsonContent;       //文章JSON内容
    private DiscoveryDetailJsonRecordEntity jsonRecord;
    private int level;                                     //等级：1到6（分别代表：HSK1-HSK6）
    private boolean isVip;                                 //是否为会员
    private boolean isNew;                                 //是否标记为最新：1是、0否
    private double baseNum;                                //阅读人数
    private boolean isCollection;                          //是否已收藏：1是，0否

    @NonNull
    @Override
    public String toString() {
        return "DiscoveryDetailEntity{" +
                "discoverArticleId=" + discoverArticleId +
                ", discoverTypeId=" + discoverTypeId +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", bigImgUrl='" + bigImgUrl + '\'' +
                ", summary='" + summary + '\'' +
                ", introduction='" + introduction + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", jsonContent=" + jsonContent +
                ", jsonRecord=" + jsonRecord +
                ", level=" + level +
                ", isVip=" + isVip +
                ", isNew=" + isNew +
                ", baseNum=" + baseNum +
                ", isCollection=" + isCollection +
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @NonNull
    public DiscoveryDetailJsonContentEntity getJsonContent() {
        return jsonContent == null ? new DiscoveryDetailJsonContentEntity() : jsonContent;
    }

    public void setJsonContent(DiscoveryDetailJsonContentEntity jsonContent) {
        this.jsonContent = jsonContent;
    }

    @NonNull
    public DiscoveryDetailJsonRecordEntity getJsonRecord() {
        return jsonRecord == null ? new DiscoveryDetailJsonRecordEntity() : jsonRecord;
    }

    public void setJsonRecord(DiscoveryDetailJsonRecordEntity jsonRecord) {
        this.jsonRecord = jsonRecord;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isVip() { return isVip; }
    public void setVip(boolean isVip) { this.isVip = isVip; }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public double getBaseNum() {
        return baseNum;
    }

    public void setBaseNum(double baseNum) {
        this.baseNum = baseNum;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
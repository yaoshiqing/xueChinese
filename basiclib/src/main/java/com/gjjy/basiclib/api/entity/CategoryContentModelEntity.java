package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.entity.SectionIds;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取分类内容下的模块
 */
public class CategoryContentModelEntity extends BaseReqEntity {
    private int unitId;                                         //模块id
    private String title;                                       //模块标题
    private String imgUrl;                                      //模块图片地址
    private String lockImgUrl;                                  //未解锁图片地址
    private String wordImgUrl;                                  //词语表图标
    private List<CategoryContentModelSectionEntity> section;    //小节数组
    private int unitStatus;                                     //模块状态：0未解锁、1已解锁、2已完成
    private int sectionNum;                                     //当前模块完成小节总数
    private int unlockSeconds;                                  //剩余解锁秒数

    @NonNull
    @Override
    public String toString() {
        return "CategoryContentModelEntity{" +
                "unitId=" + unitId +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", lockImgUrl='" + lockImgUrl + '\'' +
                ", wordImgUrl='" + wordImgUrl + '\'' +
                ", section=" + section +
                ", unitStatus='" + unitStatus + '\'' +
                ", sectionNum='" + sectionNum + '\'' +
                ", unlockSeconds='" + unlockSeconds + '\'' +
                '}';
    }

    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public String getLockImgUrl() { return lockImgUrl; }
    public void setLockImgUrl(String lockImgUrl) { this.lockImgUrl = lockImgUrl; }

    public String getWordImgUrl() { return wordImgUrl; }
    public void setWordImgUrl(String wordImgUrl) { this.wordImgUrl = wordImgUrl; }

    public List<CategoryContentModelSectionEntity> getSection() { return section; }
    public void setSection(List<CategoryContentModelSectionEntity> section) {
        this.section = section;
    }
    @NonNull
    public ArrayList<SectionIds> getSectionIds() {
        ArrayList<SectionIds> ids = new ArrayList<>();
        for ( CategoryContentModelSectionEntity data : getSection() ) {
            ids.add( new SectionIds( data.getSectionId(), data.getTitle() ) );
        }
        return ids;
    }

    public int getUnitStatus() { return unitStatus; }
    public void setUnitStatus(int unitStatus) { this.unitStatus = unitStatus; }

    public int getSectionNum() { return sectionNum; }
    public void setSectionNum(int sectionNum) { this.sectionNum = sectionNum; }

    public int getUnlockSeconds() { return unlockSeconds; }
    public void setUnlockSeconds(int unlockSeconds) { this.unlockSeconds = unlockSeconds; }
}

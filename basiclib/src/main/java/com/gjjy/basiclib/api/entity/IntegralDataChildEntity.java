package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

/**
 * 积分详细记录列表
 */
public class IntegralDataChildEntity extends BaseReqEntity {
    private int id;                 //积分记录表自增ID
    private int userId;             //用户表自增ID
    private String key;             //类型唯一标识
    private String title;           //标题
    private int type;               //积分类型，1获取，2消耗
    private int num;                //数量
    private int isBackd;            //是否后台操作
    private String createTime;     //创建时间
    private String expireTime;     //失效时间

    @NonNull
    @Override
    public String toString() {
        return "IntegralDataChildEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", num=" + num +
                ", isBackd=" + isBackd +
                ", createTime='" + createTime + '\'' +
                ", expireTime='" + expireTime + '\'' +
                '}';
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getNum() { return num; }
    public void setNum(int num) { this.num = num; }

    public int getIsBackd() { return isBackd; }
    public boolean isBackd() { return ObjUtils.parseBoolean( isBackd ); }
    public void setIsBackd(int isBackd) { this.isBackd = isBackd; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }
}

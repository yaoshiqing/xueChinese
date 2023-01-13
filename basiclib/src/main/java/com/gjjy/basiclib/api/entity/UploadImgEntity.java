package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 上传图片
 */
public class UploadImgEntity extends BaseReqEntity {
    private String save;     //上传给服务器的地址
    private String show;     //显示图片的地址
    private String localFilePath;

    @NonNull
    @Override
    public String toString() {
        return "UploadImgEntity{" +
                "save='" + save + '\'' +
                ", show='" + show + '\'' +
                ", localFilePath='" + localFilePath + '\'' +
                '}';
    }

    public String getSave() { return save; }
    public void setSave(String save) { this.save = save; }

    public String getShow() { return show; }
    public void setShow(String show) { this.show = show; }

    public String getLocalFilePath() { return localFilePath; }
    public void setLocalFilePath(String localFilePath) { this.localFilePath = localFilePath; }
}

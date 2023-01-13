package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;

public class BaseFindHolder extends BaseViewHolder {
    private ImageView ivNewFlag;
    private ImageView ivImg;
    private TextView tvToViewCount;
    private TextView tvTitle;
    private TextView tvDuration;
    private TextView tvContent;
    private TextView tvClassifyTag;

    public BaseFindHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
    }

    public ImageView getNewFlag() { return ivNewFlag; }

    public ImageView getImg() { return ivImg; }

    public TextView getToViewCount() { return tvToViewCount; }

    public TextView getTitle() { return tvTitle; }

    public TextView getDuration() { return tvDuration; }

    public TextView getContent() { return tvContent; }

    public TextView getClassifyTag() { return tvClassifyTag; }

    public void setNewFlag(ImageView ivNewFlag) { this.ivNewFlag = ivNewFlag; }

    public void setImg(ImageView ivImg) { this.ivImg = ivImg; }

    public void setToViewCount(TextView tvToViewCount) { this.tvToViewCount = tvToViewCount; }

    public void setTitle(TextView tvTitle) { this.tvTitle = tvTitle; }

    public void setDuration(TextView tvDuration) { this.tvDuration = tvDuration; }

    public void setContent(TextView tvContent) { this.tvContent = tvContent; }

    public void setClassifyTag(TextView tvClassifyTag) {
        this.tvClassifyTag = tvClassifyTag;
    }
}

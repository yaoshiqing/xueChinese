package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;

public class BaseListenDailyHolder extends BaseViewHolder {
    private ImageView ivNewFlag;
    private ImageView ivImg;
    private TextView tvToViewCount;
    private TextView tvTitle;
    private TextView tvContent;
    private ShapeTextView stvClassifyTag;

    public BaseListenDailyHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
    }

    public ImageView getNewFlag() { return ivNewFlag; }

    public ImageView getImg() { return ivImg; }

    public TextView getToViewCount() { return tvToViewCount; }

    public TextView getTitle() { return tvTitle; }

    public TextView getContent() { return tvContent; }

    public ShapeTextView getClassifyTag() { return stvClassifyTag; }


    void setNewFlag(ImageView ivNewFlag) { this.ivNewFlag = ivNewFlag; }

    void setImg(ImageView ivImg) { this.ivImg = ivImg; }

    void setToViewCount(TextView tvToViewCount) { this.tvToViewCount = tvToViewCount; }

    void setTitle(TextView tvTitle) { this.tvTitle = tvTitle; }

    void setContent(TextView tvContent) { this.tvContent = tvContent; }

    void setClassifyTag(ShapeTextView stvClassifyTag) { this.stvClassifyTag = stvClassifyTag; }
}

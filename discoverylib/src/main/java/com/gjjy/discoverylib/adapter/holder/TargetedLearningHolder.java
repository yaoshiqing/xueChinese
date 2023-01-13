package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.discoverylib.R;

public class TargetedLearningHolder extends BaseViewHolder {
    private final ImageView ivNewFlag;
    private final ImageView ivImg;
    private final TextView tvTitle;
    private final TextView tvContent;

    public TargetedLearningHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ivNewFlag = findViewById( R.id.item_targeted_learning_iv_new_flag );
        ivImg = findViewById( R.id.item_targeted_learning_iv_img );
        tvTitle = findViewById( R.id.item_targeted_learning_tv_title );
        tvContent = findViewById( R.id.item_targeted_learning_tv_content );
    }

    public ImageView getNewFlag() { return ivNewFlag; }

    public ImageView getImg() { return ivImg; }

    public TextView getTitle() { return tvTitle; }

    public TextView getContent() { return tvContent; }
}

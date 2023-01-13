package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.MaskImageView;
import com.gjjy.frontlib.R;

public class ChildItemHolder extends BaseViewHolder {
    private final MaskImageView mivImg;
    private final ImageView ivReward;
    private final TextView tvTitle;

    public ChildItemHolder(@NonNull View v) {
        super(v);
        mivImg = v.findViewById( R.id.item_front_list_child_miv_img );
        ivReward = v.findViewById( R.id.item_front_list_child_iv_reward );
        tvTitle = v.findViewById( R.id.item_front_list_child_tv_title );
    }

    public MaskImageView getImg() { return mivImg; }

    public ImageView getReward() { return ivReward; }

    public TextView getTitle() { return tvTitle; }
}

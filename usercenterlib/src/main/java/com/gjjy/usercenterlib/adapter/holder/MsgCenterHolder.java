package com.gjjy.usercenterlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.RedDotView;
import com.gjjy.usercenterlib.R;

public class MsgCenterHolder extends BaseViewHolder {
    private final RedDotView rdlRedDotView;
    private final ImageView ivImg;
    private final TextView tvTitle;

    public MsgCenterHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        rdlRedDotView = findViewById( R.id.msg_center_rd_red_dot );
        ivImg = findViewById( R.id.msg_center_iv_img );
        tvTitle = findViewById( R.id.msg_center_tv_title );
    }

    public ImageView getImg() { return ivImg; }

    public TextView getTitle() { return tvTitle; }

    public void setEnableRedDot(boolean enable) {
        rdlRedDotView.setVisibility( enable ? View.VISIBLE : View.GONE );
    }
}

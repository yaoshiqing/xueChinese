package com.gjjy.usercenterlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.RedDotView;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;
import com.gjjy.usercenterlib.R;

public class MsgListHolder extends BaseViewHolder {
    private final ImageView ivImg;
    private final ShapeTextView stvDate;
    private final TextView tvTitle;
    private final TextView tvContent;
    private final RedDotView rdvRedDot;

    public MsgListHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ivImg = findViewById( R.id.msg_notify_iv_img );
        stvDate = findViewById( R.id.msg_notify_stv_date );
        tvTitle = findViewById( R.id.msg_notify_tv_title );
        tvContent = findViewById( R.id.msg_notify_tv_content );
        rdvRedDot = findViewById( R.id.msg_notify_rdl_red_dot );
    }

    public ImageView getImg() { return ivImg; }

    public TextView getTitle() { return tvTitle; }

    public ShapeTextView getDate() { return stvDate; }

    public TextView getContent() { return tvContent; }

    public RedDotView getRedDot() { return rdvRedDot; }
}

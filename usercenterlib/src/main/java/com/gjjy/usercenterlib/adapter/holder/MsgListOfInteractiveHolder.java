package com.gjjy.usercenterlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.gjjy.usercenterlib.R;

public class MsgListOfInteractiveHolder extends BaseViewHolder {
    private final ShapeImageView sivPhoto;
    private final ImageView ivVipIcon;
    private final TextView tvUserName;
    private final TextView tvTime;
    private final TextView tvInteractContent;
    private final ShapeImageView sivMyImg;
    private final TextView tvMyContent;

    public MsgListOfInteractiveHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        sivPhoto = findViewById( R.id.item_msg_list_of_interactive_siv_photo );
        ivVipIcon = findViewById( R.id.item_msg_list_of_interactive_iv_vip_icon );
        tvUserName = findViewById( R.id.item_msg_list_of_interactive_tv_user_name );
        tvTime = findViewById( R.id.item_msg_list_of_interactive_tv_time );
        tvInteractContent = findViewById( R.id.item_msg_list_of_interactive_tv_interact_content );
        sivMyImg = findViewById( R.id.item_msg_list_of_interactive_siv_my_img );
        tvMyContent = findViewById( R.id.item_msg_list_of_interactive_tv_my_content );
    }

    public ShapeImageView getPhoto() {
        return sivPhoto;
    }

    public ImageView getVipIcon() {
        return ivVipIcon;
    }

    public TextView getUserName() {
        return tvUserName;
    }

    public TextView getTime() {
        return tvTime;
    }

    public TextView getInteractContent() { return tvInteractContent; }

    public ShapeImageView getMyImg() {
        return sivMyImg;
    }

    public TextView getMyContent() {
        return tvMyContent;
    }
}

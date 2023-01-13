package com.gjjy.usercenterlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.gjjy.usercenterlib.R;

public class FriendsHolder extends BaseViewHolder {
    private final ShapeImageView sivPhoto;
    private final ImageView ivVipIcon;
    private final TextView tvName;

    public FriendsHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ViewGroup.LayoutParams lpItemView = getItemView().getLayoutParams();
        lpItemView.height = Utils.dp2Px( getContext(), 75 );

        findViewById( R.id.item_user_photo_tv_name ).setVisibility( View.GONE );
        findViewById( R.id.item_user_list_tv_index ).setVisibility( View.GONE );
        findViewById( R.id.item_user_list_tv_xp ).setVisibility( View.GONE );

        sivPhoto = findViewById( R.id.item_user_photo_siv_img );
        ivVipIcon = findViewById( R.id.item_user_photo_iv_vip_icon );
        tvName = findViewById( R.id.item_user_list_tv_name );

        findViewById( R.id.item_user_list_inc_ll_user_photo )
                .setPadding( Utils.dp2Px( getContext(), 20 ), 0, 0, 0 );
        //固定头像高宽
        ViewGroup.LayoutParams lpPhoto = sivPhoto.getLayoutParams();
        lpPhoto.width = lpPhoto.height = Utils.dp2Px( parent.getContext(), 62 );
        sivPhoto.setLayoutParams( lpPhoto );
        //固定vip图标高宽
        ViewGroup.LayoutParams lpVipIcon = ivVipIcon.getLayoutParams();
        lpVipIcon.width = lpVipIcon.height = Utils.dp2Px( parent.getContext(), 20 );
        ivVipIcon.setLayoutParams( lpVipIcon );
    }

    public ShapeImageView getPhoto() { return sivPhoto; }

    public ImageView getVipIcon() { return ivVipIcon; }

    public TextView getName() { return tvName; }
}

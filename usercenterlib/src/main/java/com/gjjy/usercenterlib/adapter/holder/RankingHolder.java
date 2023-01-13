package com.gjjy.usercenterlib.adapter.holder;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybcomponent.widget.shape.ShapeLinearLayout;
import com.gjjy.usercenterlib.R;

public class RankingHolder extends BaseViewHolder {
    private final TextView tvIndex;
    private final ShapeImageView sivPhoto;
    private final ImageView ivVipIcon;
    private final TextView tvName;
    private final TextView tvXP;

    public RankingHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvIndex = findViewById( R.id.item_user_list_tv_index );
        sivPhoto = findViewById( R.id.item_user_photo_siv_img );
        ivVipIcon = findViewById( R.id.item_user_photo_iv_vip_icon );
        tvName = findViewById( R.id.item_user_list_tv_name );
        tvXP = findViewById( R.id.item_user_list_tv_xp );

        //隐藏头像自带的名字
        findViewById( R.id.item_user_photo_tv_name ).setVisibility( View.GONE );
        //固定头像高宽
        ViewGroup.LayoutParams lpPhoto = sivPhoto.getLayoutParams();
        lpPhoto.width = lpPhoto.height = Utils.dp2Px( parent.getContext(), 40 );
        sivPhoto.setLayoutParams( lpPhoto );
        //固定vip图标高宽
        ViewGroup.LayoutParams lpVipIcon = ivVipIcon.getLayoutParams();
        lpVipIcon.width = lpVipIcon.height = Utils.dp2Px( parent.getContext(), 13 );
        ivVipIcon.setLayoutParams( lpVipIcon );
    }

    public void setUser(boolean isUser) {
        ShapeLinearLayout sll = (ShapeLinearLayout) getItemView();
        int resColorId = isUser ? R.color.colorRankingCurrentIndex : R.color.colorEF;
        int color;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            color = getContext().getColor( resColorId );
        }else {
            color = getContext().getResources().getColor( resColorId );
        }
        sll.setBorderColor( color );
    }

    public TextView getIndex() { return tvIndex; }

    public ShapeImageView getPhoto() { return sivPhoto; }

    public ImageView getVipIcon() { return ivVipIcon; }

    public TextView getName() { return tvName; }

    public TextView getXP() { return tvXP; }
}

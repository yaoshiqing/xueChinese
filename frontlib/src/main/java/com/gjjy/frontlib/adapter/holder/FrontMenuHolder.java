package com.gjjy.frontlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.frontlib.R;

public class FrontMenuHolder extends BaseViewHolder {
    private final ImageView ivImg;
    private final TextView tvTitle;

    public FrontMenuHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ivImg = getItemView().findViewById( R.id.item_front_menu_tv_img );
        tvTitle = getItemView().findViewById( R.id.item_front_menu_tv_title );
    }

    public ImageView getImg() { return ivImg; }

    public TextView getTitle() { return tvTitle; }
}

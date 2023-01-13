package com.gjjy.basiclib.widget.catx_video.entity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.IdRes;

public class PlayerOperationTitleBar extends BasePlayOperationBar {
    private ImageView vBackBtn;

    public PlayerOperationTitleBar(Context context, int layoutRes) {
        super(context, layoutRes);
    }

    public PlayerOperationTitleBar(View vLayout) {
        super(vLayout);
    }


    public ImageView getBackButton() {
        return vBackBtn;
    }

    public PlayerOperationTitleBar setBackButton(@IdRes int resId) {
        vBackBtn = getLayout().findViewById( resId );
        return this;
    }


}

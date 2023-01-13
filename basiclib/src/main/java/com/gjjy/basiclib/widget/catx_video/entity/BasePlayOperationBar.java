package com.gjjy.basiclib.widget.catx_video.entity;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;

public class BasePlayOperationBar {
    private View vLayout;

    private BasePlayOperationBar() {}

    public BasePlayOperationBar(Context context, @LayoutRes int layoutRes) {
        vLayout = View.inflate( context, layoutRes, null );
    }

    public BasePlayOperationBar(View vLayout) {
        this.vLayout = vLayout;
    }

    public View getLayout() {
        return vLayout;
    }

    public void showLayout() {
        if( vLayout != null ) vLayout.setVisibility( View.VISIBLE );
    }

    public void hideLayout() {
        if( vLayout != null ) vLayout.setVisibility( View.INVISIBLE );
    }

    public void hideLayoutOfGone() {
        if( vLayout != null ) vLayout.setVisibility( View.GONE );
    }
}

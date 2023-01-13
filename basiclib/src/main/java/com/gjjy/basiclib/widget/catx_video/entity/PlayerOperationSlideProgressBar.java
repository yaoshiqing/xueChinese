package com.gjjy.basiclib.widget.catx_video.entity;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IdRes;

public class PlayerOperationSlideProgressBar extends BasePlayOperationBar {
    private SeekBar sbProgressSeekBar;
    private TextView tvProgressTextView;

    public PlayerOperationSlideProgressBar(Context context, int layoutRes) {
        super(context, layoutRes);
    }

    public PlayerOperationSlideProgressBar(View vLayout) {
        super(vLayout);
    }

    public SeekBar getProgressSeekBar() {
        return sbProgressSeekBar;
    }

    public PlayerOperationSlideProgressBar setProgressSeekBar(@IdRes int resId) {
        sbProgressSeekBar = getLayout().findViewById( resId );
        return this;
    }

    public TextView getProgressTextView() {
        return tvProgressTextView;
    }

    public PlayerOperationSlideProgressBar setProgressTextView(@IdRes int resId) {
        tvProgressTextView = getLayout().findViewById( resId );
        return this;
    }
}

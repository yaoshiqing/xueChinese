package com.gjjy.basiclib.widget.catx_video.entity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.gjjy.basiclib.R;


public class PlayerOperationControlBar extends BasePlayOperationBar {
    private SeekBar sbProgressSeekBar;
    private TextView tvStartTimeTextView;
    private TextView tvTotalTimeTextView;
    private ImageView ivPlayButtonImageView;
    private ImageView ivFlipButtonImageView;

    private int mPlayResId = R.drawable.ic_play;
    private int mPauseResId = R.drawable.ic_pause;
    private int mFlipLandscapeResId = R.drawable.ic_flip_landscape;
    private int mFlipPortraitResId = R.drawable.ic_flip_portrait;

    public PlayerOperationControlBar(Context context, int layoutRes) {
        super(context, layoutRes);
    }

    public PlayerOperationControlBar(View vLayout) {
        super(vLayout);
    }


    public SeekBar getProgressSeekBar() { return sbProgressSeekBar; }

    public PlayerOperationControlBar setProgressSeekBar(@IdRes int resId) {
        sbProgressSeekBar = getLayout().findViewById( resId );
        return this;
    }

    public TextView getStartTimeTextView() {
        return tvStartTimeTextView;
    }

    public PlayerOperationControlBar setStartTimeTextView(@IdRes int resId) {
        tvStartTimeTextView = getLayout().findViewById( resId );
        return this;
    }

    public TextView getTotalTimeTextView() {
        return tvTotalTimeTextView;
    }

    public PlayerOperationControlBar setTotalTimeTextView(@IdRes int resId) {
        tvTotalTimeTextView = getLayout().findViewById( resId );
        return this;
    }

    public ImageView getPlayButtonImageView() {
        return ivPlayButtonImageView;
    }

    public PlayerOperationControlBar setPlayButtonImageView(@IdRes int resId) {
        ivPlayButtonImageView = getLayout().findViewById( resId );
        return this;
    }

    public ImageView getFlipButtonImageView() {
        return ivFlipButtonImageView;
    }

    public PlayerOperationControlBar setFlipButtonImageView(@IdRes int resId) {
        ivFlipButtonImageView = getLayout().findViewById( resId );
        return this;
    }

    public PlayerOperationControlBar setPlayButtonRes(@DrawableRes int playResId,
                                                      @DrawableRes int pauseResId) {
        mPlayResId = playResId;
        mPauseResId = pauseResId;
        return this;
    }

    public int getPlayResId() { return mPlayResId; }
    public int getPauseResId() { return mPauseResId; }

    public PlayerOperationControlBar setFlipButtonRes(@DrawableRes int flipLandscapeResId,
                                                      @DrawableRes int flipPortraitResId) {
        mFlipLandscapeResId = flipLandscapeResId;
        mFlipPortraitResId = flipPortraitResId;
        return this;
    }

    public int getFlipLandscapeResId() { return mFlipLandscapeResId; }
    public int getFlipPortraitResId() { return mFlipPortraitResId; }
}

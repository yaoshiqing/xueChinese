package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class RatingView extends LinearLayout {
    private Drawable mIconRes;
    private Drawable mBgIconRes;

    private int mRating;
    private int mMax;
    private int mIconSize = LayoutParams.WRAP_CONTENT;
    private int mMargins;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation( HORIZONTAL );
        setGravity( Gravity.CENTER );
    }


    public void setIcon(@DrawableRes int resId) {
        mIconRes = getResources().getDrawable( resId );
    }

    public void setIcon(Drawable drawable) { mIconRes = drawable; }

    public void setIconSize(int size) {
        mIconSize = size;
    }

    public void setIconMargins(int margins) { mMargins = margins; }

    public int getRating() { return mRating; }

    public void setRating(int rating) {
        if( rating < 0 ) return;
        mRating = rating;

        for (int i = 0; i < mMax; i++) {
            ImageView iv = (ImageView) getChildAt( i );
            iv.setImageDrawable( i <= rating ? mIconRes : mBgIconRes );
            iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        }
    }

    public int getMax() { return mMax; }
    public void setMax(int max) {
        mMax = max;
        removeAllViews();
        for (int i = 0; i < max; i++) {
            addView( createStarView() );
        }
    }

    public void setBackgroundIcon(@DrawableRes int resId) {
        mBgIconRes = getResources().getDrawable( resId );
    }

    public void setBackgroundIcon(Drawable drawable) { mBgIconRes = drawable; }

    public void showBackgroundIcon(int index) {
        try {
            getChildAt( index ).setVisibility( VISIBLE );
        }catch(Exception e) {
            e.printStackTrace();
        }
//        ((ImageView)getChildAt( index )).setImageDrawable( mBgIconRes );
    }

    public void hideBackgroundIcon(int index) {
        try {
            getChildAt( index ).setVisibility( INVISIBLE );
        }catch(Exception e) {
            e.printStackTrace();
        }
//        ((ImageView)getChildAt( index )).setImageDrawable( null );
    }

    private ImageView createStarView() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams( mIconSize, mIconSize );
        lp.setMargins( mMargins, mMargins , mMargins, mMargins );
        iv.setLayoutParams( lp );
        return iv;
    }
}

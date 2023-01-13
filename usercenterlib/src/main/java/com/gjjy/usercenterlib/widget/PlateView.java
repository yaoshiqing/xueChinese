package com.gjjy.usercenterlib.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ybear.ybcomponent.Utils;
import com.gjjy.usercenterlib.R;

public class PlateView extends LinearLayout {
    private ImageView ivImg;
    private TextView tvTitle;
    private TextView tvNumber;

    public PlateView(Context context) {
        this(context, null);
    }

    public PlateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation( VERTICAL );
        setGravity( Gravity.CENTER );
        setFocusable( true );

        ivImg = addImgView();
        tvNumber = addNumberView();
        tvTitle = addTitleView();
    }

    private ImageView addImgView() {
//        int size = Utils.dp2Px( getContext(), 30 );
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = Utils.dp2Px( getContext(), 14 );
        iv.setLayoutParams( lp );
        iv.setVisibility( GONE );
        addView( iv );
        return iv;
    }

    private TextView addTitleView() {
        TextView tv = new TextView( getContext() );
        tv.setTextSize( 14 );
        tv.setTextColor( getResources().getColor( R.color.color99 ) );
        tv.setGravity( Gravity.CENTER );
        tv.setVisibility( GONE );
        addView( tv );
        return tv;
    }

    private TextView addNumberView() {
        TextView tv = new TextView( getContext() );
        tv.setTextSize( 19 );
        tv.setTextColor( getResources().getColor( R.color.color66 ) );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setGravity( Gravity.CENTER );
        tv.setVisibility( GONE );
        addView( tv );
        return tv;
    }

    public void setIconResource(@DrawableRes int resId) {
        ivImg.setImageResource( resId );
        ivImg.setVisibility( VISIBLE );
    }

    public void setTitle(CharSequence s) {
        tvTitle.setText( s );
        tvTitle.setVisibility( VISIBLE );
    }

    public void setTitle(@StringRes int res) {
        tvTitle.setText( res );
        tvTitle.setVisibility( VISIBLE );
    }

    public void setNumber(CharSequence s) {
        tvNumber.setText( s );
        tvNumber.setVisibility( VISIBLE );
    }

    public void setNumber(int f) { setNumber( String.valueOf( f ) ); }
}
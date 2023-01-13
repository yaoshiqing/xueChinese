package com.gjjy.basiclib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.R;
import com.ybear.ybnetworkutil.network.NetworkUtil;
import com.ybear.ybutils.utils.Utils;

public class NetworkErrorView extends LinearLayout {
    private TextView tvRefreshBtn;

    private OnClickListener mOnRefreshClickListener;

    public NetworkErrorView(Context context) {
        this(context, null);
    }

    public NetworkErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initView();
        initListener();
    }

    private void init() {
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.gravity = Gravity.CENTER;
        setLayoutParams( lp );

        setOrientation( VERTICAL );
        setBackgroundColor( Color.WHITE );
        setGravity( Gravity.CENTER );
    }

    private void initView() {
        addIconView();
        tvRefreshBtn = addRefreshBtnView();
    }

    private void initListener() {
        tvRefreshBtn.setOnClickListener(v -> {
            if( !NetworkUtil.isNetwork(  v.getContext()  ) ) return;
            if( mOnRefreshClickListener != null ) mOnRefreshClickListener.onClick( v );
        });
    }

    public void show() { setVisibility( VISIBLE ); }

    public void hide() { setVisibility( GONE ); }

    public boolean isShowing() { return getVisibility() == VISIBLE; }

    public void setOnRefreshClickListener(OnClickListener l) {
        mOnRefreshClickListener = l;
    }

    private TextView addIconView() {
        TextView tv = new TextView( getContext() );
        tv.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.ic_network_error_icon,
                0,
                0
        );
        tv.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 38 ) );
        tv.setText( R.string.stringNetworkError );
        tv.setTextSize( 15 );
        tv.setTextColor( getResources().getColor( R.color.color66 ) );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setGravity( Gravity.CENTER );

        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.CENTER;
        addView( tv, lp );
        return tv;
    }

    private TextView addRefreshBtnView() {
        TextView tv = new TextView( getContext() );
        tv.setText( R.string.stringRefreshBtn );
        tv.setTextSize( 15 );
        tv.setTextColor( Color.WHITE );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setBackgroundResource( R.drawable.ic_refresh_btn );
//        tv.setBackground( getResources().getDrawable( R.drawable.ic_refresh_btn ) );
        tv.setGravity( Gravity.CENTER );
        tv.setFocusable( true );
        tv.setClickable( true );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.CENTER;
        lp.topMargin = Utils.dp2Px( getContext(), 35 );
        addView( tv, lp );
        return tv;
    }
}
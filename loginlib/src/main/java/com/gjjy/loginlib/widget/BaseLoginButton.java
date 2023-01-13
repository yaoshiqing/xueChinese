package com.gjjy.loginlib.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;

import com.ybear.ybcomponent.Utils;

public class BaseLoginButton extends FrameLayout implements View.OnClickListener {
    private TextView tvContent;

//    private boolean isSelect = false;
    private OnClickListener mOnClickListener;

    private boolean mEnableClick = true;

    public BaseLoginButton(Context context) {
        this(context, null);
    }

    public BaseLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setEnableClick(boolean enable) { mEnableClick = enable; }

    private void init() {
        setMinimumWidth( Utils.dp2Px( getContext(), 260 ) );
        setMinimumHeight( Utils.dp2Px( getContext(), 52 ) );
        setFocusable( true );
        setClickable( true );
        super.setOnClickListener(v -> {
            if( mOnIsEnableClickListener != null ) mOnIsEnableClickListener.accept( mEnableClick );
            if( !mEnableClick ) return;
            BaseLoginButton.this.onClick( v );
        });

        tvContent = addContentView();

//        switchTouchStatus( false );
    }

    private TextView addContentView() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.leftMargin = Utils.dp2Px( getContext(), 56 );
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        tv.setLayoutParams( lp );
        tv.setTextSize( 19 );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 15 ) );
        tv.setGravity( Gravity.CENTER );
        addView( tv );
        return tv;
    }

    public void setIcon(@DrawableRes int resId) {
        tvContent.setCompoundDrawablesWithIntrinsicBounds( resId, 0, 0, 0 );
    }

    public void setText(CharSequence s) { tvContent.setText( s ); }

    public void setText(@StringRes int resId) { tvContent.setText( resId ); }

    public void setTextColor(@ColorInt int color) {
        tvContent.setTextColor( color );
    }

    @Override
    public void onClick(View v) {
//        isSelect = !isSelect;
//        switchTouchStatus( isSelect );
        if( mOnClickListener != null ) mOnClickListener.onClick( v );
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) { mOnClickListener = l; }

    private Consumer<Boolean> mOnIsEnableClickListener;
    public void setOnIsEnableClickListener(Consumer<Boolean> l) {
        mOnIsEnableClickListener = l;
    }

//    public void switchTouchStatus(boolean isTouch) {
//        tvContent.setTextColor( getResources().getColor(
//                isTouch ? R.color.colorCA : R.color.colorWhite
//        ));
//        setBackgroundResource( isTouch ? R.color.colorWhite : R.color.colorLogin );
//    }
}

package com.gjjy.login.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.ybear.ybutils.utils.Utils;

/**
 * 登录按钮
 */
public abstract class BaseButton extends AppCompatTextView implements View.OnClickListener {
    public BaseButton(Context context) {
        this(context, null, 0);
    }
    public BaseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        int p = Utils.dp2Px( getContext(), 5 );
        setCompoundDrawablePadding( p );
        setTextSize( 18 );
        setTypeface(Typeface.defaultFromStyle( Typeface.BOLD ) );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation( 2 );
        }
        setPadding( p, p, p, p );
        setGravity( Gravity.CENTER );
        setFocusable( true );
        setClickable( true );
        setOnClickListener( this );
        setTag( getClass().getSimpleName() );
    }

    public void setIcon(@DrawableRes int res) {
        setCompoundDrawablesWithIntrinsicBounds( res, 0, 0, 0 );
    }

    public void setIcon(Drawable res) {
        setCompoundDrawablesWithIntrinsicBounds( res, null, null, null );
    }
}

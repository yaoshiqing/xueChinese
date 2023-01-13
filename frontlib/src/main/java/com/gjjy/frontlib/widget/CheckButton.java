package com.gjjy.frontlib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;
import com.gjjy.frontlib.R;

/**
 * 检查按钮
 */
public class CheckButton extends ShapeTextView {
    public CheckButton(Context context) {
        this(context, null);
    }

    public CheckButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMinWidth( Utils.dp2Px( getContext(), 280 ) );
        setMinHeight( Utils.dp2Px( getContext(), 51 ) );
//        setRadius( Utils.dp2Px( getContext(), 15 ) );
//        setShadowRadius( Utils.dp2Px( getContext(), 4 ) );
//        setShadowOffsetX(  Utils.dp2Px( getContext(), 4 ) );
//        setShadowOffsetY(  Utils.dp2Px( getContext(), 4 ) );
//        setShadowColor( getResources().getColor( R.color.colorShadow ) );
        setText( getResources().getString( R.string.stringCheck ) );
        setTextColor( Color.WHITE );
        setTextSize( 22 );
        setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        setGravity( Gravity.CENTER );
        setFocusable( true );
        setClickable( true );

        setEnabled( false );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if( !isEnabled() ) return super.onTouchEvent(ev);
        switch ( ev.getAction() ) {
            case MotionEvent.ACTION_DOWN: case MotionEvent.ACTION_CANCEL:
//                setTextColor( getResources().getColor( R.color.colorCheckBtnTextFalse ) );
                break;
            case MotionEvent.ACTION_UP:
//                setTextColor( Color.WHITE );
                break;
        }
        return super.onTouchEvent(ev);
    }

    private long mTouchTime = -1;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener( v -> {
            if( l == null || !isEnabled() ) return;
            if( mTouchTime != -1 && System.currentTimeMillis() - mTouchTime >= 1000L ) {
                mTouchTime = -1;
            }else {
                if( mTouchTime == -1 ) {
                    mTouchTime = System.currentTimeMillis();
                }else {
                    return;
                }
            }
            l.onClick( v );
        } );
    }

    private int mBackground = R.drawable.ic_touch_btn_true;
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if( enabled ) {
//            setTextColor( Color.WHITE );
            setBackground( getResources().getDrawable( mBackground ) );
        }else {
//            setTextColor( getResources().getColor( R.color.colorCheckBtnTextFalse ) );
            setBackgroundResource( R.drawable.ic_touch_btn_false);
        }
    }

    public void enableSkipStyle() {
        mBackground = R.drawable.ic_touch_skip_btn;
        setTextColor( getResources().getColor( R.color.colorMain ) );
        setBackground( getResources().getDrawable( mBackground ) );
    }
}

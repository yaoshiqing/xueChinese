package com.gjjy.frontlib.widget.damping;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.gjjy.frontlib.R;

/**
 带有阻尼的线性布局
 */
public class DampingLinearLayout extends LinearLayout implements IDamping {
    private final DampingHelper mHelper;

    public DampingLinearLayout(Context context) { this(context, null); }

    public DampingLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new DampingHelper();

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.DampingLinearLayout
        );
        setDampingValue( typedArray.getFloat(
                R.styleable.DampingLinearLayout_damDampingValue, 0.5F
        ));
        setRecoverAnimationDuration( typedArray.getInt(
                R.styleable.DampingLinearLayout_damRecoverAnimationDuration, 300
        ));
        setEnablePullDown( typedArray.getBoolean(
                R.styleable.DampingLinearLayout_damEnablePullDown, true
        ));
        setEnablePullUp( typedArray.getBoolean(
                R.styleable.DampingLinearLayout_damEnablePullUp, true
        ));
        setDampingOrientation( typedArray.getInt(
                R.styleable.DampingLinearLayout_damOrientation, Orientation.VERTICAL
        ));
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHelper.onFinishInflate( this );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout( changed, l, t, r, b );
        mHelper.onLayout( l, t, r, b );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mHelper.dispatchTouchEvent( this, ev );
    }

    @Override
    public boolean superDispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent( ev );
    }

    @Override
    public void setOnDampingScrollListener(OnScrollListener listener) {
        mHelper.setOnDampingScrollListener( listener );
    }

    @Override
    public void setDampingValue(float value) {
        mHelper.setDampingValue( value );
    }

    @Override
    public void setRecoverAnimationDuration(int duration) {
        mHelper.setRecoverAnimationDuration( duration );
    }

    @Override
    public void setRecoverInterpolator(Interpolator i) {
        mHelper.setRecoverInterpolator( i );
    }

    @Override
    public void setEnablePullDown(boolean enable) {
        mHelper.setEnablePullDown( enable );
    }

    @Override
    public void setEnablePullUp(boolean enable) {
        mHelper.setEnablePullUp( enable );
    }

    @Override
    public void setDampingOrientation(int orientation) {
        mHelper.setDampingOrientation( orientation );
    }
}
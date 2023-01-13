package com.gjjy.frontlib.widget.damping;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;

/**
 滑动阻尼帮助类

 @see IDamping 必须实现的接口

 需要实现的核心接口：
 //用于获取子布局（优先获取首个ViewGroup，没有则获取第一个View），如果没有子布局则用传入的ViewGroup。
 {@link DampingHelper#onFinishInflate(ViewGroup)}
 //用于赋值给Rect，定义需要滑动的布局大小
 {@link DampingHelper#onLayout(int, int, int, int)}
 //用于处理滑动等操作
 {@link DampingHelper#dispatchTouchEvent(IDamping, MotionEvent)}

 需要实现的基本接口：
 //父类的分发点击手势事件
 {@link DampingHelper#superDispatchTouchEvent(MotionEvent ev)}
     需要调用：super.dispatchTouchEvent(MotionEvent ev);
 //滑动事件监听器
 {@link DampingHelper#setOnDampingScrollListener(OnScrollListener listener)}
 //阻尼值
 {@link DampingHelper#setDampingValue(float)}
 //启用/禁用下拉
 {@link DampingHelper#setEnablePullDown(boolean)}
 //启用/禁用上拉
 {@link DampingHelper#setEnablePullUp(boolean)}
 //恢复时的动画时长
 {@link DampingHelper#setRecoverAnimationDuration(int)}
 //恢复时的动画插值器
 {@link DampingHelper#setRecoverInterpolator(Interpolator)}

 可选实现接口：
 //滑动方向。水平：{@link Orientation#HORIZONTAL}，垂直：{@link Orientation#VERTICAL}
 {@link DampingHelper#setDampingOrientation(int)} ()}

 */
public class DampingHelper implements IDamping {
    private final Rect mDampingRect = new Rect();

    private View mDampingView;
    private Interpolator mRecoverInterpolator = new DecelerateInterpolator();
    private OnScrollListener mScrollListener;

    private int mRecoverAnimationDuration;
    private float mDampingValue;
    private boolean isEnablePullDown;
    private boolean isEnablePullUp;
    @Orientation
    private int mOrientation = Orientation.VERTICAL;

    private boolean isMoved = false;
    private float mDownXorY = Float.MIN_VALUE;
    private boolean isCompletePull = false;


    /**
     滑动事件监听器
     @param listener        监听器
     */
    @Override
    public void setOnDampingScrollListener(OnScrollListener listener) { mScrollListener = listener; }

    /**
     阻尼值
     @param value           数值越高Pull距离越长
     */
    @Override
    public void setDampingValue(float value) { mDampingValue = value; }

    /**
     Pull结束时，恢复动画时长
     @param duration        时长
     */
    @Override
    public void setRecoverAnimationDuration(int duration) { mRecoverAnimationDuration = duration; }

    /**
     Pull结束时的动画插值器
     @param i               插值器，默认：{@link DecelerateInterpolator}
     */
    public void setRecoverInterpolator(Interpolator i) { mRecoverInterpolator = i; }

    /**
     是否启用下拉
     @param enable          是否启用
     */
    @Override
    public void setEnablePullDown(boolean enable) { isEnablePullDown = enable; }

    /**
     是否启用上拉
     @param enable          是否启用
     */
    @Override
    public void setEnablePullUp(boolean enable) { isEnablePullUp = enable; }

    /**
     滑动方向
     水平滑动：{@link Orientation#HORIZONTAL}
     垂直滑动：{@link Orientation#VERTICAL}
     @param orientation     {@link Orientation}
     */
    @Override
    public void setDampingOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void onFinishInflate(@NonNull ViewGroup vg) {
        int count = vg.getChildCount();
        //没有子布局时，采用传入的ViewGroup
        if( count == 0 ) {
            mDampingView = vg;
            return;
        }
        //查找首个ViewGroup
        for( int i = 0; i < count; i++ ) {
            View v = vg.getChildAt( i );
            if( !( v instanceof ViewGroup ) ) continue;
            mDampingView = v;
        }
        if( mDampingView != null ) return;
        //没有ViewGroup时，采用第一个View
        if( count > 0 ) mDampingView = vg.getChildAt( 0 );
    }

    public void onLayout(int l, int t, int r, int b) {
        mDampingRect.set(
                l, t,
                r, b
        );
    }

    public boolean dispatchTouchEvent(IDamping damping, MotionEvent ev) {
        int pointerCount = ev.getPointerCount();
        //最后一组被点击的坐标下标
        int pointerIndex = ( pointerIndex = pointerCount - 1 ) < 0 ? 0 : pointerIndex;
        float evX = ev.getX( pointerIndex );
        float evY = ev.getY( pointerIndex );

        boolean isVertical = isVertical();

        if( ( isVertical && evY >= mDampingRect.bottom || evY <= mDampingRect.top ) ||
                ( !isVertical && evX >= mDampingRect.right || evX <= mDampingRect.left ) ) {
            recoverLayout();
            return true;
        }

        float xOrY = isVertical() ? evY : evX;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if( mDownXorY == Float.MIN_VALUE ) mDownXorY = xOrY;
            case MotionEvent.ACTION_MOVE:
                int scrollXorYDiff = (int) ( xOrY - mDownXorY );
                boolean pullDown = scrollXorYDiff > 0 && canPullDown();
                boolean pullUp = scrollXorYDiff < 0 && canPullUp();
                if( pullDown || pullUp ) {
                    /* 取消子view已经处理的事件 */
                    ev.setAction( MotionEvent.ACTION_CANCEL );
                    damping.superDispatchTouchEvent( ev );
                    //Pull系数
                    int offset = (int) ( scrollXorYDiff * mDampingValue );
                    mDampingView.layout(
                            mDampingRect.left + ( isVertical ? 0 : offset ),
                            mDampingRect.top + ( isVertical ? offset : 0 ),
                            mDampingRect.right + ( isVertical ? 0 : offset ),
                            mDampingRect.bottom + ( isVertical ? offset : 0 )
                    );
                    isMoved = true;
                    isCompletePull = false;
                    if ( mScrollListener != null ) mScrollListener.onScroll( evX, evY );
                    return true;
                }else {
                    isMoved = false;
                    isCompletePull = true;
                    return damping.superDispatchTouchEvent( ev );
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDownXorY = Float.MIN_VALUE;
                recoverLayout();
                return !isCompletePull || damping.superDispatchTouchEvent( ev );
            default:
                return true;
        }
    }

    @Override
    public boolean superDispatchTouchEvent(MotionEvent ev) { return false; }

    /**
     * 恢复布局
     */
    private void recoverLayout() {
        if ( !isMoved ) return;
        boolean isVertical = isVertical();
        TranslateAnimation anim = new TranslateAnimation(
                isVertical ? 0 : mDampingView.getLeft() - mDampingRect.left,
                0,
                isVertical ? mDampingView.getTop() - mDampingRect.top : 0,
                0
        );
        anim.setDuration( mRecoverAnimationDuration );
        anim.setInterpolator( mRecoverInterpolator );
        mDampingView.startAnimation( anim );
        mDampingView.layout(
                mDampingRect.left, mDampingRect.top,
                mDampingRect.right, mDampingRect.bottom
        );
        isMoved = false;
    }

    /**
     * 判断是否可以下拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullDown() {
        if( !isEnablePullDown ) return false;
        if( isVertical() ) return !mDampingView.canScrollVertically( -1 );
        return !mDampingView.canScrollHorizontally( -1 );
    }

    /**
     * 判断是否可以上拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullUp() {
        if( !isEnablePullUp ) return false;
        if( isVertical() ) return !mDampingView.canScrollVertically( 1 );
        return !mDampingView.canScrollHorizontally( 1 );
    }

    private boolean isVertical() { return mOrientation == Orientation.VERTICAL; }
}

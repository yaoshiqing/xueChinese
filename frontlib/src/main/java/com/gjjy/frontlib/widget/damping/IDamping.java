package com.gjjy.frontlib.widget.damping;

import android.view.MotionEvent;
import android.view.animation.Interpolator;

public interface IDamping {
    /**
     滑动事件监听器
     @param listener        监听器
     */
    void setOnDampingScrollListener(OnScrollListener listener);

    /**
     阻尼值
     @param value           数值越高Pull距离越长
     */
    void setDampingValue(float value);

    /**
     Pull结束时，恢复动画时长
     @param duration        时长
     */
    void setRecoverAnimationDuration(int duration);

    /**
     Pull结束时的动画插值器
     @param i               插值器，默认：{@link android.view.animation.DecelerateInterpolator}
     */
    void setRecoverInterpolator(Interpolator i);

    /**
     是否启用下拉
     @param enable          是否启用
     */
    void setEnablePullDown(boolean enable);

    /**
     是否启用上拉
     @param enable          是否启用
     */
    void setEnablePullUp(boolean enable);

    /**
     设置滑动方向
     @param orientation     滑动方向
     */
    void setDampingOrientation(@Orientation int orientation);

    boolean superDispatchTouchEvent(MotionEvent ev);
}

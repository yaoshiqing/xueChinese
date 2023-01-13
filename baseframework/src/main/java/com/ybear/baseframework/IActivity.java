package com.ybear.baseframework;

import android.view.View;
import android.widget.Space;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringRes;

import com.ybear.ybutils.utils.StackManage;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;

public interface IActivity {
    /**
     * 重新更新Window状态
     */
    void onReChangedWindowState();

    /**
     * 获取Toast管理器
     * @return {@link ToastManage}
     */
    ToastManage getToast();

    /**
     * 获取栈管理器（用于关闭页面，返回桌面，双击返回桌面等一系列操作）
     * @return  {@link StackManage}
     */
    StackManage getStackManage();

    /**
     * 设置页面透明度
     * @param alpha     0.0 ~ 1.0
     */
    boolean setAlpha(float alpha);

    /**
     * 设置半透明
     * @param enable    是否半透明
     */
    boolean setTransparent(boolean enable);

    /**
     * 设置全透明
     * @param enable    是否全透明
     */
    boolean setFullyTransparent(boolean enable);

    /**
     * 显示/隐藏活动栏
     * @param enable    是否启用
     */
    void setShowActionBar(boolean enable);

    /**
     * 设置状态栏图标颜色
     * @param statusBarColor    颜色
     */
    void setStatusBarIconColor(@SysUtil.StatusBarIconColor int statusBarColor);

    /**
     * 状态栏图标颜色
     * @return  颜色
     */
    int onStatusBarIconColor();

    /**
     * 沉浸式模式下view可能会被状态栏遮挡
     * @param id     {@link Space} 或者 {@link View}的资源id
     */
    void setStatusBarHeight(@IdRes int id);

    /**
     沉浸式模式下，view可能会被状态栏遮挡，这里设置View的padding top下移view的位置
     @param v       {@link View}
     */
    void setStatusBarHeightForPadding(View v);

    /**
     取消沉浸式模式下，View的padding top下移view的位置
     @param v       {@link View}
     */
    void setCancelStatusBarHeightForPadding(View v);

    /**
     沉浸式模式下，view可能会被状态栏遮挡，这里通过设置Space高度下移view的位置
     @param s       {@link Space}
     */
    void setStatusBarHeightForSpace(Space s);

    /**
     取消沉浸式模式下，Space高度下移view的位置
     @param s       {@link Space}
     */
    void setCancelStatusBarHeightForSpace(Space s);

    /**
     * 是否启用全屏
     * @param enable    是否启用
     */
    void setEnableFullScreen(boolean enable);

    /**
     * 启用全屏
     * 当{@link #onEnableImmersive()}为true时无效
     * @return  是否启用
     */
    boolean onEnableFullScreen();

    /**
     * 是否启用沉浸式
     * @param enable    是否启用
     */
    void setEnableImmersive(boolean enable);

    /**
     * 启用沉浸式
     * @return  是否启用
     */
    boolean onEnableImmersive();

    /**
     进入和退出动画：退出动画
     @param defResId    默认的动画。
                        进入：0：进入动画，1：退出动画
                        退出：2：进入动画，3：退出动画
     @return            默认返回 defResId
     */
    @NonNull
    @Size(min = 4, max = 4)
    int[] onOverridePendingTransition(@NonNull @Size(min = 4, max = 4) int[] defResId);

    /**
     启用进入和退出动画
     @return        是否启用
     */
    boolean onEnableOverridePendingTransition();

    void showToast(String s, int duration, @Nullable Build build);
    void showToast(String s, int duration);
    void showToast(String s, @Nullable Build build);
    void showToast(String s);
    void showToastOfLong(String s, @Nullable Build build);
    void showToastOfLong(String s);

    void showToast(@StringRes int id, int duration, @Nullable Build build);
    void showToast(@StringRes int id, int duration);
    void showToast(@StringRes int id, @Nullable Build build);
    void showToast(@StringRes int id);
    void showToastOfLong(@StringRes int id, @Nullable Build build);
    void showToastOfLong(@StringRes int id);
}

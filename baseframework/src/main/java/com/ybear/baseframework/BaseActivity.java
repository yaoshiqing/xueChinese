package com.ybear.baseframework;

import android.os.Bundle;
import android.view.View;
import android.widget.Space;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringRes;

import com.ybear.mvp.view.MvpActivity;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.StackManage;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;

public class BaseActivity extends MvpActivity implements IActivity, DOM.OnResultListener {
    private final ActivityImpl mImpl = ActivityImpl.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImpl.onCreate( this, hashCode(), this );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImpl.onResume( this, this );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImpl.onPause();
    }

    @Override
    public void onReChangedWindowState() {
        mImpl.onReChangedWindowState( this );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImpl.onDestroy( this, hashCode() );

    }

    @Override
    public void finish() {
        super.finish();
        mImpl.finish( this );
    }

    @Override
    public void onResult(int id, Object data) {  }

    public void setCallResult(int id) { mImpl.setCallResult( id ); }

    public void setCallResult(int id, Object data) { mImpl.setCallResult( id, data ); }

    /**
     * 设置页面透明度
     * @param alpha     0.0 ~ 1.0
     */
    @Override
    public boolean setAlpha(float alpha) { return mImpl.setAlpha( this, alpha ); }

    /**
     * 设置半透明
     * @param enable    是否半透明
     */
    @Override
    public boolean setTransparent(boolean enable) {
        return mImpl.setTransparent( getActivity(), enable );
    }

    /**
     * 设置全透明
     * @param enable    是否全透明
     */
    @Override
    public boolean setFullyTransparent(boolean enable) {
        return mImpl.setFullyTransparent( getActivity(), enable );
    }

    /**
     * 显示/隐藏活动栏
     * @param enable    是否启用
     */
    @Override
    public void setShowActionBar(boolean enable) { mImpl.setShowActionBar( this, enable ); }

    /**
     * 沉浸式模式下view可能会被状态栏遮挡
     * @param id     {@link Space} 或者 {@link View}的资源id
     */
    @Override
    public void setStatusBarHeight(@IdRes int id) { mImpl.setStatusBarHeight( this, id ); }

    /**
     沉浸式模式下，view可能会被状态栏遮挡，这里设置View的padding top下移view的位置
     @param v       {@link View}
     */
    @Override
    public void setStatusBarHeightForPadding(View v) { mImpl.setStatusBarHeightForPadding( v ); }

    /**
     取消沉浸式模式下，View的padding top下移view的位置
     @param v       {@link View}
     */
    @Override
    public void setCancelStatusBarHeightForPadding(View v) {
        mImpl.setCancelStatusBarHeightForPadding( v );
    }

    /**
     沉浸式模式下，view可能会被状态栏遮挡，这里通过设置Space高度下移view的位置
     @param s       {@link Space}
     */
    @Override
    public void setStatusBarHeightForSpace(Space s) { mImpl.setStatusBarHeightForSpace( s ); }

    /**
     取消沉浸式模式下，Space高度下移view的位置
     @param s       {@link Space}
     */
    @Override
    public void setCancelStatusBarHeightForSpace(Space s) {
        mImpl.setCancelStatusBarHeightForSpace( s );
    }

    /**
     * 设置状态栏图标颜色
     * @param statusBarColor    颜色
     */
    @Override
    public void setStatusBarIconColor(@SysUtil.StatusBarIconColor int statusBarColor) {
        mImpl.setStatusBarIconColor( this, statusBarColor );
    }

    /**
     * 状态栏图标颜色
     * @return  颜色
     */
    @SysUtil.StatusBarIconColor
    @Override
    public int onStatusBarIconColor() { return mImpl.onStatusBarIconColor(); }

    /**
     * 是否启用全屏
     * @param enable    是否启用
     */
    @Override
    public void setEnableFullScreen(boolean enable) {
        mImpl.setEnableFullScreen( this, enable );
    }

    /**
     * 启用全屏
     * 当{@link #onEnableImmersive()}为true时无效
     * @return  是否启用
     */
    @Override
    public boolean onEnableFullScreen() { return mImpl.onEnableFullScreen(); }

    /**
     * 是否启用沉浸式
     * @param enable    是否启用
     */
    @Override
    public void setEnableImmersive(boolean enable) {
        mImpl.setEnableImmersive( this, enable );
    }

    /**
     * 启用沉浸式
     * @return  是否启用
     */
    @Override
    public boolean onEnableImmersive() { return mImpl.onEnableImmersive(); }

    /**
     进入和退出动画：退出动画
     @param defResId    默认的动画。
     进入：0：进入动画，1：退出动画
     退出：2：进入动画，3：退出动画
     @return            默认返回 defResId
     */
    @NonNull
    @Size(min = 4, max = 4)
    @Override
    public int[] onOverridePendingTransition(@NonNull @Size(min = 4, max = 4) int[] defResId) {
        return mImpl.onOverridePendingTransition( defResId );
    }

    /**
     启用进入和退出动画
     @return        是否启用
     */
    @Override
    public boolean onEnableOverridePendingTransition() {
        return mImpl.onEnableOverridePendingTransition();
    }

    /**
     * 获取栈管理器（用于关闭页面，返回桌面，双击返回桌面等一系列操作）
     * @return  {@link StackManage}
     */
    @Override
    public StackManage getStackManage() { return mImpl.getStackManage(); }

    @Override
    public ToastManage getToast() { return mImpl.getToast(); }

    @Override
    public void showToast(String s, int duration, @Nullable Build build) {
        mImpl.showToast( s, duration, build );
    }

    @Override
    public void showToast(String s, int duration) { mImpl.showToast( s, duration ); }

    @Override
    public void showToast(String s, @Nullable Build build) { mImpl.showToast( s, build ); }

    @Override
    public void showToast(String s) { mImpl.showToast( s ); }

    @Override
    public void showToastOfLong(String s, @Nullable Build build) {
        mImpl.showToastOfLong( s, build );
    }

    @Override
    public void showToastOfLong(String s) { mImpl.showToastOfLong( s ); }

    @Override
    public void showToast(int id, int duration, @Nullable Build build) {
        mImpl.showToast( getResources(), id, duration, build );
    }

    @Override
    public void showToast(@StringRes int id, int duration) {
        mImpl.showToast( getResources(), id, duration );
    }

    @Override
    public void showToast(int id, @Nullable Build build) {
        mImpl.showToast( getResources(), id, build );
    }

    @Override
    public void showToast(@StringRes int id) {
        mImpl.showToast( getResources(), id );
    }

    @Override
    public void showToastOfLong(int id, @Nullable Build build) {
        mImpl.showToastOfLong( getResources(), id, build );
    }

    @Override
    public void showToastOfLong(@StringRes int id) {
        mImpl.showToastOfLong( getResources(), id );
    }
}

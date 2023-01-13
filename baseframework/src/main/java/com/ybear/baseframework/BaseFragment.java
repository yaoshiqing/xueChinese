package com.ybear.baseframework;

import android.os.Bundle;
import android.view.View;
import android.widget.Space;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.ybear.mvp.view.fragment.MvpFragment;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.toast.ToastManage;

public class BaseFragment extends MvpFragment implements DOM.OnResultListener {
    private final ActivityImpl mImpl = ActivityImpl.create();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImpl.onCreate( getActivity(), hashCode(), this );
    }

    @Override
    public void onResume() {
        super.onResume();
        mImpl.onResume( this, null );
    }

    @Override
    public void onPause() {
        super.onPause();
        mImpl.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImpl.onDestroy( getActivity(), hashCode() );
    }

    @Override
    public void finish() {
        super.finish();
        mImpl.finish( getActivity() );
    }

    @Override
    public void onResult(int id, Object data) { }

    public void setCallResult(int id) { mImpl.setCallResult( id ); }

    public void setCallResult(int id, Object data) { mImpl.setCallResult( id, data ); }

    /**
     * 设置页面透明度
     * @param alpha     0.0 ~ 1.0
     */
    public boolean setAlpha(float alpha) { return mImpl.setAlpha( getActivity(), alpha ); }

    /**
     * 设置半透明
     * @param enable    是否半透明
     */
    public boolean setTransparent(boolean enable) {
        return mImpl.setTransparent( getActivity(), enable );
    }

    /**
     * 设置全透明
     * @param enable    是否全透明
     */
    public boolean setFullyTransparent(boolean enable) {
        return mImpl.setFullyTransparent( getActivity(), enable );
    }

    /**
     * 沉浸式模式下view可能会被状态栏遮挡
     * @param id     {@link Space} 或者 {@link View}的资源id
     */
    public void setStatusBarHeight(@IdRes int id) {
        mImpl.setStatusBarHeight( getActivity(), id );
    }

    /**
     * 沉浸式模式下view可能会被状态栏遮挡，这里通过padding top下移view的位置
     */
    public void setStatusBarHeightForPadding(View v) { mImpl.setStatusBarHeightForPadding( v ); }

    /**
     * 沉浸式模式下view可能会被状态栏遮挡，这里通过设置Space高度下移view的位置
     * @param s     {@link Space}
     */
    public void setStatusBarHeightForSpace(Space s) { mImpl.setStatusBarHeightForSpace( s ); }

    public void setStatusBarIconColor(@SysUtil.StatusBarIconColor int statusBarColor) {
        mImpl.setStatusBarIconColor( getActivity(), statusBarColor );
    }

    public ToastManage getToast() {
        return mImpl.getToast();
    }

    public void showToast(String s, int duration) {
        mImpl.showToast( s, duration );
    }

    public void showToast(String s) {
        mImpl.showToast( s );
    }

    public void showToastOfLong(String s) {
        mImpl.showToastOfLong( s );
    }

    public void showToast(@StringRes int id, int duration) {
        mImpl.showToast( getResources(), id, duration );
    }

    public void showToast(@StringRes int id) {
        mImpl.showToast( getResources(), id );
    }

    public void showToastOfLong(@StringRes int id) {
        mImpl.showToastOfLong( getResources(), id );
    }
}
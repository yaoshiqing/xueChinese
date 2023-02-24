package com.gjjy.discoverylib.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

import com.gjjy.basiclib.Config;
import com.gjjy.discoverylib.mvp.presenter.DiscoveryPresenter;
import com.gjjy.discoverylib.mvp.view.DiscoveryView;
import com.ybear.mvp.annotations.Presenter;
import com.gjjy.discoverylib.R;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.Toolbar;
import com.ybear.ybutils.utils.LogUtil;

/**
 * Explore 页面
 */
public class DiscoveryFragment extends BaseFragment implements DiscoveryView {
    @Presenter
    private DiscoveryPresenter mPresenter;
    private ListenDailyFragment fListenDailyFragment;
    private PopularVideosFragment fPopularVideosFragment;
    private TargetedLearningFragment fTargetedLearningFragment;

    private Toolbar tbToolbar;
    private NestedScrollView nsvScrollList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StartUtil.REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            mPresenter.startMyCourseActivity();
        }
    }

    private void initView() {
        setStatusBarHeightForSpace(findViewById(R.id.toolbar_height_space));

        tbToolbar = findViewById(R.id.find_tb_toolbar);
        nsvScrollList = findViewById(R.id.find_nsv_scroll_list);
//        fListenDailyFragment = (ListenDailyFragment) findFragmentById( R.id.find_f_listen_daily );
//        fTargetedLearningFragment = (TargetedLearningFragment) findFragmentById( R.id.find_f_TargetedLearning );
//        fPopularVideosFragment = (PopularVideosFragment) findFragmentById( R.id.find_f_popular_videos );

        if (getActivity() == null) {
            return;
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.find_ll_fragment_layout, fListenDailyFragment = new ListenDailyFragment());
        ft.add(R.id.find_ll_fragment_layout, fTargetedLearningFragment = new TargetedLearningFragment());
        ft.add(R.id.find_ll_fragment_layout, fPopularVideosFragment = new PopularVideosFragment());

        ft.show(fPopularVideosFragment);
        ft.show(fListenDailyFragment);
        ft.show(fTargetedLearningFragment);
        ft.commitAllowingStateLoss();
    }

    private void initData() {
        tbToolbar.showBackBtnOfImg(false);
        tbToolbar.showBackBtnOfText(false);

        tbToolbar.setOtherBtnOfText(R.string.stringMyCoursesMainTitle);
        tbToolbar.setOtherBtnColor(R.color.colorMain);
        tbToolbar.setOtherBtnTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tbToolbar.showOtherBtnOfText(true);
    }

    private void initListener() {
        tbToolbar.setOnClickOtherBtnListener(view -> mPresenter.startMyCourseActivity());
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        switch (id) {
            case DOMConstant.NOTIFY_DISCOVERY_LIST:
                LogUtil.e("Discovery -> " + nsvScrollList.canScrollVertically(0) + " | " + data);
                if (data == null && !nsvScrollList.canScrollVertically(0)) {
                    nsvScrollList.smoothScrollTo(0, 0);
                } else {
                    if (data != null) {
                        break;
                    }
                    //刷新
                    setCallResult(id, 1);
                    if (getActivity() != null && fListenDailyFragment != null && fTargetedLearningFragment != null) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.show(fListenDailyFragment);
                        ft.show(fPopularVideosFragment);
                        ft.show(fTargetedLearningFragment);
                        ft.commitAllowingStateLoss();
                    }
                }
                break;
            case DOMConstant.NETWORK_AVAILABLE_STATUS:  //监听网络状态
//                post(() -> doNetworkStatus( (Boolean) data ) );
                if ((Boolean) data) {
                    if (fListenDailyFragment != null) {
                        fListenDailyFragment.notifyUpdatedData();
                    }
                    if (fPopularVideosFragment != null) {
                        fPopularVideosFragment.notifyUpdatedData();
                    }
                    if (fTargetedLearningFragment != null) {
                        fTargetedLearningFragment.notifyUpdatedData();
                    }
                }
                break;
        }
    }
}
package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.VideoPresenter;
import com.gjjy.frontlib.mvp.view.VideoView;
import com.gjjy.frontlib.widget.AnswerOptionsLayout;
import com.gjjy.frontlib.widget.AnswerVideoView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

/**
 * 视频题
 */
public class VideoFragment extends BaseFragment implements VideoView, OnVisibleChangedListener {
    @Presenter
    private VideoPresenter mPresenter;

    private AnswerVideoView avvVideo;
    private AnswerOptionsLayout aolOpt;
    private CheckButton cbCheckBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_video, container, false );
    }

    @Override
    public void onPause() {
        super.onPause();
        if( avvVideo != null ) avvVideo.pause();
    }

    @Override
    public void onDestroy() {
        if( avvVideo != null ) avvVideo.release();
        if( aolOpt != null ) aolOpt.release();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        if( mPresenter.isAllowInitLayout() ) {
            if( aolOpt != null ) aolOpt.cancelSelect();
            if( cbCheckBtn != null ) cbCheckBtn.setEnabled( false );
        }
        mPresenter.onPagerHiddenStatus( false );
        post( () -> {
            //视频链接
            mPresenter.setVideoUrl();
            onPlay();
        }, 500);

        BaseActivity activity = (BaseActivity) context;
        activity.setOnNetworkErrorRefreshClickListener( v -> {} );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
//        avvVideo.pause();
        if( aolOpt != null ) aolOpt.release();
    }

    private void initView() {
        avvVideo = findViewById( R.id.video_avv_video );
        aolOpt = findViewById( R.id.video_aol_opt );
        cbCheckBtn = findViewById( R.id.video_cb_check_btn );
    }

    private void initData() {
        avvVideo.setEnableOrientation( false );
        aolOpt.cancelKeepNotSquareHeight();
        aolOpt.setAnswerType( mPresenter.getAnswerType() );
        aolOpt.setEnableChangedTextLayout( false );
    }

    private void initListener() {
        aolOpt.setOnOptClickListener((vg, v, data, position) -> {
            mPresenter.refreshSnail();
            mPresenter.setSelectPosition( position );
            avvVideo.pause();
            if( mPresenter.isShowCheck() ) {
                cbCheckBtn.setEnabled( true );
            }else {
                check();
            }
        });

        cbCheckBtn.setOnClickListener(v -> {
            check();
        });
    }

    private void check() {
        if( avvVideo != null ) avvVideo.pause();
        if( aolOpt != null ) aolOpt.stop();
        mPresenter.check();
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }

//    private boolean isExistVideoUrl = false;
    @Override
    public void onCallVideoUrl(String url) {
//        if( isExistVideoUrl ) return;
        LogUtil.e("onCallVideoUrl -> " + url);
        avvVideo.setDataSource( url );
//        isExistVideoUrl = true;
    }

    @Override
    public void onCallOptionsLayout(int layout, OptionsEntity... opts) {
        if( aolOpt != null ) aolOpt.setData( layout, opts );
    }

    @Override
    public void onCallOptionsCorrect() { if( aolOpt != null ) aolOpt.switchCorrect(); }

    @Override
    public void onCallOptionsError() { if( aolOpt != null ) aolOpt.switchError(); }

    @Override
    public void onCallSpeed(float speed) {
        if( aolOpt != null ) aolOpt.setSpeed( speed );
    }

    @Override
    public void onCheckVisibility(int visibility) { cbCheckBtn.setVisibility( visibility ); }

    @Override
    public void onPlay() { if( avvVideo != null ) avvVideo.play(); }
}

package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.mvp.presenter.HearingPresenter;
import com.gjjy.frontlib.mvp.view.HearingView;
import com.gjjy.frontlib.widget.AnswerOptionsLayout;
import com.gjjy.frontlib.widget.AudioPlayButton;
import com.gjjy.frontlib.widget.CheckButton;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;

/**
 * 听力题
 */
public class HearingFragment extends BaseFragment
        implements HearingView, OnVisibleChangedListener {
    @Presenter
    private HearingPresenter mPresenter;

//    private TextView tvTitle;
    private AnswerOptionsLayout aolOpt;
    private AudioPlayButton apbAudioBtn;
    private CheckButton cbCheckBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_hearing, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    @Override
    public void onDestroy() {
        if( apbAudioBtn != null ) apbAudioBtn.release();
        if( aolOpt != null ) aolOpt.release();
        super.onDestroy();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        if( mPresenter.isAllowInitLayout() ) {
            if( aolOpt != null ) aolOpt.cancelSelect();
            if( cbCheckBtn != null ) cbCheckBtn.setEnabled( false );
        }
        post(() -> {
            if( apbAudioBtn != null ) apbAudioBtn.initMediaX();
        });
        mPresenter.onPagerHiddenStatus( false );

        BaseActivity activity = (BaseActivity) context;
        activity.setOnNetworkErrorRefreshClickListener( v -> {} );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
        pause();
        if( aolOpt != null ) aolOpt.release();
        post(() -> {
            if( apbAudioBtn != null ) apbAudioBtn.release();
        });
    }

    private void pause() {
        try { apbAudioBtn.pauseNow(); } catch(Exception e) { e.printStackTrace(); }
        try { aolOpt.pause(); } catch(Exception e) { e.printStackTrace(); }
    }

    private void initView() {
//        tvTitle = findViewById( R.id.hearing_tv_title );
        aolOpt = findViewById( R.id.hearing_aol_opt );
        apbAudioBtn = findViewById( R.id.hearing_apb_audio_btn );
        cbCheckBtn = findViewById( R.id.hearing_cb_check_btn );
    }

    private void initData() {
//        apbAudioBtn.switchRoundRect();
//        apbAudioBtn.setSpeechSynthesizer( SpeechSynthesizer.get() );
        aolOpt.cancelKeepNotSquareHeight();
        aolOpt.setAnswerType( mPresenter.getAnswerType() );
    }

    private void initListener() {
        aolOpt.setOnOptClickListener((vg, v, data, position) -> {
            mPresenter.refreshSnail();
            mPresenter.setSelectPosition( position );
            if( mPresenter.isShowCheck() ) {
                cbCheckBtn.setEnabled( true );
                apbAudioBtn.stopNow();
            }else {
                check();
            }
        });

        apbAudioBtn.setOnAudioPlayClickListener(v -> mPresenter.refreshSnail());

        cbCheckBtn.setOnClickListener(v -> check());
    }

    private void check() {
        if( apbAudioBtn != null ) apbAudioBtn.stopNow();
        if( aolOpt != null ) aolOpt.stop();
        mPresenter.check();
    }

    @Override
    public void onCallTitle(String title) {
//        tvTitle.setText( title );
    }

    @Override
    public void onCallAudio(String s) {
        apbAudioBtn.setAudio( s );
        apbAudioBtn.play();
    }

    @Override
    public void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts) {
        if( aolOpt != null ) aolOpt.setData( layout, opts );
    }

    @Override
    public void onCallOptionsCorrect() { if( aolOpt != null ) aolOpt.switchCorrect(); }

    @Override
    public void onCallOptionsError() { if( aolOpt != null ) aolOpt.switchError(); }

    @Override
    public void onCheckVisibility(int visibility) { cbCheckBtn.setVisibility( visibility ); }

    @Override
    public void onCallSpeed(float speed) {
        if( apbAudioBtn != null ) apbAudioBtn.setSpeed( speed );
        if( aolOpt != null ) aolOpt.setSpeed( speed );
    }

    @Override
    public void onPlay() {
        if( apbAudioBtn != null ) apbAudioBtn.performClick();
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }
}

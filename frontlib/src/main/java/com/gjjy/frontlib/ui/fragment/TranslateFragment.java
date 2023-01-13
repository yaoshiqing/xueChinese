package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.TranslatePresenter;
import com.gjjy.frontlib.mvp.view.TranslateView;
import com.gjjy.frontlib.widget.AnswerOptionsLayout;
import com.gjjy.frontlib.widget.AudioPlayButton;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

/**
 * 翻译题
 */
public class TranslateFragment extends BaseFragment
        implements TranslateView, OnVisibleChangedListener {
    @Presenter
    private TranslatePresenter mPresenter;

    private TextView tvTitle;
    private TextView tvQuestion;


    private AudioPlayButton apbAudioBtn;
    private AnswerOptionsLayout aolOpt;
    private CheckButton cbCheckBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_translate, container, false );
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
        tvTitle = findViewById( R.id.translate_tv_title );
        tvQuestion = findViewById( R.id.translate_tv_question );
        apbAudioBtn = findViewById( R.id.translate_apb_audio_btn );
        aolOpt = findViewById( R.id.translate_aol_opt );
        cbCheckBtn = findViewById( R.id.translate_cb_check_btn );
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
            apbAudioBtn.pauseNow();
            if( mPresenter.isShowCheck() ) {
                cbCheckBtn.setEnabled( true );
            }else {
                check();
            }
        });

        apbAudioBtn.setOnAudioPlayClickListener(v -> {
            mPresenter.refreshSnail();
            if( aolOpt != null ) aolOpt.pause();
        });

        cbCheckBtn.setOnClickListener(v -> check() );
    }

    private void check() {
        if( apbAudioBtn != null ) apbAudioBtn.stopNow();
        if( aolOpt != null ) aolOpt.stop();
        mPresenter.check();
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }

    @Override
    public void onCallTitle(String title) { tvTitle.setText( title ); }

    @Override
    public void onCallQuestion(String question) { tvQuestion.setText( question ); }

    @Override
    public void onCallAudioUrl(String url) {
        if( TextUtils.isEmpty( url ) || !url.startsWith("http") ) {
            apbAudioBtn.setVisibility( View.GONE );
            return;
        }
        apbAudioBtn.setAudio( url );
        apbAudioBtn.play();
        apbAudioBtn.setVisibility( View.VISIBLE );
    }

    @Override
    public void onCallOptionsLayout(int layout, OptionsEntity... opts) {
        if( aolOpt != null ) aolOpt.setData( layout, opts );
    }

    @Override
    public void onCallOptionsCorrect() {
        if( aolOpt != null ) aolOpt.switchCorrect();
        if( apbAudioBtn != null ) apbAudioBtn.stopNow();
    }

    @Override
    public void onCallOptionsError() {
        if( aolOpt != null ) aolOpt.switchError();
        if( apbAudioBtn != null ) apbAudioBtn.stopNow();
    }

    @Override
    public void onCallSpeed(float speed) {
        if( apbAudioBtn != null ) apbAudioBtn.setSpeed( speed );
        if( aolOpt != null ) aolOpt.setSpeed( speed );
    }

    @Override
    public void onCheckVisibility(int visibility) { cbCheckBtn.setVisibility( visibility ); }

    @Override
    public void onPlay() {
        if( apbAudioBtn != null ) apbAudioBtn.performClick();
    }
}

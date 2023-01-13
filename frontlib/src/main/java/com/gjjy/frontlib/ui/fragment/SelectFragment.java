package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.widget.AnswerOptionsLayout;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.mvp.presenter.SelectPresenter;
import com.gjjy.frontlib.mvp.view.SelectView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

/**
 * 选择题
 */
public class SelectFragment extends BaseFragment
        implements SelectView, OnVisibleChangedListener {
    @Presenter
    private SelectPresenter mPresenter;

    private TextView tvTitle;
    private AnswerOptionsLayout aolOpt;
    private CheckButton cbCheckBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_select, container, false );
    }

    @Override
    public void onPause() {
        super.onPause();
        aolOpt.pause();
    }

    @Override
    public void onDestroy() {
        aolOpt.release();
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
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
        try { aolOpt.pause(); } catch(Exception e) { e.printStackTrace(); }
        if( aolOpt != null ) aolOpt.release();
    }

    private void initView() {
        tvTitle = findViewById( R.id.select_tv_title );
        aolOpt = findViewById( R.id.select_aol_opt );
        cbCheckBtn = findViewById( R.id.select_cb_check_btn );
    }

    private void initData() {
        aolOpt.cancelKeepNotSquareHeight();
        aolOpt.setAnswerType( mPresenter.getAnswerType() );
    }

    private void initListener() {
        aolOpt.setOnOptClickListener((vg, v, data, position) -> {
            mPresenter.refreshSnail();
            mPresenter.setSelectPosition( position );
            if( mPresenter.isShowCheck() ) {
                cbCheckBtn.setEnabled( true );
            }else {
                check();
            }
        });

        cbCheckBtn.setOnClickListener(v -> check());
    }

    private void check() {
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
}

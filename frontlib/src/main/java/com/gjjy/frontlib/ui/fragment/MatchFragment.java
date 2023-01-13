package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.mvp.presenter.MatchPresenter;
import com.gjjy.frontlib.mvp.view.MatchView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.frontlib.widget.MatchOptionsView;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

/**
 * 匹配题
 */
public class MatchFragment extends BaseFragment
        implements MatchView, OnVisibleChangedListener  {
    @Presenter
    private MatchPresenter mPresenter;

    private TextView tvTitle;
    private MatchOptionsView movMatch;
    private CheckButton cbCheckBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_match, container, false );
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
        movMatch.pause();
    }

    @Override
    public void onDestroy() {
        movMatch.release();
        super.onDestroy();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( false );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
        try { movMatch.pause(); } catch(Exception e) { e.printStackTrace(); }
        movMatch.release();
    }

    private void initView() {
        tvTitle = findViewById( R.id.match_tv_title );
        movMatch = findViewById( R.id.match_mov_match_view );
        cbCheckBtn = findViewById( R.id.match_cb_check_btn );
    }

    private void initData() {
        cbCheckBtn.setText( R.string.stringContinue );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener(v -> check());

        movMatch.setOnMatchClickListener(v -> mPresenter.refreshSnail());

        movMatch.setOnMatchChangedListener((data, correctCount, count, isCorrect) ->
                mPresenter.setCheckResult( correctCount == count ));
    }

    private void check() {
        movMatch.pause();
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
        movMatch.setData( opts );
    }

    @Override
    public void onEnableCheckBtn() { cbCheckBtn.setEnabled( true ); }

    @Override
    public void onCallSpeed(float speed) {
        movMatch.setSpeed( speed );
    }

    @Override
    public void onCheckVisibility(int visibility) { cbCheckBtn.setVisibility( visibility ); }
}

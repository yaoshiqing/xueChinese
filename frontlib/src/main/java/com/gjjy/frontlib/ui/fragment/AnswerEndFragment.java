package com.gjjy.frontlib.ui.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.AnswerEndPresenter;
import com.gjjy.frontlib.mvp.view.AnswerEndView;
import com.gjjy.frontlib.widget.CheckButton;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybutils.utils.FrameAnimation;

/**
 * 答题结束页
 */
public class AnswerEndFragment extends BaseFragment
        implements AnswerEndView, OnVisibleChangedListener {
    @Presenter
    private AnswerEndPresenter mPresenter;
    private CheckButton cbCheckBtn;
    private ImageView ivLightningIcon;
    private ImageView ivLightningEffect;
    private ImageView ivIcon;
    private TextView tvIconTitle;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvTestGoBackBtn;
    private FrameAnimation.FrameControl mIconFrameCtrl;
    private FrameAnimation.FrameControl mLightningEffectFrameCtrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_answer_end, container, false );
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
        super.onActivityResult( requestCode, resultCode, data );
    }

    private int isVisibleChanged = View.INVISIBLE;
    @Override
    public void onResume() {
        super.onResume();

        if( isVisibleChanged == View.VISIBLE ) doVisibleChanged();
    }

    @Override
    public void onDestroy() {
        if( mIconFrameCtrl != null ) mIconFrameCtrl.stopNow();
        if( mLightningEffectFrameCtrl != null ) mLightningEffectFrameCtrl.stopNow();
        super.onDestroy();
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult( id, data );
        if( id == DOMConstant.ANSWER_EXP_NEXT && isVisibleChanged != View.GONE ) {
            isVisibleChanged = View.VISIBLE;
            if( data != null && !(boolean)data ) post( this::doVisibleChanged );
        }
    }

    private void doVisibleChanged() {
        isVisibleChanged = View.INVISIBLE;
        mPresenter.onVisibleChanged( true );

        BaseActivity activity = (BaseActivity) getContext();
        if( activity != null ) activity.setOnNetworkErrorRefreshClickListener( v -> {} );
//        if( isVisibleChanged == View.VISIBLE ) doVisibleChanged();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        mPresenter.onVisibleChanged( true );
//
//        BaseActivity activity = (BaseActivity) context;
//        activity.setOnNetworkErrorRefreshClickListener( v -> {} );
////        if( isVisibleChanged == View.VISIBLE ) doVisibleChanged();
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onVisibleChanged( false );
        isVisibleChanged = View.GONE;
    }

    private void initView() {
        ivIcon = findViewById( R.id.answer_end_iv_icon );
        ivLightningIcon = findViewById( R.id.answer_end_iv_icon_lightning );
        ivLightningEffect = findViewById( R.id.answer_end_lightning_anim_effect );
        tvIconTitle = findViewById( R.id.answer_end_tv_icon_title );
        tvTitle = findViewById( R.id.answer_end_tv_title );
        tvContent = findViewById( R.id.answer_end_tv_content );
        cbCheckBtn = findViewById( R.id.answer_end_cb_check_btn );
        tvTestGoBackBtn = findViewById( R.id.answer_end_tv_test_go_back_btn );
    }

    private void initData() {
        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener(v -> {
            if( mPresenter.getTestOfStatus() != -1 ) {
                //再来一次
                mPresenter.startAnswerActivityOfTest();
            }
            mPresenter.nextItem();
        });

        tvTestGoBackBtn.setOnClickListener( v -> {
            mPresenter.nextItem();
            mPresenter.buriedPointPassTestPageOfLaterButton();
        } );
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        if( args == null ) return;
        mPresenter.doArguments( args );
    }

    public void setScore(int oldScore, int newScore) {
        mPresenter.setOldScore( oldScore );
        mPresenter.setNewScore( newScore );
    }

    @Override
    public void onCallIconType(@Constant.AnswerType int answerType) {
        String resName = null;
        int count = 0;
        boolean result = mPresenter.isAnswerResult();
        switch ( answerType ) {
            case Constant.AnswerType.TEST:
                //1.2.3
                resName = mPresenter.getTestOfIconResName();
                count = 1;
                ivIcon.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
                ivIcon.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            case Constant.AnswerType.SKIP_TEST:
            case Constant.AnswerType.NORMAL:
                resName = result ? "ic_answer_end_icon_" : "ic_answer_end_failure_icon_";
                count = result ? 1 : 1;
                break;
            case Constant.AnswerType.FAST_REVIEW:
                resName = "ic_fast_review_icon_";
                count = 1;
                break;
            case Constant.AnswerType.INTRODUCE:
                resName = "ic_introduce_complete_icon_";
                count = 1;
                break;
        }

        if( resName == null ) {
            mPresenter.nextItem();
            return;
        }
        mIconFrameCtrl = FrameAnimation
                .create()
                .loop( 1 )
                .time( 64 )
                .load( getContext(), resName, 0, count )
                .into( ivIcon );
        mIconFrameCtrl.play( getContext() );

        boolean isLightning = answerType == Constant.AnswerType.NORMAL ||
                answerType == Constant.AnswerType.SKIP_TEST;
        if( isLightning && result ) {
            int dur = 500;
            int effectCount = 5;
            ivLightningEffect.setVisibility( View.VISIBLE );
            mLightningEffectFrameCtrl = FrameAnimation
                    .create()
                    .loop( 1 )
                    .time( dur )
                    .load( getContext(), "answer_end_lightning_", 1, effectCount )
                    .into( ivLightningEffect );
            ivLightningIcon.setVisibility( View.VISIBLE );
            post( () -> {
                float[] scales = new float[ effectCount * 2 ];
                for( int i = 0; i < scales.length; i++ ) {
                    scales[ i ] = i == scales.length - 1 ? 1.0F : i % 2 == 0 ? 1.2F : 0.8F;
                }
                ValueAnimator vaScale = ValueAnimator.ofFloat( scales );
                vaScale.setDuration( effectCount * dur );
                vaScale.addUpdateListener( animation -> {
                    float val = (float) animation.getAnimatedValue();
                    ivLightningIcon.setScaleX( val );
                    ivLightningIcon.setScaleY( val );
                    ivLightningEffect.setScaleX( val );
                    ivLightningEffect.setScaleY( val );
                });
                vaScale.start();

                mLightningEffectFrameCtrl.play( getContext() );
            }, 300);
        }
    }

    @Override
    public void onCallIconTitle(String s) {
        tvIconTitle.setText( s );
        tvIconTitle.setVisibility( View.VISIBLE );
        cbCheckBtn.setText( R.string.stringTestTryAgain );
        tvTestGoBackBtn.setVisibility( View.VISIBLE );
    }

    @Override
    public void onCallTitle(String s) { tvTitle.setText( s ); }

    @Override
    public void onCallContent(String s) { tvContent.setText( s ); }

    @Override
    public void onCallIsShowCheckButton(boolean isShow) {
        cbCheckBtn.setVisibility( isShow ? View.VISIBLE : View.GONE );
    }

    @Override
    public void onStartNeedLoginActivity() {
//        StartUtil.startNeedLoginActivity( this, PageName.COURSE_REMIND );
//        BuriedPointEvent.get().onWhetherLoginOfLogin(
//                getActivity(),
//                PageName.COURSE_LOGIN
//        );
    }
}

package com.gjjy.frontlib.ui.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.AnswerExpPresenter;
import com.gjjy.frontlib.mvp.view.AnswerExpView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.DOMConstant;

/**
 答题经验值页面
 */
@Route(path = "/front/answerExpActivity")
public class AnswerExpActivity extends BaseActivity implements AnswerExpView {
    @Presenter
    private AnswerExpPresenter mPresenter;

    private ImageView ivIcon;
    private TextView tvFastReviewExp;
    private TextView tvTitle;
    private TextView tvContent;
    private CheckButton cbCheckBtn;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_answer_exp );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition( R.anim.anim_right_in, R.anim.anim_left_out );
    }

    private void initData() {
        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );

        mPresenter.loadData();
    }

    private void initView() {
        ivIcon = findViewById( R.id.answer_exp_iv_icon );
        tvFastReviewExp = findViewById( R.id.answer_exp_tv_fast_review_exp );
        tvTitle = findViewById( R.id.answer_exp_tv_title );
        tvContent = findViewById( R.id.answer_exp_tv_content );
        cbCheckBtn = findViewById( R.id.answer_exp_cb_check_btn );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener( v -> {
            mPresenter.buriedPointContinueButton();
            setCallResult( DOMConstant.ANSWER_EXP_NEXT, true );
            finish();
        } );
    }

    @Override
    public void onCallTitle(String text, int num) {
        tvTitle.setText( String.format( text, num ) );
//        startAnim( num, 1000, s -> tvTitle.setText( String.format( text, s ) ) );
    }

    @Override
    public void onCallContent(String text, int num) {
        ivIcon.setImageResource(  R.drawable.ic_answer_exp_icon );
        tvTitle.setVisibility( View.VISIBLE );
        tvFastReviewExp.setVisibility( View.GONE );

        startAnim(num, s -> tvContent.setText( Html.fromHtml( String.format( text, s ) ) ) );
    }

    @Override
    public void onCallContentOfFastReview( String text, String content, int num ) {
        ivIcon.setImageResource( R.drawable.ic_answer_exp_fast_review_icon );
        tvTitle.setVisibility( View.GONE );
        tvFastReviewExp.setVisibility( View.VISIBLE );

        startAnim(num, s -> {
            tvFastReviewExp.setText( Html.fromHtml( String.format( text, s ) ) );
            tvContent.setText( content );
        });
    }

//    @Override
//    public boolean onEnableFullScreen() { return fda; }

    private void startAnim(int num, Consumer<String> call) {
        post(() -> {
            ValueAnimator anim = ValueAnimator.ofInt( 0, num );
            anim.setDuration( Math.min( 5000, 200 * num ) );
            anim.addUpdateListener(animation -> {
                int val = (Integer) animation.getAnimatedValue();
                if( call != null ) call.accept( String.valueOf( val ) );
            });
            anim.start();
        }, 800);
    }
}

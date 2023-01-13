package com.gjjy.frontlib.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.FrameAnimation;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.mvp.presenter.AnswerNodePresenter;
import com.gjjy.frontlib.mvp.view.AnswerNodeView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.frontlib.widget.RatingView;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.ybear.ybutils.utils.ResUtil;

/**
 * 答题里程碑页
 */
public class AnswerNodeFragment extends BaseFragment
        implements AnswerNodeView, OnVisibleChangedListener {
    @Presenter
    private AnswerNodePresenter mPresenter;

    private LinearLayout llLayout;
    private ImageView ivIcon;
    private ImageView ivRatingAnim;
    private RatingView ratRating;
    private TextView tvContent;
    private TextView tvProgressText;
    private CheckButton cbCheckBtn;

    private FrameAnimation.FrameControl mIconFrameCtrl;
    private FrameAnimation.FrameControl mRatingFrameCtrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_answer_node, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

//    private int mShowCount = 2;
//    private int mFragmentShowPos;
//    @Override
//    public void onResume() {
//        super.onResume();
//        --mShowCount;
//        if( getContext() != null && mShowCount == 0 ) {
//            onFragmentShow( getContext(), mFragmentShowPos );
//        }
//    }

    private int isVisibleChanged = View.INVISIBLE;
    @Override
    public void onResume() {
        super.onResume();

        if( isVisibleChanged == View.VISIBLE ) doVisibleChanged();
    }

    @Override
    public void onDestroy() {
        if( mIconFrameCtrl != null ) mIconFrameCtrl.stopNow();
        mRatingFrameCtrl.stopNow();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("onActivityResult " + requestCode + " | " + resultCode);
        if( requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_NEED_LOGIN_RESULT ) {
            if( resultCode == Activity.RESULT_OK ) {
                finish();
            }
//            else {
//                post(() -> mPresenter.nextItemOfBundle(), 250);
//            }
        }
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
        if( activity == null ) return;
        activity.setOnNetworkErrorRefreshClickListener( v -> mPresenter.saveProgress() );
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
//        LogUtil.e( "onFragmentShow -> " + mShowCount);
//        mFragmentShowPos = position;
////        if( mShowCount > 0 || mShowCount == -1 ) return;
//        mShowCount = -1;
//        mPresenter.onVisibleChanged( true );
//
//        BaseActivity activity = (BaseActivity) context;
//        activity.setOnNetworkErrorRefreshClickListener( v -> mPresenter.saveProgress() );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onVisibleChanged( false );
        isVisibleChanged = View.GONE;
    }

    private void initView() {
        llLayout = findViewById( R.id.answer_node_ll_layout );
        ivIcon = findViewById( R.id.answer_node_iv_icon );
        ratRating = findViewById( R.id.answer_node_rb_rating_bar );
        ivRatingAnim = findViewById( R.id.answer_node_iv_rating_anim );
        tvContent = findViewById( R.id.answer_node_tv_content );
        tvProgressText = findViewById( R.id.answer_node_tv_progress_text );
        cbCheckBtn = findViewById( R.id.answer_node_cb_check_btn );
    }

    private void initData() {
        if( getContext() == null ) return;
        ratRating.setIconSize( Utils.dp2Px( getContext(), 37 ) );
        ratRating.setIcon( R.drawable.ic_answer_node_star_on );
        ratRating.setBackgroundIcon( R.drawable.ic_answer_node_star_off );
        ratRating.setIconMargins( Utils.dp2Px( getContext(), 7 ) );
        ratRating.setMax( mPresenter.getRatingMax() );

//        String content = String.format(
//                getString( R.string.stringAnswerNode ),
//                "<img src='ic_text_lightning'/>"
//        );
//        tvContent.setText( mPresenter.isFullRating() ?
//                        R.string.stringAnswerNodeCompleted : R.string.stringAnswerNode
////                com.ybear.ybutils.utils.Utils.fromHtmlOfResImg(
////                        getResources(),
////                        content
////                )
//        );

        LogUtil.e( "AnswerNode -> initData -> " + mPresenter.isAnswerStatus() );
        if( mPresenter.isAnswerStatus() ) {
            mIconFrameCtrl = FrameAnimation
                    .create()
                    .time( 24 )
                    .loop( 1 )
                    .load( getContext(), "ic_answer_node_icon_", 0, 101 )
                    .into( ivIcon );
        }else {
            ivIcon.setImageResource( R.drawable.ic_answer_node_icon );
        }

        mRatingFrameCtrl = FrameAnimation
                .create()
                .time( 52 )
                .loop( 1 )
                .load( getContext(), "ic_answer_node_star_", 1, 26 )
                .setCallbackTarget( new FrameAnimation.CallbackTargetAdapter() {
                    @Override
                    public void onComplete(@NonNull View v, int count,
                                           @FrameAnimation.FrameStatus int status) {
                        super.onComplete( v, count, status );
                        if( status != FrameAnimation.FrameStatus.END ) return;
                        ratRating.showBackgroundIcon( ratRating.getRating() );
                        AnimUtil.setAlphaAnimator(
                                300,
                                animator -> ivRatingAnim.setVisibility( View.INVISIBLE ),
                                ivRatingAnim
                        );
                    }
                } )
//                .setCallbackTarget( new FrameAnimation.CallbackTargetAdapter() {
//                    @Override
//                    public void onTarget(@NonNull View v, @NonNull Drawable drawable,
//                                         int index, int count, @FrameAnimation.FrameStatus int status) {
//                        super.onTarget( v, drawable, index, count, status );
//                    }
//
//                    @Override
//                    public void onComplete(@NonNull View v, int count,
//                                           @FrameAnimation.FrameStatus int status) {
//                        super.onComplete( v, count, status );
////                        ratRating.showBackgroundIcon( ratRating.getRating() );
////                        ivRatingAnim.setVisibility( View.GONE );
//                    }
//                })
                .into( ivRatingAnim );

        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener(v -> mPresenter.nextItem());
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }

    @Override
    public void onCallRating(int num, int max, boolean isAnswerStatus) {
        //最大评级
        if( max != -1) {
            ratRating.setMax( max );
        }
        //评级
        if( num != -1 ) {
            ratRating.setRating( num );
            //开始星星音效
            startStarSound();
            //开始星星动画
            post( () -> startStarAnim( num ) );
        }

        if( isAnswerStatus && getContext() != null ) {
            tvContent.setText(
                    ResUtil.fromHtmlOfResImg(
                            getContext(),
                            String.format(
                                    getString( R.string.stringAnswerNode ),
                                    "<img src='ic_text_lightning'/>"
                            ),
                            R.drawable.class
                    )
            );
        }else {
            tvContent.setText( num == max - 1 ?
                    R.string.stringAnswerNodeCompletedOfFastLearning :
                    R.string.stringAnswerNodeOfFastLearning
            );
        }

        //进度文本
        tvProgressText.setText( mPresenter.getProgressText() );
    }

    @UiThread
    private void startStarAnim(int index) {
        View child = ratRating.getChildAt( index );
        //隐藏背景图标
        ratRating.hideBackgroundIcon( index );

        ivRatingAnim.setX(
                llLayout.getX() + ratRating.getX() +
                        child.getX() + ( child.getWidth() / 2F ) - ( ivRatingAnim.getWidth() / 2F )
        );
        ivRatingAnim.setY(
                llLayout.getY() + ratRating.getY() +
                        child.getY() + ( child.getHeight() / 2F ) - ( ivRatingAnim.getHeight() / 2F )
        );
        ivRatingAnim.setVisibility( View.VISIBLE );
        /* 播放动画 */
        if( mIconFrameCtrl != null ) mIconFrameCtrl.play( getContext() );
        mRatingFrameCtrl.play( getContext() );
    }

    private void startStarSound() {
        setCallResult( Constant.SoundType.SOUND_NODE_COMPLETE );
    }

    @Override
    public void onStartNeedLoginActivity() {
        StartUtil.startNeedLoginActivity( this, PageName.COURSE_REMIND );
        BuriedPointEvent.get().onWhetherLoginOfLogin(
                getActivity(),
                PageName.COURSE_LOGIN
        );
        post(() -> mPresenter.nextItemOfBundle(), 250);
    }
}

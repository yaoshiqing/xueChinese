package com.gjjy.frontlib.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.mvp.presenter.AnswerTestRefreshScorePresenter;
import com.gjjy.frontlib.mvp.view.AnswerTestRefreshScoreView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;

import static com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_BUY_VIP;
import static com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LACK_LIGHTNING_OF_REFRESH_SCORE;

/**
 刷新答题分数页面
 */
@Route(path = "/front/answerTestRefreshScoreActivity")
public class AnswerTestRefreshScoreActivity extends BaseActivity implements AnswerTestRefreshScoreView {
    @Presenter
    private AnswerTestRefreshScorePresenter mPresenter;

    private Toolbar tbToolbar;
    private TextView tvScore;
    private TextView tvRefreshBtn;
    private PopupWindow pwLockPopup;

    private DialogOption mLoadingDialog;
    private ValueAnimator mScoreValueAnim;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_answer_test_refresh_score );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();

        post( () -> mScoreValueAnim.start(), 300 );

    }

    @Override
    protected void onDestroy() {
        onCallLoadingDialog( false );
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        //闪电不足
        if( requestCode == REQUEST_CODE_LACK_LIGHTNING_OF_REFRESH_SCORE ) {
            finish();
            return;
        }
        //购买vip
        if( requestCode == REQUEST_CODE_BUY_VIP ) {
//            boolean result = resultCode == RESULT_OK;
//            //购买完vip后刷新战绩
//            if( result ) post( () -> mPresenter.refreshScore(), 300 );
            mPresenter.buriedPointRefreshRecordPageOfToBuyButton( resultCode == RESULT_OK );
        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.answer_test_refresh_score_tb_toolbar );
        tvScore = findViewById( R.id.answer_test_refresh_score_tv_score );
        tvRefreshBtn = findViewById( R.id.answer_test_refresh_score_tv_refresh_btn );
    }

    private void initData() {
        setStatusBarHeightForSpace( findViewById( R.id.toolbar_height_space ) );
        tbToolbar.setBackBtnOfImg( R.drawable.ic_white_back );
        mLoadingDialog = createLoadingDialog();
        mLoadingDialog.setCancelable( false );
        mLoadingDialog.setCanceledOnTouchOutside( false );

        //获取分数
        mScoreValueAnim = ValueAnimator.ofInt( 0, mPresenter.getScore() )
                .setDuration( 1200 );
        tvScore.setText( "0%" );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        mScoreValueAnim.addUpdateListener( anim ->
                tvScore.setText( ( ObjUtils.parseInt( anim.getAnimatedValue() ) + "%" ) )
        );

        tvRefreshBtn.setOnClickListener( v -> showRefreshLockPopup( mPresenter.isVip(), isVip -> {
            if( isVip ) {
                //刷新成绩
                mPresenter.refreshScore();
                return;
            }
            //购买会员
            com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( this );
        }));
    }

    private void showRefreshLockPopup(boolean isVip, Consumer<Boolean> onOkBtn) {
        if( getActivity() == null ) return;
        if( pwLockPopup == null ) {
            pwLockPopup = new PopupWindow(
                    View.inflate( getContext(), R.layout.popup_refresh_score_lock, null ),
                    Utils.dp2Px( getContext(), 320 ),
                    Utils.dp2Px( getContext(), 156 )
            );
            pwLockPopup.setOutsideTouchable( true );
            pwLockPopup.setOnDismissListener( () -> setTransparent( false ) );
        }
        View cView = pwLockPopup.getContentView();
        TextView tvContent = cView.findViewById( R.id.popup_refresh_score_lock_tv_content );
        TextView tvOkBtn = cView.findViewById( R.id.popup_refresh_score_lock_tv_ok_btn );
        if( isVip ) {
            tvContent.setText( ResUtil.fromHtmlOfResImg( this,
                    String.format(
                            getString( R.string.stringAnswerTestRefreshScoreOfLackLightningTips ),
                            "<img src='ic_popup_refresh_score_lock_icon'/>"
                    ),
                    R.drawable.class
            ));
        }else {
            tvContent.setText( R.string.stringAnswerTestRefreshScoreOfLackLightningOfVipTips );
        }
        tvOkBtn.setText( isVip ? R.string.stringOK : R.string.stringStartNow );

        tvOkBtn.setOnClickListener(v -> {
            pwLockPopup.dismiss();
            if( onOkBtn != null ) onOkBtn.accept( isVip );
        });

        if( pwLockPopup.isShowing() ) return;
        //显示位置
        pwLockPopup.showAsDropDown( tvRefreshBtn, 0, -Utils.dp2Px( this, 10 ) );
        setTransparent( true );
    }

    /**
     刷新成绩结果
     @param result      刷新结果
     */
    @Override
    public void onCallRefreshScoreResult(boolean result) {
        if( result ) {
            AnswerBaseEntity entity = mPresenter.getAnswerBaseEntity();
            //记录成绩
            entity.setRecordRecord( true );
            //开始考试
            StartUtil.startAnswerActivityOfTest( entity );
            mPresenter.buriedPointRefreshRecordPageOfBackButton();
            return;
        }
        //闪电不足
        StartUtil.startRefreshScoreLackLightningActivity( this );
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( isShow ) {
            if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
            return;
        }
        if( mLoadingDialog.isShowing() ) mLoadingDialog.dismiss();
    }
}

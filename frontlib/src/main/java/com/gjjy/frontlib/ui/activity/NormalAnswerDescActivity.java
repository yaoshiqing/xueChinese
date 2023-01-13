package com.gjjy.frontlib.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.frontlib.mvp.presenter.NormalAnswerDescPresenter;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.model.JsCallModel;
import com.gjjy.frontlib.mvp.view.NormalAnswerDescView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.speechsdk.PermManage;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.ContentWebView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 正常答题介绍页面
 */
@Route(path = "/front/normalAnswerDescActivity")
public class NormalAnswerDescActivity extends BaseActivity implements NormalAnswerDescView {
    @Presenter
    private NormalAnswerDescPresenter mPresenter;

    private Space sToolbarSpace;
    private Toolbar tbToolbar;
    private ContentWebView cwvWebView;
//    private XScrollView xsvList;
    private LinearLayout llOperation;
    private CheckButton cbStartBtn;
    private CheckButton cbSkipBtn;
    private DialogOption mLoadingDialog;
    private ImageView ivHideBtn;

    private boolean isReLoadAnswerDetail = false;

//    private int mScreenHeight;
//    private int[] mScrollViewXY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_answer_desc);
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( isReLoadAnswerDetail ) {
            isReLoadAnswerDetail = false;
            mPresenter.loadAnswerDetail();
        }
    }

    @Override
    protected void onDestroy() {
        cwvWebView.removeJavascriptInterface( "jsCall" );
        super.onDestroy();
    }

    private boolean isShowSkipTestDialog = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if( requestCode == StartUtil.REQUEST_CODE_SKIP_TEST_LIGHTNING ) {
            LogUtil.e( "resultCode " + resultCode + " | data " + data );
            if( resultCode == RESULT_OK && data != null ) {
                isShowSkipTestDialog = true;
                int type = data.getIntExtra( Constant.SKIP_TEST_LIGHTNING_TYPE, Constant.AnswerType.NONE );
                switch( type ) {
                    case Constant.AnswerType.SKIP_TEST:
                        startTest();
                        break;
                    case Constant.AnswerType.NORMAL:
                        mPresenter.startAnswer();
                        break;
                }
            }
        }
    }

    private void initView() {
        sToolbarSpace = findViewById( R.id.toolbar_height_space );
        tbToolbar = findViewById( R.id.normal_answer_desc_tb_toolbar );
//        xsvList = findViewById( R.id.normal_answer_desc_xsv_list );
        cwvWebView = findViewById( R.id.normal_answer_desc_cwv_web_view );
        llOperation = findViewById( R.id.normal_answer_desc_ll_operation );
        cbStartBtn = findViewById( R.id.normal_answer_desc_cb_start_btn );
        cbSkipBtn = findViewById( R.id.normal_answer_desc_cb_skip_btn );
        ivHideBtn = findViewById( R.id.normal_answer_desc_iv_hide_btn );
    }

//    private float mOperationY = -1;

    private void initData() {
        setStatusBarHeightForSpace( sToolbarSpace );
//        mScreenHeight = SysUtil.getScreenHeight( this );
        mLoadingDialog = createLoadingDialog();
        mLoadingDialog.setCancelable( false );
        mLoadingDialog.setCanceledOnTouchOutside( false );
        mLoadingDialog.show();

        boolean isComplete = mPresenter.isUnitComplete();
        cbStartBtn.setText( isComplete ? R.string.stringStartAnswerComplete : R.string.stringStartAnswer );
        cbSkipBtn.setText( isComplete ? R.string.stringSkipAnswerComplete : R.string.stringSkipAnswer );
        cbStartBtn.setEnabled( true );
        cbSkipBtn.setEnabled( true );

        cbSkipBtn.enableSkipStyle();

        mPresenter.loadAnswerDetail();

        reqPerm( result -> {
            if( !result ) finish();
        } );
    }

//    private boolean isWebScrollTop = false;
//    private int mWebScrollY;
    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
        cbStartBtn.setOnClickListener(v -> reqPerm(result -> {
            if( !result ) return;
            mPresenter.startAnswer();
        }));

        cbSkipBtn.setOnClickListener(v -> reqPerm(result -> {
            if( !result ) return;
            mPresenter.buriedPointSkipButton();

            if( isShowSkipTestDialog && !mPresenter.isVip() ) {
                isShowSkipTestDialog = false;
            }
            //进入过消耗闪电页并返回结果后才能打开
            if( isShowSkipTestDialog ) {
                showSkipTestDialog(mPresenter.isUnitComplete(), v1 -> {
                    //取消
                    mPresenter.buriedPointSkipDialogFalse();
                }, v2 -> {
                    //开始
                    startTest();
                });
                return;
            }
            //非会员，扣除闪电
            mPresenter.startSkipTestLightningConsumeActivity();
        }));


//        llOperation.setOnClickListener(v -> {
//            boolean isShow = llOperation.getY() <= tbToolbar.getY() + tbToolbar.getHeight();
//            //上移动画
//            startOperationViewAnim( llOperation.getY(), mOperationY );
//            ivHideBtn.setVisibility( isShow ? View.VISIBLE : View.GONE );
//        });

//        cwvWebView.setOnScrollChangeListener((ContentWebView.OnScrollChangeListener)
//                (v, x, y, ox, oy) -> {
//            if( mWebScrollY == 0 ) mWebScrollY = y;
//            isWebScrollTop = mWebScrollY > y;
//            ivHideBtn.setVisibility( isWebScrollTop ? View.GONE : View.VISIBLE );
//
//            startOperationViewAnim( llOperation.getY(), getWebScrollY( isWebScrollTop ) );
//            mWebScrollY = y;
//        });

//        cwvWebView.setOnScrollChangeListener((ContentWebView.OnScrollChangeListener)
//                (v, x, y, ox, oy) -> {
//            if( mWebScrollY == 0 ) mWebScrollY = y;
//            isWebScrollTop = mWebScrollY > y;
//            ivHideBtn.setVisibility( isWebScrollTop ? View.GONE : View.VISIBLE );
//
//            startOperationViewAnim( llOperation.getY(), getWebScrollY( isWebScrollTop ) );
//            mWebScrollY = y;
//        });
    }

    private void startTest() {
        mPresenter.startTest();
        mPresenter.buriedPointSkipDialogTrue();
    }
//    private boolean isScrollUp;

//    private void startOperationViewAnim(float startVal, float endVal) {
//        if( mVA == null ) {
//            mVA = ValueAnimator.ofFloat();
//            mVA.setDuration( 200 );
//            mVA.addUpdateListener(animation ->
//                    llOperation.setY( (float) animation.getAnimatedValue() )
//            );
//        }else if( !mVA.isRunning() && startVal != endVal ) {
//            mVA.setFloatValues( startVal, endVal );
//            mVA.start();
//        }
//    }

//    private float getWebScrollY(boolean isShow) {
//        float h = llOperation.getMeasuredHeight() / 2F;
//        return isShow ? mOperationY : mOperationY + h;
//    }

//    private void startOperationViewAnim(float startVal, float endVal) {
//        if( mVA == null ) {
//            mVA = ValueAnimator.ofFloat();
//            mVA.setDuration( 200 );
//            mVA.addUpdateListener(animation ->
//                    llOperation.setY( (float) animation.getAnimatedValue() )
//            );
//        }else if( !mVA.isRunning() && startVal != endVal ) {
//            mVA.setFloatValues( startVal, endVal );
//            mVA.start();
//        }
//    }

    private ValueAnimator mVA;

//    private float getWebScrollY(boolean isShow) {
//        float h = llOperation.getMeasuredHeight() / 2F;
//        return isShow ? mOperationY : mOperationY + h;
//    }

    private PermManage.Perm mPerm;
    private void reqPerm(Consumer<Boolean> call) {
        //权限检查
        if( mPerm == null ) mPerm = PermManage.create( this );
        mPerm.reqRecordAudioPerm(result -> {
            if( call != null ) call.accept( result );
        });
    }

    @Override
    public void onCallLoadUrl(String url) {
        mLoadingDialog.dismiss();
        cwvWebView.loadUrl( url );
    }

    @Override
    public void onCallNullUrl() {
        isReLoadAnswerDetail = true;
    }

//    /**
//     1.1.1版本
//     @param s
//     */
//    @Override
//    public void onCallShowTestBtn(boolean isShow) {
//        if( isShow && cbSkipBtn.getVisibility() == View.VISIBLE ) return;
//        AnimUtil.setAlphaAnimator(300, animator -> {
//            cbSkipBtn.setVisibility( isShow ? View.VISIBLE : View.GONE );
////            //跳题按钮是否展示处理完毕后，更新一次操作布局的Y坐标
////            mOperationY = llOperation.getY();
//        }, cbSkipBtn);
//    }

    @Override
    public void oCallError(String s) {
        if( mLoadingDialog != null ) mLoadingDialog.dismiss();
        showToastOfLong( s );
        finish();
    }

    @Override
    public void onCallJavascriptInterface(String name, JsCallModel model) {
        //开放外部接口
        cwvWebView.addJavascriptInterface( model, name );
    }

    @Override
    public void onCallEvaluateJavascript(String script, ValueCallback<String> call) {
        cwvWebView.evaluateJavascript( script, call );
    }
}

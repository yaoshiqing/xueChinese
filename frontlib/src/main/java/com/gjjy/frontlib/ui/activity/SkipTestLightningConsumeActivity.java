package com.gjjy.frontlib.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.Dialog;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.SkipTestLightningConsumePresenter;
import com.gjjy.frontlib.mvp.view.SkipTestLightningConsumeView;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 跳级考试闪电消耗页
 */
@Route(path = "/front/skipTestLightningConsumeActivity")
public class SkipTestLightningConsumeActivity extends BaseActivity implements SkipTestLightningConsumeView {
    @Presenter
    private SkipTestLightningConsumePresenter mPresenter;

    private Toolbar tbToolbar;
    private TextView tvLightningCount;
    private View vBuyVipBtn;
    private TextView tvStartTestBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip_test_lightning_consume);
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if( requestCode == StartUtil.REQUEST_CODE_BUY_VIP && resultCode == RESULT_OK ) {
            endPage( true, Constant.AnswerType.NONE );
        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.skip_test_lightning_consume_tb_toolbar );
        tvLightningCount = findViewById( R.id.skip_test_lightning_consume_tv_lightning_count );
        vBuyVipBtn = findViewById( R.id.skip_test_lightning_consume_tv_buy_vip_btn );
        tvStartTestBtn = findViewById( R.id.skip_test_lightning_consume_tv_start_test_btn );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        String content = String.format(
                getString( R.string.stringUseStartTest ),
                "<img src='ic_progress_lightning'/>"
        );
        tvStartTestBtn.setText( ResUtil.fromHtmlOfResImg(
                this,
                content,
                R.drawable.class
        ));
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener( v -> finish() );

        vBuyVipBtn.setOnClickListener( v -> {
            mPresenter.buriedPointBuyVip();
            if( !mPresenter.isVip() ) {
                StartUtil.startBuyVipActivity( getActivity() );
                return;
            }
            //开始答题
            endPage( true, Constant.AnswerType.SKIP_TEST );
        } );

        tvStartTestBtn.setOnClickListener( v -> {
            mPresenter.buriedPointUnLockTestTips();
            if( mPresenter.isHaveFewerLightning() ) {
                //开始答题，减少闪电
                startSkipTestOfReduceLightning();
                return;
            }
            //闪电不足提示
            showSkipTestFewerLightningDialog( v1 -> {
                if( mPresenter.isVip() ) {
                    //开始答题
                    endPage( true, Constant.AnswerType.SKIP_TEST );
                }else {
                    //购买会员
                    StartUtil.startBuyVipActivity( this );
                }
                mPresenter.buriedPointSkipTestFewerLightning();
            }, v2 -> {
                //返回正常答题
                endPage( true, Constant.AnswerType.NORMAL );
                mPresenter.buriedPointGoBackDesc();
            });
        } );
    }

    private void startSkipTestOfReduceLightning() {
        int from = mPresenter.getLightning();
        int to = from - mPresenter.getMaxLightningCount();
        valueAnimator( value -> {
            tvLightningCount.setText( String.valueOf( value ) );
            if( value == to ) {
                //是否存在剩余闪电
                endPage( true, Constant.AnswerType.SKIP_TEST );
            }
        }, from, to );
    }

    private void endPage(boolean result, @Constant.AnswerType int type) {
        Intent data = new Intent();
        data.putExtra( Constant.SKIP_TEST_LIGHTNING_TYPE, type );
        setResult( result ? RESULT_OK : RESULT_CANCELED, data );
        finish();
    }

    @Override
    public void onCallLightningCount(int count) {
        valueAnimator( value ->
                tvLightningCount.setText( String.valueOf( value ) ), 0, count
        );
    }

    private void valueAnimator(Consumer<Integer> call, int... values) {
        ValueAnimator va = ValueAnimator.ofInt(  values  );
        va.setDuration( 1000 );
        va.addUpdateListener( anim -> {
            if( call != null ) call.accept( (Integer) anim.getAnimatedValue() );
        } );
        va.start();
    }

    /**
     * 跳级考试闪电数量不足对话框
     * @param lBuyVip      购买会员点击事件监听器
     * @param lNormal      练习模式点击事件监听器
     */
    public void showSkipTestFewerLightningDialog(View.OnClickListener lBuyVip,
                                                 View.OnClickListener lNormal) {
        View view = View.inflate( this, R.layout.dialog_skip_test_fewer_lightning, null );
        view.setLayoutParams( new ViewGroup.LayoutParams(
                SysUtil.getScreenWidth( this ),
                SysUtil.getScreenHeight( this )
        ));
        DialogOption op = Dialog.with( this )
                .animOfBottomTranslate()
                .setDimAmount( 0.5F )
                .transparentBackground()
                .createOfFree( view );

        View vLayout = op.findViewById( R.id.dialog_skip_test_fewer_lightning_layout );
        TextView tvCount = op.findViewById( R.id.dialog_skip_test_fewer_lightning_tv_count );
        TextView tvStartBtn = op.findViewById( R.id.dialog_skip_test_fewer_lightning_tv_start_btn );
        TextView tvBackBtn = op.findViewById( R.id.dialog_skip_test_fewer_lightning_tv_go_practice_btn );

        if( vLayout != null ) vLayout.setOnClickListener( v -> op.dismiss() );
        if( tvCount != null ) tvCount.setText( String.valueOf( mPresenter.getLightning() ) );
        if( tvStartBtn != null ) tvStartBtn.setOnClickListener( lBuyVip );
        if( tvBackBtn != null ) tvBackBtn.setOnClickListener( lNormal );

        op.setOnDismissListener(dialog -> {
            //
            tvLightningCount.setVisibility( View.VISIBLE );
        });

        tvLightningCount.setVisibility( View.INVISIBLE );
        op.show();
    }
}

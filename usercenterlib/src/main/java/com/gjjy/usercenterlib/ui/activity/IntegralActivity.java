package com.gjjy.usercenterlib.ui.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.mvp.presenter.IntegralPresenter;
import com.gjjy.usercenterlib.mvp.view.IntegralView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

/**
 * 积分页面
 */
@Route(path = "/userCenter/integralActivity")
public class IntegralActivity extends BaseActivity implements IntegralView {
    @Presenter
    private IntegralPresenter mPresenter;

    private Toolbar tbToolbar;
    private TextView tvCount;
    private TextView tvDetailsBtn;
    private LinearLayout llPointsInviteLayout;
    private LinearLayout llPointsVipLayout;
    private LinearLayout llPointsRedeemLayout;
    private TextView tvPointsInviteBtn;
    private TextView tvPointsVipBtn;
    private TextView tvPointsRedeemBtn;

    private DialogOption mLoadingDialog;
    private DialogOption mIntegralPointsRedeemDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_integral );
        initView();
        initData();
        initListener();

        mPresenter.queryTotalCount();
    }

    @Override
    protected void onDestroy() {
        onCallLoadingDialog( false );
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.integral_tb_toolbar );
        tvCount = findViewById( R.id.integral_tv_count );
        tvDetailsBtn = findViewById( R.id.integral_tv_details_btn );
        llPointsInviteLayout = findViewById( R.id.integral_ll_earned_invite );
        llPointsVipLayout = findViewById( R.id.integral_ll_earned_vip );
        llPointsRedeemLayout = findViewById( R.id.integral_ll_redeem_energy_redeem );

        tvPointsInviteBtn = llPointsInviteLayout.findViewById( R.id.integral_points_tv_points_btn );
        tvPointsVipBtn = llPointsVipLayout.findViewById( R.id.integral_points_tv_points_btn );
        tvPointsRedeemBtn = llPointsRedeemLayout.findViewById( R.id.integral_points_tv_points_btn );
    }

    private void initData() {
        tbToolbar.setBackBtnOfImg( R.drawable.ic_white_back );
        ViewGroup[] vgPoints = { llPointsInviteLayout, llPointsVipLayout, llPointsRedeemLayout };
        int[] pointsImgRes = {
                R.drawable.ic_integral_invite_courteous,
                R.drawable.ic_integral_menber_points_doubled,
                R.drawable.ic_integral_energy_redeem
        };
        int[] pointsTitleRes = {
                R.string.stringIntegralPointsTitleInvite,
                R.string.stringIntegralPointsTitleVip,
                R.string.stringIntegralPointsTitleEnergyRedeem
        };
        int[] pointsContentRes = {
                R.string.stringIntegralPointsContentInvite,
                R.string.stringIntegralPointsContentVip,
                R.string.stringIntegralPointsContentEnergyRedeem
        };
        for( int i = 0; i < vgPoints.length; i++ ) {
            ViewGroup vg = vgPoints[ i ];
            ImageView ivImg = vg.findViewById( R.id.integral_points_iv_img );
            TextView tvTitle = vg.findViewById( R.id.integral_points_tv_points_title );
            TextView tvContent = vg.findViewById( R.id.integral_points_tv_points_content );
            ivImg.setImageResource( pointsImgRes[ i ] );
            tvTitle.setText( pointsTitleRes[ i ] );
            tvContent.setText( pointsContentRes[ i ] );
        }
        tvPointsInviteBtn.setText( R.string.stringIntegralPointsBtnInvite );
        tvPointsVipBtn.setText( R.string.stringIntegralPointsBtnVip );
        tvPointsRedeemBtn.setText( R.string.stringIntegralPointsBtnEnergyRedeem );

        mLoadingDialog = createLoadingDialog();
        mLoadingDialog.setCanceledOnTouchOutside( false );
        mLoadingDialog.setCancelable( false );
        FrameLayout.LayoutParams lpToolbar = (FrameLayout.LayoutParams) tbToolbar.getLayoutParams();
        lpToolbar.topMargin = SysUtil.getStatusBarHeight();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> {
            finish();
            mPresenter.buriedPointCreditsDetailPageOfBackButton();
        });

        //积分详情
        tvDetailsBtn.setOnClickListener( v -> {
            com.gjjy.basiclib.utils.StartUtil.startIntegralDetailsActivity();
            mPresenter.buriedPointCreditsDetailPageOfCreditsDetail();
        });

        //邀请好友
        tvPointsInviteBtn.setOnClickListener( v -> {
            StartUtil.startInviteFriendsActivity();
            mPresenter.buriedPointCreditsDetailPageOfToInviteButton();
        });

        //开通会员
        tvPointsVipBtn.setOnClickListener( v ->
                com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( this )
        );

        //积分兑换
        tvPointsRedeemBtn.setOnClickListener( v -> {
            mIntegralPointsRedeemDialog = showIntegralPointsRedeemDialog(
                    mPresenter.getIntegral(), count -> mPresenter.exchangeLightning( count )
            );
            mPresenter.buriedPointCreditsDetailPageOfExchangeButton();
        });
    }

    @Override
    public void onCallTotalCount(int count) {
        tvCount.setText( String.valueOf( count ) );
        //更新对话框的积分
        if( mIntegralPointsRedeemDialog != null && mIntegralPointsRedeemDialog.isShowing() ) {
            TextView tvIntegralCount = mIntegralPointsRedeemDialog.findViewById(
                    R.id.dialog_integral_points_redeem_tv_integral_count
            );
            if( tvIntegralCount != null ) {
                tvIntegralCount.setText( String.valueOf( count ) );
            }
        }
        setResult( RESULT_OK );
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            mLoadingDialog.show();
        }else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public int onStatusBarIconColor() { return SysUtil.StatusBarIconColor.WHITE; }
}

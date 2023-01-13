package com.gjjy.frontlib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.frontlib.R;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Model;

import static com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_EXCHANGE_POINTS_RESULT;

/**
 刷新答题分数闪电不足页面
 */
@Route(path = "/front/refreshScoreLackLightningActivity")
public class RefreshScoreLackLightningActivity extends BaseActivity {
    @Model
    private UserModel mUserModel;

    private TextView tvToRedeemBtn;
    private TextView tvToPracticeBtn;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_refresh_score_lack_lightning );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if( mUserModel.isVip() && requestCode == REQUEST_CODE_EXCHANGE_POINTS_RESULT ) {
            buriedPointEnergyShortagePageOfToExchangeButton( resultCode == RESULT_OK );
        }
    }

    private void initView() {
        tvToRedeemBtn = findViewById( R.id.answer_test_refresh_score_tv_to_redeem_btn );
        tvToPracticeBtn = findViewById( R.id.answer_test_refresh_score_tv_to_practice_btn );
    }

    private void initData() {

    }

    private void initListener() {
        //积分详情页面
        tvToRedeemBtn.setOnClickListener( v -> {
            StartUtil.startIntegralActivity( this );
            finish();
        } );
        //回到首页
        tvToPracticeBtn.setOnClickListener( v -> {
            if( mUserModel.isVip() ) {
                buriedPointEnergyShortagePageOfToPractice();
            }
            finish();
        });
    }

    public void buriedPointEnergyShortagePageOfToExchangeButton(boolean result) {
        BuriedPointEvent.get().onEnergyShortagePageOfToExchangeButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                result
        );
    }

    public void buriedPointEnergyShortagePageOfToPractice() {
        BuriedPointEvent.get().onEnergyShortagePageOfToPractice(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );
    }
}

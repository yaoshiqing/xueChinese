package com.gjjy.usercenterlib.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.presenter.AchievementPresenter;
import com.gjjy.usercenterlib.mvp.view.AchievementView;
import com.gjjy.usercenterlib.widget.RewardItemView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;

/**
 * 我的奖励页面
 */
@Route(path = "/userCenter/achievementActivity")
public class AchievementActivity extends BaseActivity implements AchievementView {
    @Presenter
    private AchievementPresenter mPresenter;

    private Toolbar tbToolbar;
    private ShapeTextView stvLoginTips;
    private RewardItemView rivHeart;
    private RewardItemView rivLightning;
    private RewardItemView rivXP;
    private int xpNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        xpNum = getIntent().getIntExtra(Constant.XP_NUM, 0);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tbToolbar = findViewById(R.id.achievement_tb_toolbar);
        stvLoginTips = findViewById(R.id.achievement_stv_not_logged_tips);
        rivHeart = findViewById(R.id.achievement_riv_heart);
        rivLightning = findViewById(R.id.achievement_riv_lightning);
        rivXP = findViewById(R.id.achievement_riv_xp);
    }

    private void initData() {
        setStatusBarHeight(R.id.toolbar_height_space);
//        rivHeart.setIcon( R.drawable.ic_user_center_achievement_heart_icon );
//        rivHeart.setTitle( R.string.stringAchievementLanternTitle );
//        rivHeart.setContent( R.string.stringAchievementLanternContent );
        rivHeart.setNumber(0);

//        rivLightning.setIcon( R.drawable.ic_user_center_achievement_lightning_icon );
//        rivLightning.setTitle( R.string.stringAchievementKnotTitle );
//        rivLightning.setContent( R.string.stringAchievementKnotContent );
        rivLightning.setNumber(0);
        rivXP.setNumber(0);

        //检查登录状态
        mPresenter.checkLoginStatus();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
    }

    @Override
    public void onCallIsLoginSuccess(boolean isSuccess) {
        stvLoginTips.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCallRewardMoney(int lightning, int heart) {
        rivLightning.setNumber(lightning);
        rivHeart.setNumber(heart);
        rivXP.setNumber(xpNum);
    }
}

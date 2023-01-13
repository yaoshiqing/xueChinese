package com.gjjy.loginlib.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.loginlib.R;
import com.gjjy.loginlib.utils.StartUtil;
import com.gjjy.basiclib.utils.Constant;

/**
 * 是否需要登录页面
 */
@Route(path = "/login/needLoginActivity")
public class NeedLoginActivity extends BaseActivity {
    private TextView tvLoginBtn;
    private TextView tvTalkLaterBtn;

    private boolean isLoginResult = false;
    private String mPageName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_need_login );
        initIntent();
        initView();
        initData();
        initListener();
    }

    @Override
    public void finish() {
        setResult( isLoginResult ? Activity.RESULT_OK : Activity.RESULT_CANCELED );
        super.finish();
        overridePendingTransition( R.anim.anim_right_in, R.anim.anim_left_out );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.e("NeedLoginActivity -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode
        );
        if( requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN_RESULT &&
                resultCode == RESULT_OK ) {
            isLoginResult = true;
            finish();
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        if( intent == null ) return;
        mPageName = intent.getStringExtra( Constant.PAGE_NAME );

        if( intent.getBooleanExtra( Constant.IS_FULL_SCREEN, false ) ) {
            //是否全屏展示
            setEnableFullScreen( true );
        }
    }

    private void initView() {
        tvLoginBtn = findViewById( R.id.need_login_tv_login_btn );
        tvTalkLaterBtn = findViewById( R.id.need_login_tv_talk_later_btn );
    }

    private void initData() {

    }

    private void initListener() {
        tvLoginBtn.setOnClickListener(v -> StartUtil.startLoginActivity( this, mPageName ) );

        tvTalkLaterBtn.setOnClickListener(v -> finish());
    }
}

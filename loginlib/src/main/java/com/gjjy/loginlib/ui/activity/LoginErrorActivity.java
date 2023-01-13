package com.gjjy.loginlib.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 登录失败界面
 */
@Route(path = "/login/loginErrorActivity")
public class LoginErrorActivity extends BaseActivity {
    public @interface LoginType {
        int GOOGLE = 0;
        int FACEBOOK = 1;
        int EMAIL = 2;
    }

    private Toolbar tbToolbar;
    private TextView tvTryAgainBtn;
    private TextView tvOtherLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login_error );
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.login_error_tb_toolbar );
        tvTryAgainBtn = findViewById( R.id.login_error_stv_try_again_btn );
        tvOtherLoginBtn = findViewById( R.id.login_error_tv_other_login_btn );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tvTryAgainBtn.setOnClickListener(v -> {
            setResult( RESULT_OK );
            finish();
        });

        tvOtherLoginBtn.setOnClickListener(v -> finish());
    }
}

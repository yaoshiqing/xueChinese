package com.gjjy.loginlib.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.loginlib.utils.StartUtil;
import com.ybear.mvp.annotations.Model;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;
import com.ybear.ybutils.utils.dialog.DialogOption;

/**
 * 退出登录后的登录页面
 */
@Route(path = "/login/exitAppLoginActivity")
public class ExitAppLoginActivity extends BaseActivity {
    @Model
    private UserModel mUserModel;

    private TextView tvLoginBtn;
    private TextView tvTalkLaterBtn;

    private boolean isLoginResult = false;
    private String mPageName = "";
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_exit_app_login );
        initIntent();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        post(() -> {
            if( isLoginResult = mUserModel.isLoginResult() ) finish();
            LogUtil.e( "ExitAppLoginActivity.onResume -> isLoginResult:" + isLoginResult );
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //新增逻辑
        isLoginResult = false;
        getStackManage().exitApp( this );
    }

    @Override
    public void finish() {
        setResult( isLoginResult ? Activity.RESULT_OK : Activity.RESULT_CANCELED );
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("ExitAppLoginActivity -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode
        );
        switch ( requestCode ) {
            case com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN_RESULT:
                if( resultCode == RESULT_OK ) {
                    isLoginResult = true;
                    finish();
                }else if( resultCode == RESULT_FIRST_USER ) {
                    com.gjjy.basiclib.utils.StartUtil.startGuideActivity(
                            this, false, false
                    );
                }
                break;
            case com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_GUIDE_RESULT:
                if( resultCode != RESULT_OK ) break;
                //设置完目的视为登录完成（主要是为了让调用的页面做处理）
                isLoginResult = true;
                finish();
                break;
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        if( intent == null ) return;
        mPageName = intent.getStringExtra( Constant.PAGE_NAME );
    }

    private void initView() {
        tvLoginBtn = findViewById( R.id.exit_app_login_tv_login_btn );
        tvTalkLaterBtn = findViewById( R.id.exit_app_login_tv_talk_later_btn );
    }

    private void initData() {
        mLoadingDialog = createLoadingDialog();
        mLoadingDialog.show();
        mUserModel.logOut( result -> post( () -> mLoadingDialog.dismiss() ) );
    }

    private void initListener() {
        tvLoginBtn.setOnClickListener(v -> StartUtil.startLoginActivity( this, mPageName ) );

        tvTalkLaterBtn.setOnClickListener(v -> com.gjjy.basiclib.utils.StartUtil.startGuideActivity(
                this,
                false,
                false
        ));
    }
}

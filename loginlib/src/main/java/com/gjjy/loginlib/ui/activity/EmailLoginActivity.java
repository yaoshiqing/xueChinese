package com.gjjy.loginlib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.loginlib.mvp.presenter.EmailLoginPresenter;
import com.gjjy.loginlib.mvp.view.EmailLoginView;
import com.gjjy.loginlib.utils.StartUtil;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.EditView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 邮箱登录界面
 */
@Route(path = "/login/emailLoginActivity")
public class EmailLoginActivity extends BaseActivity implements EmailLoginView {
    @Presenter
    private EmailLoginPresenter mPresenter;

    private Toolbar tbToolbar;
    private TextView tvSignUpBtn;
    private TextView tvForgetPwdBtn;
    private EditView evEmail;
    private EditView evPassword;
    private TextView tvLoginBtn;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_email_login );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
        //清除透明状态栏
        clearTranslucentStatus();
    }

    @Override
    public boolean onEnableImmersive() { return false; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("EmailLoginActivity -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode
        );
        if( requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_EMAIL_SIGN_UP_RESULT ) {
            if( resultCode == RESULT_OK && data != null ) {
//                onCallLoginResult( data.getStringExtra( Constant.EMAIL ), true, 0 );
                onCallShowLoadingDialog( true );

                String email = data.getStringExtra( Constant.EMAIL );
                String pwd = data.getStringExtra( Constant.PASSWORD );
                evEmail.setText( email );
                evPassword.setText( pwd );
                mPresenter.login( email, pwd, false );
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String acc = evEmail.getTextString();
        String pwd = evPassword.getTextString();
        if( keyCode == KeyEvent.KEYCODE_ENTER && mPresenter.checkLogin( acc, pwd ) ) {
            mPresenter.login( acc, pwd, false );
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initView() {
        tbToolbar = findViewById( R.id.email_login_tb_toolbar );
        tvSignUpBtn = findViewById( R.id.email_login_tv_sign_up_btn );
        evEmail = findViewById( R.id.email_login_ev_email );
        evPassword = findViewById( R.id.email_login_ev_pwd );
        tvForgetPwdBtn = findViewById( R.id.email_login_tv_forget_pwd_btn );
        tvLoginBtn = findViewById( R.id.email_login_tv_login_btn );

//        findViewById(R.id.test_login).setOnClickListener(v -> mPresenter.login(
//                "1443697172@qq.com",
//                "111111",
//                false
//        ));
    }

    private void initData() {
//        setStatusBarHeightForSpace( findViewById( R.id.toolbar_height_space ) );
        mLoadingDialog = createLoadingDialog();

        mPresenter.setSignUpText(
                getString( R.string.stringEmailNotEmail ),
                getString( R.string.stringEmailSignUp )
        );

        mPresenter.setForgetPasswordText(
                getString( R.string.stringEmailForgetPwd )
        );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        //注册
        tvSignUpBtn.setOnClickListener( v -> StartUtil.startEmailSignUpActivity( this ));

        //找回密码
        tvForgetPwdBtn.setOnClickListener( v -> mPresenter.startEmailForgetPasswordActivity());

        //登录
        tvLoginBtn.setOnClickListener(v -> mPresenter.login(
                evEmail.getTextString(),
                evPassword.getTextString(),
                true
        ));

        EditText etEmailView = evEmail.getView();
        etEmailView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkAccount( etEmailView.getText().toString() );
        });

        EditText etPwdView = evPassword.getView();
        etPwdView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkPassword( etPwdView.getText().toString() );
        });
    }

    @Override
    public void onCallSignUpText(Spanned s) {
        tvSignUpBtn.setText( s );
    }

    @Override
    public void onCallForgetPasswordText(Spanned s) {
        tvForgetPwdBtn.setText( s );
    }

    @Override
    public void onCallShowEmailTips(boolean isShow) {
        if( isShow ) {
            evEmail.showTips();
        }else {
            evEmail.hideTips();
        }
    }

    @Override
    public void onCallShowPasswordTips(boolean isShow) {
        if( isShow ) {
            evPassword.showTips();
        }else {
            evPassword.hideTips();
        }
    }

    @Override
    public void onCallLoginResult(String id, boolean result, int errorCode) {
        if( result ) {
            Intent intent = new Intent();
            intent.putExtra( Constant.EMAIL, id );
            setResult( RESULT_OK, intent );
            finish();
        }else {
            showToast( errorCode == 1102 ?
                    R.string.stringEmailLoginInfoErrorTips :
                    R.string.stringEmailLoginFailedTips
            );
        }
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( isShow ) {
            mLoadingDialog.show();
        }else {
            mLoadingDialog.dismiss();
        }
    }
}

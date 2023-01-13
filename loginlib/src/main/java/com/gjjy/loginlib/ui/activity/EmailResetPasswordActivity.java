package com.gjjy.loginlib.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.loginlib.mvp.presenter.EmailResetPasswordPresenter;
import com.gjjy.loginlib.mvp.view.EmailResetPasswordView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.Build;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.EditView;

/**
 * 邮箱重置密码页面
 */
@Route(path = "/login/emailResetPasswordActivity")
public class EmailResetPasswordActivity extends BaseActivity implements EmailResetPasswordView {
    @Presenter
    private EmailResetPasswordPresenter mPresenter;
    private Toolbar tbToolbar;
    private TextView tvSaveBtn;
    private EditView evVerificationCode;
    private EditView evPassword;
    private EditView evCheckPassword;

    private DialogOption mLoadingDialog;

    private Build mToastBuild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_email_reset_password );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
        //清除透明状态栏
        clearTranslucentStatus();
        //发送一次验证码
        evVerificationCode.callOnClickOfVerificationCodeBtn();
    }

    @Override
    public boolean onEnableImmersive() { return false; }

    @Override
    protected void onDestroy() {
        evVerificationCode.cancelCountDownTimer();
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.email_reset_password_tb_toolbar );
        evVerificationCode = findViewById( R.id.email_reset_password_ev_verification_code );
        evPassword = findViewById( R.id.email_reset_password_ev_pwd );
        evCheckPassword = findViewById( R.id.email_reset_password_ev_check_pwd );
        tvSaveBtn = findViewById( R.id.email_reset_password_tv_save_btn );
    }


    private void initData() {
        mLoadingDialog = createLoadingDialog();

        mToastBuild = new Build();
        mToastBuild.setGravity( Gravity.BOTTOM );
        mToastBuild.setYOffset( Utils.dp2Px( this, 60 ) );

        onCallAuthSave( false );
    }



    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        EditText etCodeView = evVerificationCode.getView();
        etCodeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkCode( s.toString() );
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        etCodeView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkCode( etCodeView.getText().toString() );
        });

        EditText etPwdView = evPassword.getView();
        etPwdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkPassword( s.toString() );
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        etPwdView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkPassword( etPwdView.getText().toString() );
        });

        EditText etPwd2View = evCheckPassword.getView();
        etPwd2View.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkEqPassword( etPwdView.getText().toString(), s.toString() );
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        etPwd2View.setOnFocusChangeListener((v, hasFocus) -> {
            if( hasFocus ) return;
            mPresenter.checkEqPassword(
                    etPwdView.getText().toString(),
                    etPwd2View.getText().toString()
            );
        });

        //保存密码
        tvSaveBtn.setOnClickListener(v -> mPresenter.resetPwd(
                evVerificationCode.getTextString(),
                evPassword.getTextString(),
                evCheckPassword.getTextString()
        ));

        evVerificationCode.setOnVerificationCodeButtonClickListener(v ->
                mPresenter.sendVerificationCode());
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
    public void onCallShowCheckPasswordTips(boolean isShow) {
        if( isShow ) {
            evCheckPassword.showTips();
        }else {
            evCheckPassword.hideTips();
        }
    }

    @Override
    public void onCallSendVerificationCodeResult(boolean result, int errorCode) {
        if( result ) {
            evVerificationCode.startCountDownTimer();
            return;
        }
        switch ( errorCode ) {
            case 1104:  //60秒后才可以再次获取验证码
                showToast( R.string.stringEmailVerificationCodeAgainTips );
                break;
            case 1105:  //验证码发送失败
                showToast( R.string.stringEmailVerificationCodeSendErrorTips );
                break;
            case 1106:  //验证码已失效
                showToast( R.string.stringEmailVerificationCodeInvalidTips );
                break;
            case 1107:  //验证码错误
                showToast( R.string.stringEmailVerificationCodeErrorTips );
                break;
            case 1109:  //该邮箱未被注册
                showToast( R.string.stringEmailNotRegTips );
                break;
            default:
                showToast( R.string.stringError );
                break;
        }
    }

    @Override
    public void onCallResetResult(boolean result) {
        if( result ) {
            showSuccessToast();
            setResult( RESULT_OK );
            finish();
            return;
        }
        showFailureToast();
        setResult( RESULT_CANCELED );
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( isShow ) {
            mLoadingDialog.show();
        }else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onCallAuthSave(boolean isAuth) {
        tvSaveBtn.setEnabled( isAuth );
        tvSaveBtn.setBackgroundResource(
                isAuth ? R.drawable.ic_touch_btn_true : R.drawable.ic_touch_btn_false
        );
    }

    private void showSuccessToast() {
        mToastBuild.setTextColor( getResources().getColor( R.color.colorSuccessToastText ) );
        mToastBuild.setBackgroundResource( R.drawable.shape_success_toast_bg);
        showToast( R.string.stringEmailResetSuccessTips, mToastBuild );
    }

    private void showFailureToast() {
        mToastBuild.setTextColor( getResources().getColor( R.color.colorFailureToastText ) );
        mToastBuild.setBackgroundResource( R.drawable.shape_failure_toast_bg);
        showToast( R.string.stringEmailResetFailureTips, mToastBuild );
    }
}

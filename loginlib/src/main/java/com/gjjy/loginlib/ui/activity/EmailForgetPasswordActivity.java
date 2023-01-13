package com.gjjy.loginlib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.loginlib.mvp.presenter.EmailForgetPasswordPresenter;
import com.gjjy.loginlib.mvp.view.EmailForgetPasswordView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.basiclib.widget.EditView;

/**
 * 邮箱忘记密码界面
 */
@Route(path = "/login/emailForgetPasswordActivity")
public class EmailForgetPasswordActivity extends BaseActivity implements EmailForgetPasswordView {
    @Presenter
    private EmailForgetPasswordPresenter mPresenter;
    private Toolbar tbToolbar;
    private EditView evEmail;
    private TextView tvOkBtn;

    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_email_forget_password );
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

        if( requestCode == StartUtil.REQUEST_CODE_EMAIL_RESET_PASSWORD_RESULT ) {
            if( resultCode == RESULT_OK ) {
                finish();
            }
        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.email_forget_password_tb_toolbar );
        evEmail = findViewById( R.id.email_forget_password_ev_email );
        tvOkBtn = findViewById( R.id.email_forget_password_tv_ok_btn );
    }


    private void initData() {
        mLoadingDialog = createLoadingDialog();
        onCallEnableOkBtn( false );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        //找回密码
        tvOkBtn.setOnClickListener( v -> mPresenter.forget( evEmail.getTextString() ));

        EditText etEmailView = evEmail.getView();
        etEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkAccount( s.toString() );
                onCallEnableOkBtn( !TextUtils.isEmpty( s ) );
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        etEmailView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkAccount( etEmailView.getText().toString() );
        });
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
    public void onCallForgetPassword(String email) {
        StartUtil.startEmailResetPasswordActivity( this, email );
    }

    @Override
    public void onCallError(int errorCode) {
        switch ( errorCode ) {
            case 1104:      //60秒后才可以再次获取验证码
                showToast( R.string.stringEmailVerificationCodeAgainTips );
                break;
            case 1105:      //验证码发送失败
                showToast( R.string.stringEmailVerificationCodeSendErrorTips );
                break;
            case 1109:      //邮箱未被注册
                showToast( R.string.stringEmailNotRegTips );
                break;
            default:
                showToast( R.string.stringError );
                break;
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

    public void onCallEnableOkBtn(boolean isAuth) {
        tvOkBtn.setEnabled( isAuth );
        tvOkBtn.setBackgroundResource(
                isAuth ? R.drawable.ic_touch_btn_true : R.drawable.ic_touch_btn_false
        );
    }
}

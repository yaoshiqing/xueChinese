package com.gjjy.loginlib.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.loginlib.mvp.presenter.EmailUpdatePasswordPresenter;
import com.gjjy.loginlib.mvp.view.EmailUpdatePasswordView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.Build;
import com.gjjy.loginlib.R;
import com.gjjy.basiclib.widget.EditView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 邮箱更新密码页面
 */
@Route(path = "/login/emailUpdatePasswordActivity")
public class EmailUpdatePasswordActivity extends BaseActivity implements EmailUpdatePasswordView {
    @Presenter
    private EmailUpdatePasswordPresenter mPresenter;
    private Toolbar tbToolbar;
    private TextView tvSaveBtn;
    private EditView evOldPassword;
    private EditView evPassword;
    private EditView evCheckPassword;

    private DialogOption mLoadingDialog;

    private Build mToastBuild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_email_update_password );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
        //清除透明状态栏
        clearTranslucentStatus();
    }

    @Override
    public boolean onEnableImmersive() { return false; }

    private void initView() {
        tbToolbar = findViewById( R.id.email_update_password_tb_toolbar );
        evOldPassword = findViewById( R.id.email_update_password_ev_old_pwd );
        evPassword = findViewById( R.id.email_update_password_ev_pwd );
        evCheckPassword = findViewById( R.id.email_update_password_ev_check_pwd );
        tvSaveBtn = findViewById( R.id.email_update_password_tv_save_btn );
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

        EditText etOldPwdView = evOldPassword.getView();
        etOldPwdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkOldPassword( s.toString() );
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        etOldPwdView.setOnFocusChangeListener((v, hasFocus) -> {
            if( !hasFocus ) mPresenter.checkOldPassword( etOldPwdView.getText().toString() );
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
                evOldPassword.getTextString(),
                evPassword.getTextString()
        ));

        evOldPassword.setOnVerificationCodeButtonClickListener(v ->
                mPresenter.sendVerificationCode());
    }

    @Override
    public void onCallShowOldPasswordTips(boolean isShow) {
        if( isShow ) {
            evOldPassword.showTips();
        }else {
            evOldPassword.hideTips();
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
            evOldPassword.startCountDownTimer();
            return;
        }
        switch ( errorCode ) {
            case 1109:  //该邮箱未被注册
                showToast( R.string.stringEmailNotRegTips );
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

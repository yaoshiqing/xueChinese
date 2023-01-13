package com.gjjy.loginlib.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.loginlib.mvp.view.EmailResetPasswordView;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.ReqUserModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

public class EmailResetPasswordPresenter extends MvpPresenter<EmailResetPasswordView> {
    @Model
    private LoginModel mLoginModel;
    @Model
    private ReqUserModel mReqUserModel;
    @Model
    private UserModel mUserModel;

    private String mEmail;

    private boolean mCodeIsNull = true;
    private boolean mPwdIsNull = true;
    private boolean mPwd2IsNull = true;

    public EmailResetPasswordPresenter(@NonNull EmailResetPasswordView view) {
        super(view);
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        mLoginModel.hideKeyboard( getActivity(), ev );
    }

    public void initIntent(Intent intent) {
        if( intent == null ) return;
        mEmail = intent.getStringExtra( Constant.EMAIL );
    }

    public boolean checkCode(@NonNull String code) {
        mCodeIsNull = TextUtils.isEmpty( code );
        checkIsNull();
        return !mCodeIsNull;
    }

    public boolean checkPassword(@NonNull String pwd) {
        boolean checkPwd = mLoginModel.checkPassword( pwd.trim() );
        viewCall(v -> v.onCallShowPasswordTips( !checkPwd ));

        mPwdIsNull = TextUtils.isEmpty( pwd );
        checkIsNull();
        return checkPwd;
    }

    public boolean checkEqPassword(@NonNull String pwd1, @NonNull String pwd2) {
        boolean eqPwd = mLoginModel.checkPassword( pwd1.trim(), pwd2.trim() );
        viewCall(v -> v.onCallShowCheckPasswordTips( !eqPwd ));

        mPwd2IsNull = TextUtils.isEmpty( pwd2 );
        checkIsNull();
        return eqPwd;
    }

    private void checkIsNull() {
        viewCall(v -> v.onCallAuthSave(
                !mCodeIsNull &&
                        !mPwdIsNull &&
                        !mPwd2IsNull
        ));
    }

    public void resetPwd(String code, String password, String checkPassword) {
        if( mLoginModel.checkTouchIntervalTimeOut( LoginModel.TimeOut.LONG_TIME ) ) return;

//        boolean checkName = mLoginModel.checkName( code );
        boolean checkPwd = mLoginModel.checkPassword( password );
        boolean eqPwd = mLoginModel.checkPassword( password, checkPassword );

        viewCall(v -> {
            v.onCallShowPasswordTips( !checkPwd );
            v.onCallShowCheckPasswordTips( !eqPwd );
        });

        if( !checkPwd || !eqPwd ) return;
        //重置密码
        mReqUserModel.reqResetEmailPasswordByCode(code, mEmail, password, result -> {
            //埋点保存密码
            BuriedPointEvent.get().onSendVerificationCodePageSaveButton( getContext(), result );
            //重置流程
            viewCall(v -> v.onCallResetResult( result ));
        });

    }

    public void sendVerificationCode() {
        viewCall(v -> v.onCallShowLoadingDialog( true ));

        mReqUserModel.reqSendEmailCode(mEmail, result -> {
            //埋点发送验证码
            BuriedPointEvent.get().onSendVerificationCodePageOfResentButton(
                    getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result == 1
            );
            viewCall(v -> {
                v.onCallShowLoadingDialog( false );
                v.onCallSendVerificationCodeResult( result == 1, result );
            });
        });
    }
}
package com.gjjy.loginlib.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.loginlib.mvp.view.EmailLoginView;
import com.gjjy.loginlib.utils.StartUtil;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

public class EmailLoginPresenter extends MvpPresenter<EmailLoginView> {
    @Model
    private LoginModel mLoginModel;
    @Model
    private UserModel mUserModel;

    private String[] userInfo;

    public EmailLoginPresenter(@NonNull EmailLoginView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        //检查是否允许自动登录
        checkAutoLogin();
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        mLoginModel.hideKeyboard( getActivity(), ev );
    }

    public void initIntent(Intent intent) {
        if( intent == null ) return;

        String acc = intent.getStringExtra( Constant.EMAIL );
        String pwd = intent.getStringExtra( Constant.PASSWORD );

        if( !TextUtils.isEmpty( acc ) && !TextUtils.isEmpty( pwd ) ) {
            userInfo = new String[ 2 ];
            userInfo[ 0 ] = acc;
            userInfo[ 1 ] = pwd;
        }
    }

    public void checkAutoLogin() {
        if( userInfo != null ) login( userInfo[ 0 ], userInfo[ 1 ], false );
    }

    public void startEmailForgetPasswordActivity() {
        //埋点忘记密码
        BuriedPointEvent.get().onEmailLoginPageOfForgetPassword( getContext() );
        //忘记密码页面
        StartUtil.startEmailForgetPasswordActivity();
    }

    public void login(String account, String password, boolean isCheckTouch) {
        if( isCheckTouch && mLoginModel.checkTouchIntervalTimeOut( LoginModel.TimeOut.LONG_TIME ) ) {
            return;
        }

        EmailLoginView v = getView();
        if( v == null ) return;

        v.onCallShowEmailTips( !mLoginModel.checkEmail( account ) );
        v.onCallShowPasswordTips( !mLoginModel.checkPassword( password ) );

        if( !checkLogin( account, password ) ) return;

        v.onCallShowLoadingDialog( true );

        mUserModel.loginEmail(account, password, result -> {
            //登录埋点
            BuriedPointEvent.get().onEmailLoginPageOfLoginButton(
                    getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result == 1
            );
            //登录流程
            viewCall(v2 -> {
                v2.onCallShowLoadingDialog( false );
                v2.onCallLoginResult( account, result == 1, result );
            });
        });
    }

    public boolean checkLogin(String account, String password) {
        return mLoginModel.checkEmail( account ) && mLoginModel.checkPassword( password );
    }

    public void setSignUpText(String left, String right) {
        EmailLoginView v = getView();
        if( v != null ) v.onCallSignUpText( mLoginModel.getAccountTipsTextStyle( left, right ) );
    }

    public void setForgetPasswordText(String s) {
        EmailLoginView v = getView();
        if( v != null ) v.onCallForgetPasswordText( mLoginModel.getUnderlineTextStyle( s ) );
    }

    public boolean checkAccount(@NonNull String account) {
        boolean checkAcc = mLoginModel.checkEmail( account.trim() );
        viewCall(v -> v.onCallShowEmailTips( !checkAcc ));
        return checkAcc;
    }

    public boolean checkPassword(@NonNull String pwd) {
        boolean checkPwd = mLoginModel.checkPassword( pwd.trim() );
        viewCall(v -> v.onCallShowPasswordTips( !checkPwd ));
        return checkPwd;
    }
}
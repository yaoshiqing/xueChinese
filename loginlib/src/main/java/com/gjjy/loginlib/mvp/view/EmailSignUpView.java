package com.gjjy.loginlib.mvp.view;

import android.text.Spanned;

import com.ybear.mvp.view.MvpViewable;

public interface EmailSignUpView extends MvpViewable {

    void onCallLoginText(Spanned s);

    void onCallShowNameTips(boolean isShow);

    void onCallShowEmailTips(boolean isShow);

    void onCallShowPasswordTips(boolean isShow);

    void onCallShowCheckPasswordTips(boolean isShow);

    void onCallSignUpResult(String id, String pwd, boolean result, int errorCode);

    void onCallUserRegisterResult(String id, String pwd, String phone, boolean isSuccess, String errMsg);

    void onCallSendSmsCodeResult(String phone, int errCode, String errMsg);

    void onCallAuthSignUp(boolean isAuth);
}

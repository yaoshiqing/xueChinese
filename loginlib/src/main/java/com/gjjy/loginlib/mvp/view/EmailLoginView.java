package com.gjjy.loginlib.mvp.view;

import android.text.Spanned;

import com.ybear.mvp.view.MvpViewable;

public interface EmailLoginView extends MvpViewable {
    void onCallSignUpText(Spanned s);
    void onCallForgetPasswordText(Spanned s);
    void onCallShowEmailTips(boolean isShow);
    void onCallShowPasswordTips(boolean isShow);
    void onCallLoginResult(String id, boolean result, int errorCode);
    void onCallShowLoadingDialog(boolean isShow);
}

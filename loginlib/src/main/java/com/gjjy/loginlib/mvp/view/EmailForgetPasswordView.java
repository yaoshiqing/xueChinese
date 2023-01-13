package com.gjjy.loginlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface EmailForgetPasswordView extends MvpViewable {
    void onCallShowEmailTips(boolean isShow);
    void onCallForgetPassword(String email);
    void onCallError(int errorCode);
    void onCallEnableOkBtn(boolean isAuth);
    void onCallShowLoadingDialog(boolean isShow);
}

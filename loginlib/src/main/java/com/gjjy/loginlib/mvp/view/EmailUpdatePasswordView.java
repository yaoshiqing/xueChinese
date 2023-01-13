package com.gjjy.loginlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface EmailUpdatePasswordView extends MvpViewable {
    void onCallShowOldPasswordTips(boolean isShow);
    void onCallShowPasswordTips(boolean isShow);
    void onCallShowCheckPasswordTips(boolean isShow);
    void onCallSendVerificationCodeResult(boolean result, int errorCode);
    void onCallResetResult(boolean result);
    void onCallAuthSave(boolean isSave);
    void onCallShowLoadingDialog(boolean isShow);
}

package com.gjjy.login.google;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * 登录授权回调
 */
public interface GoogleCallback {
    /**
     * 授权成功
     * @param account   授权信息
     * @param isFirst   之前是否授权过
     */
    void onSuccess(GoogleSignInAccount account, boolean isFirst);
    /**
     * 取消授权
     */
    void onCancel();
    /**
     * 授权失败
     */
    void onFailure();
}

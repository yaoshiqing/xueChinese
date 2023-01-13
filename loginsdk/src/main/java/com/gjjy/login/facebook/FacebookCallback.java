package com.gjjy.login.facebook;

import com.facebook.FacebookException;

/**
 * 登录授权回调
 */
public interface FacebookCallback {
    /**
     * 授权成功
     * @param result   授权信息
     */
    void onSuccess(AccountResult result);

    /**
     * 取消授权
     */
    void onCancel();

    /**
     * 授权失败
     */
    void onError(FacebookException error);
}
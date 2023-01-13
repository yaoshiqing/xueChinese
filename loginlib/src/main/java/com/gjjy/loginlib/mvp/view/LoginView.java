package com.gjjy.loginlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface LoginView extends MvpViewable {
    void onCallLoginResult(String type, String id, int result);
    void onCallIsShowLoading(boolean isShow);
    void onCallDisLoginTips(boolean isDisable);
}

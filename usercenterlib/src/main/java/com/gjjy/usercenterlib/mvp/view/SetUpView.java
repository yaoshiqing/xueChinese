package com.gjjy.usercenterlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;
import com.gjjy.basiclib.dao.entity.UserDetailEntity;

public interface SetUpView extends MvpViewable {
    void onCallRemindSwitch(boolean enable);
    void onCallLogOut(boolean result);
    void onCallShowResetPassword(boolean isShow);
    void onCallUpdateUserData(UserDetailEntity data);
    void onCallLoginResult(boolean result);
    void onCallShowLoadingDialog(boolean isShow);
}

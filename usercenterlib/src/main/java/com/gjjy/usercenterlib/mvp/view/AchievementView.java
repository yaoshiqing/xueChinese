package com.gjjy.usercenterlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface AchievementView extends MvpViewable {
    void onCallIsLoginSuccess(boolean isSuccess);
    void onCallRewardMoney(int lightning, int heart);
}

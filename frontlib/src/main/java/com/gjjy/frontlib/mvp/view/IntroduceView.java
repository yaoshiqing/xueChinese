package com.gjjy.frontlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface IntroduceView extends MvpViewable {
    void onCallIsHaveOpenLightning(boolean isHave);
    void onCallAddEndPager(String url, int endPage);
    void onCallLoadingDialog(boolean isShow);
}

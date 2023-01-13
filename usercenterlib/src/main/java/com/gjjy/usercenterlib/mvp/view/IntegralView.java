package com.gjjy.usercenterlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface IntegralView extends MvpViewable {
    void onCallTotalCount(int count);
    void onCallLoadingDialog(boolean isShow);
}

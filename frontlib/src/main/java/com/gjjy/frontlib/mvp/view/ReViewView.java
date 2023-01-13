package com.gjjy.frontlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface ReViewView extends MvpViewable {
    void onCallReviewNewCount(int count);
    void onCallShowLoadingDialog(boolean isShow);
}

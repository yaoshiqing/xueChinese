package com.gjjy.frontlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface AnswerTestRefreshScoreView extends MvpViewable {
    void onCallRefreshScoreResult(boolean result);
    void onCallLoadingDialog(boolean isShow);
}

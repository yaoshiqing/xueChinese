package com.gjjy.frontlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface AnswerNodeView extends MvpViewable {
//    double ID = Math.random() * 99999999D;
    void onCallRating(int num, int max, boolean isAnswerStatus);
    void onStartNeedLoginActivity();
}

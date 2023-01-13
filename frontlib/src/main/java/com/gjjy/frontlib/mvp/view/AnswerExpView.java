package com.gjjy.frontlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface AnswerExpView extends MvpViewable {
    void onCallTitle(String text, int num);
    void onCallContent(String text, int num);
    void onCallContentOfFastReview(String text, String content, int num);
}

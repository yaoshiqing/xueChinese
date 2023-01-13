package com.gjjy.frontlib.mvp.view;

import com.gjjy.basiclib.utils.Constant;
import com.ybear.mvp.view.MvpViewable;

public interface AnswerEndView extends MvpViewable {
    void onCallIconType(@Constant.AnswerType int answerType);
    void onCallIconTitle(String s);
    void onCallTitle(String s);
    void onCallContent(String s);
    void onCallIsShowCheckButton(boolean isShow);
    void onStartNeedLoginActivity();
}

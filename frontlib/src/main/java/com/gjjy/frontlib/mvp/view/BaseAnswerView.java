package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.view.MvpViewable;

public interface BaseAnswerView extends MvpViewable {
    void onCallTitle(String title);
    void onCallQuestion(String question);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
}

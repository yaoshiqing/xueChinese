package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.view.MvpViewable;

public interface HearingView extends MvpViewable {
    void onCallTitle(String title);
    void onCallAudio(String s);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
    void onCallOptionsCorrect();
    void onCallOptionsError();
    void onCheckVisibility(int visibility);
    void onCallSpeed(float speed);
    void onPlay();
}

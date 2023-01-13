package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.view.MvpViewable;

public interface VideoView extends MvpViewable {
    void onCallVideoUrl(String url);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
    void onCallOptionsCorrect();
    void onCallOptionsError();
    void onCallSpeed(float speed);
    void onCheckVisibility(int visibility);
    void onPlay();
}

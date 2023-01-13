package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.view.MvpViewable;

public interface TranslateView extends MvpViewable {
    void onCallTitle(String title);
    void onCallQuestion(String question);
    void onCallAudioUrl(String url);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
    void onCallOptionsCorrect();
    void onCallOptionsError();
    void onCallSpeed(float speed);
    void onCheckVisibility(int visibility);
    void onPlay();
}

package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.mvp.view.MvpViewable;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;

import java.util.List;

public interface VoiceView extends MvpViewable {
    void onCallTitle(String title);
    void onCallAudioUrl(String url);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
    void onCallData(String[] pinyin, String[] data);
    void onCallStatus(List<Syll> statusList);
    void onEndRecording();
    void onResult(boolean result);
    void onCheckVisibility(int visibility);
    void onShowAudioBtn(boolean isShow);
    void onCallSpeed(float speed);
    void onPlay();
}

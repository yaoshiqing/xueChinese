package com.gjjy.usercenterlib.mvp.view;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.ybear.mvp.view.MvpViewable;

public interface FeedbackView extends MvpViewable {
    void onCallContactHintColor(@ColorInt int color);
    void onCallSubmitResult(boolean result);
    void onCallAutoFillInEmail(@NonNull String email);
    void onCallCopyEmailResult(boolean result);
}

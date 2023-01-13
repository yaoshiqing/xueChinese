package com.gjjy.basiclib.mvp.model;

import android.webkit.JavascriptInterface;

import androidx.core.util.Consumer;

public class JsCallModel {
    private Consumer<Boolean> mCallFeedback;

    @JavascriptInterface
    public void feedbackResult(int result) {
        if( mCallFeedback != null ) mCallFeedback.accept( result == 1 );
    }

    public void setCallFeedback(Consumer<Boolean> call) {
        mCallFeedback = call;
    }
}

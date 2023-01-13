package com.gjjy.frontlib.mvp.view;

import android.webkit.ValueCallback;

import com.gjjy.frontlib.mvp.model.JsCallModel;
import com.ybear.mvp.view.MvpViewable;

public interface NormalAnswerDescView extends MvpViewable {
    void onCallLoadUrl(String url);
    void onCallNullUrl();
//    void onCallShowTestBtn(boolean isShow);
    void oCallError(String msg);
    void onCallJavascriptInterface(String name, JsCallModel model);
    void onCallEvaluateJavascript(String script, ValueCallback<String> call);
}

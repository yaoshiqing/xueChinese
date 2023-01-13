package com.gjjy.frontlib.mvp.model;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import androidx.annotation.Nullable;

import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;

public class JsCallModel extends MvpModel {
    private final SpeechSynthesizer mTts;
    public JsCallModel(SpeechSynthesizer tts) {
        super();
        mTts = tts;
        LogUtil.e(String.format(
                "JsCallModel -> Javascript interface initialization is %s!",
                tts == null ? "failure" : "success"
        ));
    }

    @Override
    public void onDestroyModel() {
        super.onDestroyModel();
        mTts.release();
    }

//    @JavascriptInterface
//    public void playTts(int num) {
//        playTts( String.valueOf( num ) );
//    }

    @JavascriptInterface
    public void playTts(@Nullable String s) {
        if( TextUtils.isEmpty( s ) ) return;
        if( s != null && s.startsWith("http") || "undefined".equals( s ) ) return;
        if( mTts != null ) mTts.start( s );
        LogUtil.i("JsCallModel -> playTts:" + s + " | tts:" + mTts);
    }
}

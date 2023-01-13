package com.gjjy.speechsdk;

import com.gjjy.speechsdk.evaluator.parser.result.Result;

public interface OnResultListener {
    void onResult(Result result, boolean isLast);
    void onError(int errCode, String msg);
}
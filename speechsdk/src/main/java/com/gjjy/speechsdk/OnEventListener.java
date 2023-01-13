package com.gjjy.speechsdk;

import android.os.Bundle;

public interface OnEventListener {
    void onEvent(int eventType, int arg1, int arg2, Bundle b);
}

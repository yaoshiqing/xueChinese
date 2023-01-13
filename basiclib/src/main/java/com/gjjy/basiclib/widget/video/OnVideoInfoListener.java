package com.gjjy.basiclib.widget.video;

import androidx.annotation.NonNull;

public interface OnVideoInfoListener {
    void onSource(@NonNull Object data, @NonNull Class<?> dataType);
    void onInfo(int progress, int total);
    void onProgress(int progress);
}
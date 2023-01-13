package com.gjjy.basiclib.widget.video;

public interface OnVideoStatusListener {
    void onVideoReady();
    void onVideoPlay();
    void onVideoPause();
    void onVideoStop();
    void onVideoReset();
    void onVideoRelease();
    void onVideoCompletion(int currentPlayNum, int playTotal, boolean isCompletion);
    void onBufferingUpdate(int percent);
    boolean onError(int what, int extra);
}
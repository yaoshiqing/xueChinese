package com.gjjy.basiclib.widget.video;

import android.net.Uri;

import androidx.annotation.NonNull;

public interface VideoCallback {
    void setDataSource(@NonNull String... paths);
    void setDataSource(@NonNull Uri... uris);
    void play();
    void on();
    void next();
    void pause();
    void stop();
    void reset();
    void release();
    void setSpeed(float speed);
    void seekTo(int progress);
    void setLooping(boolean enable);
    boolean isLooping();
    boolean isPlaying();
    boolean isLandscapeView();
    boolean isLandscapeScreen();
    void setEnableOrientation(boolean enable);
    void addVideoStatusListener(OnVideoStatusListener l);
    void removeVideoStatusListener(OnVideoStatusListener l);
    void addOrientationChangedListener(ScreenOrientationChangeListener l);
    void removeOrientationChangedListener(ScreenOrientationChangeListener l);
    void addVideoInfoListener(OnVideoInfoListener l);
    void removeVideoInfoListener(OnVideoInfoListener l);
    void setFollowSystemRotation(boolean enable);

    VideoScreen setScreenSizeOfPortrait(int width, int height);
    VideoScreen setScreenSizeOfLandscape(int width, int height);
    VideoScreen setEnableFullScreenOfLandscape(boolean enable);
}

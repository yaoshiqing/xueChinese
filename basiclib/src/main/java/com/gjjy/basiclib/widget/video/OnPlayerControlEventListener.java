package com.gjjy.basiclib.widget.video;

import android.widget.*;

public interface OnPlayerControlEventListener
{
    void onPlayerClick(final ImageView p0);
    
    void onPlayerEvent(final ImageView p0);
    
    void onPauseEvent(final ImageView p0);
    
    void onOnEvent(final ImageView p0);
    
    void onNextEvent(final ImageView p0);
    
    void onFlipClick(final ImageView p0);
    
    void onPortraitFlipEvent(final ImageView p0);
    
    void onLandscapeFlipEvent(final ImageView p0);
    
    void onFinishEvent(final ImageView p0);
}

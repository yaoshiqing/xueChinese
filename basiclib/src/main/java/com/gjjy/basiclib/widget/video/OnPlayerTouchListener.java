package com.gjjy.basiclib.widget.video;

public interface OnPlayerTouchListener
{
    void onDown();
    
    void onClick();
    
    void onLongClick();
    
    void onDoubleClick();
    
    void onSlide(final int p0, final int p1);
    
    void onControlViewDisplay(final boolean p0);
}

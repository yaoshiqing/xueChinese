package com.gjjy.basiclib.widget.video;

import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationControlBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationSlideProgressBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationTitleBar;

public interface PlayerOperationLayoutIF
{
    PlayerOperationTitleBar onTitleBar();
    
    PlayerOperationControlBar onControlBar();
    
    PlayerOperationSlideProgressBar onSlideProgressBar();
}

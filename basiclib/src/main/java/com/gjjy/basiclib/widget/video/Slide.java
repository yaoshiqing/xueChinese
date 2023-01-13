package com.gjjy.basiclib.widget.video;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@interface Slide {
    public static final int LEFT_TOP_AND_BOTTOM = 1;
    public static final int RIGHT_TOP_AND_BOTTOM = 2;
    public static final int LEFT_AND_RIGHT_OF_LEFT = 3;
    public static final int LEFT_AND_RIGHT_OF_RIGHT = 4;
}

package com.gjjy.xuechinese.mvp.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.CLASS )
public @interface PathType {
    int LISTEN_DAILY = 1001;        //每日聆听
    int POPULAR_VIDEOS = 1002;      //热门视频
    int TARGETED_LEARNING = 1003;   //专项学习
}
package com.gjjy.frontlib.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface OptionsLayout{
    int NONE = 0;              //无效
    int LINEAR = 1;            //线性布局
    int GRID = 2;              //网格布局
    int VIDEO_GRID = 3;        //网格布局
//    int DRAG = 3;            //拖拽布局
    int LINEAR_GRID = 4;       //线性网格布局
}

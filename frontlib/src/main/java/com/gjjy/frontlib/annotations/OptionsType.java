package com.gjjy.frontlib.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface OptionsType{
    int NONE = 0;                           //无效
    int CENTER_TEXT = 1001;                //中间文本
    int CENTER_PINYIN = 1002;              //中间拼音
    int CENTER_TEXT_AND_PINYIN = 1003;     //中间文本和拼音
    int IMAGE = 1006;                      //图片
    @Deprecated
    int CENTER_TEXT_AND_IMAGE = 5;          //中间文本和图片
    @Deprecated
    int CENTER_PINYIN_AND_IMAGE = 6;        //中间拼音和图片
    int BOTTOM_TEXT_AND_IMAGE = 1004;      //底部文本和图片
    int BOTTOM_PINYIN_AND_IMAGE = 1005;    //底部拼音和图片
}

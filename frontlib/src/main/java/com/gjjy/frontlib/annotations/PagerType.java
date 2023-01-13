package com.gjjy.frontlib.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface PagerType {
    int NOT_PAGER = -1;         //空列表
    int NONE = 0;               //无效
    int HEARING = 1;            //听力题
    int VOICE = 2;              //口语题
    int SELECT = 3;             //选择题
    int MATCH = 4;              //匹配题
    int TRANSLATE = 5;          //翻译题
    int LABEL_TRANSLATE = 6;    //翻译标签题
    int LABEL_HEARING = 7;      //听力标签题
    int VIDEO = 8;              //视频题
}
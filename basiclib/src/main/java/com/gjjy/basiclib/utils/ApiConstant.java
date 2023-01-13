package com.gjjy.basiclib.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ApiConstant {
    /**
     * 添加用户结果
     * 类型：boolean
     */
    String ADD_USER_RESULT = "ADD_USER_RESULT";
}
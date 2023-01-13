package com.gjjy.basiclib.push;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.CLASS )
public @interface ID {
    int UID_TAG = 100;
}
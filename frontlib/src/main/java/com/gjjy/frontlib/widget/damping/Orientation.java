package com.gjjy.frontlib.widget.damping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Orientation {
    int HORIZONTAL = 0;
    int VERTICAL = 1;
}

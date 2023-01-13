package com.gjjy.basiclib.buried_point;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.CLASS )
public @interface LoginType {
    String NONE = null;
    String FACEBOOK = "Facebook";
    String GOOGLE = "Google";
    String EMAIL = "Email";
}
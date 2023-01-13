package com.gjjy.basiclib.buried_point;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.CLASS )
public @interface QuestionType {
    String LISTENING = "Listening";
    String SPEAKING = "Speaking";
    String TRANSLATING = "Translating";
    String SLOW_AUDIO = "SlowAudio";
}
package com.gjjy.frontlib.mvp.presenter;

public interface IAnswer {
    void check();
    void nextItem(boolean isCorrect);
    void nextItemDelay(boolean isCorrect);
}

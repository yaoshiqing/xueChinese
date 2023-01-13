package com.gjjy.basiclib.statistical;

public interface IStatisticalEvent {
    void onSignIn(String method, String id);
    void onSignOff(String id);
    void onExitApp();
}
package com.gjjy.usercenterlib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface LearningReminderView extends MvpViewable {
    void onCallOpenRemindError();
//    void onCallShowRemind(boolean enable);
    void onCallIsEnableRemind(boolean enable);
    void onCallTime(int hour, int minute);
    void onCallWeekStatus(boolean[] weekStatus);
}

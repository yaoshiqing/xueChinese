package com.gjjy.xuechinese.mvp.view;

import com.gjjy.xuechinese.mvp.model.PathType;
import com.ybear.mvp.view.MvpViewable;

public interface MainView extends MvpViewable {
    void onCallDispatchScheme(@PathType int type, int id, String videoId, String name);
    void onCheckUpdate(String version, String content, String url, boolean isCancelable);
    void onCheckAnnouncement(String content);
    void onCallToFront();
    void onCallShowFrontInitGuide();
    void onCallShowFindInitGuide();
    void onCallShowMeInvitationCodeInitGuide();
}

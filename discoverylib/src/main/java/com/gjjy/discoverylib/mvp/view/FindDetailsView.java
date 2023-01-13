package com.gjjy.discoverylib.mvp.view;

import com.ybear.mvp.view.MvpViewable;

public interface FindDetailsView extends MvpViewable {
    void onCallTitle(String title);
    void onCallVideoId(String videoId, int progress, int totalSecond);
    void onCallShowShareButton(boolean isShow);
    void onCallCollectStatus(boolean isCollect, boolean isShowToast);
    void onCallShareResult(boolean result);
    void onCallShowLoadingDialog(boolean isShow);
}

package com.gjjy.discoverylib.mvp.view;

import com.ybear.mvp.view.MvpViewable;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;

import java.util.List;

public interface MyCourseView extends MvpViewable {
    void onCallCancelCollectId(long id);
    void onCallCollectDataList(int page, List<DiscoveryListAdapter.ItemData> list, boolean isVip);
    void onCallHaveReadDataList(int page, List<DiscoveryListAdapter.ItemData> list, boolean isVip);
    void onCallShowLoadingDialog(boolean isShow);
}

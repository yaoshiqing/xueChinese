package com.gjjy.discoverylib.mvp.view;

import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface ListenDailyMoreListView extends MvpViewable {
    void onCallDataList(List<DiscoveryListAdapter.ItemData> list, boolean isPaging);
    void onCallIsVip(boolean isVip);
    void onCallShowLoadingDialog(boolean isShow);
}

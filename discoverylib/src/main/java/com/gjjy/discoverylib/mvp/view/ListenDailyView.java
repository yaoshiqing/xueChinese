package com.gjjy.discoverylib.mvp.view;

import com.ybear.mvp.view.MvpViewable;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;

import java.util.List;

public interface ListenDailyView extends MvpViewable {
    void onCallDataList(List<DiscoveryListAdapter.ItemData> list);
    void onCallIsVip(boolean isVip);
}

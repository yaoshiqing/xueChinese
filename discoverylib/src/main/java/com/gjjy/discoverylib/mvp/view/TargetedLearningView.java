package com.gjjy.discoverylib.mvp.view;

import com.ybear.mvp.view.MvpViewable;
import com.gjjy.discoverylib.adapter.TargetedLearningListAdapter;

import java.util.List;

public interface TargetedLearningView extends MvpViewable {
    void onCallDataList(List<TargetedLearningListAdapter.ItemData> list);
}

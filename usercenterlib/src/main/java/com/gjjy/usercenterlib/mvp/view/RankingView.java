package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface RankingView extends MvpViewable {
    void onCallRankingList(List<RankingAdapter.ItemData> list, int currentIndex, int lastIndex);
    void onCallShowLoadingDialog(boolean isShow);
}

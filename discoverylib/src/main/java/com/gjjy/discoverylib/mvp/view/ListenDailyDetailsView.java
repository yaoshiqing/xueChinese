package com.gjjy.discoverylib.mvp.view;

import com.gjjy.discoverylib.widget.VerticalScrollSubtitlesView;

import java.util.List;

public interface ListenDailyDetailsView extends FindDetailsView {
    void onCallSubtitlesData(String imgUrl, List<VerticalScrollSubtitlesView.VssAdapter.ItemData> list);
}

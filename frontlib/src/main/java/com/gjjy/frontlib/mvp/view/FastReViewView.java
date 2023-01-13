package com.gjjy.frontlib.mvp.view;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.adapter.FastReViewAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface FastReViewView extends MvpViewable {
    void onCallDataList(@NonNull List<FastReViewAdapter.ItemData>list);
    void onCallShowLoadingDialog(boolean isShow);
}

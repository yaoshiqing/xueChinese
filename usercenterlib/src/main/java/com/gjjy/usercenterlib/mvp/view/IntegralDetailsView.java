package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.IntegralAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface IntegralDetailsView extends MvpViewable {
    void onCallTotalCount(int count);
    void onCallIntegralList(List<IntegralAdapter.ItemData> list, boolean isPaging);
    void onCallMonthTotal(int incomeCount, int expendCount, int pos);
    void onCallLoadingDialog(boolean isShow);
}

package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.VouchersAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface VouchersView extends MvpViewable {
    void onCallVouchersList(List<VouchersAdapter.ItemData> list, int index, boolean isPaging);
    void onCallBuyVipTicketResult(int code, int pos);
}

package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.MsgListAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface MsgListView extends MvpViewable {
    void onCallMsgList(List<MsgListAdapter.ItemData>list, boolean isPaging);
    void onCallUnread(int position);
    void onCallUnreadAll();
    void onCallShowLoadingDialog(boolean isShow);
}

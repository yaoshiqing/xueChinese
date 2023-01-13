package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.MsgCenterAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface MsgCenterView extends MvpViewable {
    void onCallMsgList(List<MsgCenterAdapter.ItemData>list);
    void onCallShowLoadingDialog(boolean isShow);
}

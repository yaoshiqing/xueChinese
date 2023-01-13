package com.gjjy.usercenterlib.mvp.view;

import com.gjjy.usercenterlib.adapter.FriendsAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface FriendsView extends MvpViewable {
    void onCallFriendsList(List<FriendsAdapter.ItemData> list, boolean isPaging);
}

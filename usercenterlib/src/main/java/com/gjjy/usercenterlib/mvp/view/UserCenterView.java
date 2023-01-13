package com.gjjy.usercenterlib.mvp.view;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.ybear.mvp.view.MvpViewable;
import com.gjjy.basiclib.api.entity.VouchersPopupChildEntity;

import java.util.List;

public interface UserCenterView extends MvpViewable {
    void onCallShowMessageCenter(boolean enable);
    void onCallIntegralTotalCount(int count);
    void onCallUpdateUserData(UserDetailEntity data);
    void onCallRankingList(List<RankingAdapter.ItemData> list);
    void onCallEditFriendInviteResult(@Nullable String code, int result);
    void onCallShowLoadingDialog(boolean isShow);
    void onCallVouchersPopup(VouchersPopupChildEntity list);
}

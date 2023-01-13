package com.gjjy.frontlib.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.adapter.FrontListAdapter;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface FrontView extends MvpViewable {
    void onCallRewardMoney(int lightning, int oldLightning, boolean isLightningChanged,
                           int heart, int oldHeart, boolean isHeartChanged
    );
//    void onCallChangedSectionNum(int position, int itemPosition, int sectionNum);
//    void onCallChangedUnitStatus(int position, int itemPosition, int unitStatus);
    void onCallAllCategory(@NonNull List<FrontMenuAdapter.ItemData> list,
                           @Nullable FrontMenuAdapter.ItemData currentItem
    );
    void onCallCategoryContent(int id, @NonNull List<FrontListAdapter.ItemData> list);
    void onCallAllCategoryTitle(int id);
    void onCallLoadingDialog(boolean isShow);
}

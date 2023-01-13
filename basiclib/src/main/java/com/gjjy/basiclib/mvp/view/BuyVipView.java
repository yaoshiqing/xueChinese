package com.gjjy.basiclib.mvp.view;

import com.gjjy.basiclib.entity.BuyVipEvaluationEntity;
import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface BuyVipView extends MvpViewable {
    void onCallNormalVipOptionData(List<BuyVipOptionEntity> list);
    void onCallEvalListData(List<BuyVipEvaluationEntity> list);
    void onCallBuyVipResult(@PurchaseState int state, boolean result, String errMsg);
    void onCallLoadingDialog(boolean isShow);
}

package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.ReqIntegralModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.view.IntegralView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.toast.ToastManage;

public class IntegralPresenter extends MvpPresenter<IntegralView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqIntegralModel mReqIntegralModel;

    public IntegralPresenter(@NonNull IntegralView view) {
        super(view);
    }

    public void queryTotalCount() {
        mReqIntegralModel.reqTotal( false, count -> {
            mUserModel.setIntegral( count );
            viewCall( v -> {
                v.onCallTotalCount( count );
                v.onCallLoadingDialog( false );
            });
        });
    }

    public void exchangeLightning(int lightningCount) {
        viewCall( v -> v.onCallLoadingDialog( true ) );
        mReqIntegralModel.reqLightningExchange( lightningCount, point -> viewCall( v -> {
            if( getContext() == null ) return;
            boolean result = point > 0;
            int toastResId = result ?
                    R.string.stringIntegralExchangeSuccess :
                    R.string.stringIntegralExchangeFailure;
            v.onCallLoadingDialog( false );
            ToastManage.get().showToast( getContext(), toastResId );
            if( result ) v.onCallTotalCount( point );
            buriedPointCreditsDetailPageOfExchangePopupOfExchangeNow( result );
        }));
    }

    public int getIntegral() { return mUserModel.getIntegral(); }

    public void buriedPointCreditsDetailPageOfCreditsDetail() {
        BuriedPointEvent.get().onCreditsDetailPageOfCreditsDetail( getContext() );
    }

    public void buriedPointCreditsDetailPageOfToInviteButton() {
        BuriedPointEvent.get().onCreditsDetailPageOfToInviteButton(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), true
        );
    }

    public void buriedPointCreditsDetailPageOfExchangeButton() {
        BuriedPointEvent.get().onCreditsDetailPageOfExchangeButton(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() )
        );
    }

    public void buriedPointCreditsDetailPageOfExchangePopupOfExchangeNow(boolean result) {
        BuriedPointEvent.get().onCreditsDetailPageOfExchangePopupOfExchangeNow(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result
        );
    }

    public void buriedPointCreditsDetailPageOfBackButton() {
        BuriedPointEvent.get().onCreditsDetailPageOfBackButton(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() )
        );
    }
}
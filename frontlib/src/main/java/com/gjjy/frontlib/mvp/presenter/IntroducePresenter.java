package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.ReqProvideUnitModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.frontlib.mvp.view.IntroduceView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class IntroducePresenter extends MvpPresenter<IntroduceView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqProvideUnitModel mProvideUnitModel;
    private final String[] mHtmlUrls = Config.mIntroduceListUrls;

    public IntroducePresenter(@NonNull IntroduceView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mProvideUnitModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    public int getMaxProgress() { return mHtmlUrls.length; }

    public String[] getHtmlUrls() { return mHtmlUrls; }

    public void reqHaveOpenLightningStatus() {
        mProvideUnitModel.reqProvideUnitDetail(entity -> {
            boolean isHave = entity.getStatus() == 2;
            viewCall(v -> {
                v.onCallIsHaveOpenLightning( isHave );
                v.onCallAddEndPager( mHtmlUrls[ mHtmlUrls.length - 1 ], isHave ? 0 : 1 );
            });
            if( isHave ) buriedPointHaveOpenLightning();
        });
    }

    public void reqProvideUnitOpenLightning() {
        viewCall( v -> v.onCallLoadingDialog( true ) );
        mProvideUnitModel.reqProvideUnitOpenLightning(result -> viewCall( v -> {
            v.onCallLoadingDialog( false );
            buriedPointGetOpenLightning();
        }));
    }

    public void buriedPointCloseButton1() {
        BuriedPointEvent.get().onPrefaceModuleMandarinIntroducePageOfCloseButton( getContext() );
    }

    public void buriedPointCloseButton2() {
        BuriedPointEvent.get().onPrefaceModulePinyinIntroducePageOfCloseButton( getContext() );
    }

    public void buriedPointCloseButton3() {
        BuriedPointEvent.get().onPrefaceModuleTonesIntroducePageOfCloseButton( getContext() );
    }

    private void buriedPointGetOpenLightning() {
        BuriedPointEvent.get().onPrefaceModuleFirstModuleCompletePageOfContinueButton( getContext() );
    }

    private void buriedPointHaveOpenLightning() {
        BuriedPointEvent.get().onPrefaceModuleRepetitionModuleCompletePageOfContinueButton( getContext() );
    }
}

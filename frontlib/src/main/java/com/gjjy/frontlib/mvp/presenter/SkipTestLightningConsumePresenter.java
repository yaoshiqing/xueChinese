package com.gjjy.frontlib.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.mvp.view.SkipTestLightningConsumeView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

public class SkipTestLightningConsumePresenter extends MvpPresenter<SkipTestLightningConsumeView> {
    @Model
    private UserModel mUserModel;

    private final int mMaxLightningCount = 4;
    private int mLightning;
    private AnswerBaseEntity mAnswerBaseEntity;


    public SkipTestLightningConsumePresenter(@NonNull SkipTestLightningConsumeView view) {
        super(view);

    }

    public void initIntent(Intent intent) {
        if( intent == null ) {
            finish();
            return;
        }
        mAnswerBaseEntity = intent.getParcelableExtra( Constant.ANSWER_BASE_ENTITY );
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate( savedInstanceState );
        //查询闪电数量
        queryLightningCount();
    }

    public boolean isHaveFewerLightning() {
        return getLightning() >= mMaxLightningCount;
    }

    public boolean isVip() {
        return mUserModel.isVip();
    }

    public int getLightning() {
        return mLightning;
    }

    public int getMaxLightningCount() { return mMaxLightningCount; }

    public void queryLightningCount() {
        mLightning = mUserModel.getLightning();
//        mLightning = 3;
        viewCall( v -> v.onCallLightningCount( mLightning ), 250 );
    }

    public void buriedPointBuyVip() {
        if( mAnswerBaseEntity == null ) return;
        //非会员的限时弹窗
        BuriedPointEvent.get().onCourseDescriptionOfSkipTestPageOfExperienceUnlimitedSkipButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }

    public void buriedPointUnLockTestTips() {
        if( mAnswerBaseEntity == null ) return;
        //非会员的限时弹窗
        BuriedPointEvent.get().onCourseDescriptionOfSkipTestPageOfStartExamButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }

    public void buriedPointSkipTestFewerLightning() {
        if( mAnswerBaseEntity == null ) return;
        //非会员的限时弹窗
        BuriedPointEvent.get().onSkipTestPageOfLightningLackPopupOfUnlimitedSkipButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }

    public void buriedPointGoBackDesc() {
        if( mAnswerBaseEntity == null ) return;
        //非会员的限时弹窗
        BuriedPointEvent.get().onSkipTestPageOfLightningLackPopupOfGotoPracticeButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }
}
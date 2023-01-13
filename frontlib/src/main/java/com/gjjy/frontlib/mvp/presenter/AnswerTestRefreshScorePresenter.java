package com.gjjy.frontlib.mvp.presenter;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.ReqAnswerModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.frontlib.mvp.view.AnswerTestRefreshScoreView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class AnswerTestRefreshScorePresenter extends MvpPresenter<AnswerTestRefreshScoreView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqAnswerModel mReqAnswer;

    private AnswerBaseEntity mAnswerBaseEntity;

    public AnswerTestRefreshScorePresenter(@NonNull AnswerTestRefreshScoreView view) {
        super(view);
        mReqAnswer.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    public void initIntent(Intent intent) {
        if( intent == null ) {
            finish();
            return;
        }
        mAnswerBaseEntity = intent.getParcelableExtra( Constant.ANSWER_BASE_ENTITY );
        if( mAnswerBaseEntity == null ) {
            finish();
        }
    }

    public void refreshScore() {
        viewCall( v -> v.onCallLoadingDialog( true ) );

        mReqAnswer.reqRefreshScore( result -> viewCall( v -> {
            v.onCallRefreshScoreResult( result );
            v.onCallLoadingDialog( false );
        } ) );
    }

    public void buriedPointRefreshRecordPageOfToBuyButton(boolean result) {
        BuriedPointEvent.get().onRefreshRecordPageOfToBuyButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                result
        );
    }

    public void buriedPointRefreshRecordPageOfBackButton() {
        BuriedPointEvent.get().onRefreshRecordPageOfBackButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );
    }

    public int getScore() { return mAnswerBaseEntity.getScore(); }

    public AnswerBaseEntity getAnswerBaseEntity() { return mAnswerBaseEntity; }

    public boolean isVip() { return mUserModel.isVip(); }
}

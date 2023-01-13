package com.gjjy.frontlib.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Consumer;

import com.gjjy.frontlib.api.AnswerDescH5;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.mvp.model.JsCallModel;
import com.gjjy.frontlib.mvp.view.NormalAnswerDescView;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.ReqAnswerModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

import java.util.ArrayList;

/**
 * P层 - 正常答题介绍页面
 */
public class NormalAnswerDescPresenter extends MvpPresenter<NormalAnswerDescView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqAnswerModel mReqAnswer;
    private int mUnitStatus, mSectionNum;
    private ArrayList<Integer> mSectionIds;
    private AnswerBaseEntity mAnswerBaseEntity;
    @Nullable
    private SpeechSynthesizer mTts;

    private long mViewDuration = 0;

    public NormalAnswerDescPresenter(@NonNull NormalAnswerDescView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mReqAnswer.setUid( mUserModel.getUid(), mUserModel.getToken() );
        mViewDuration = System.currentTimeMillis();
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        if( mTts == null && getContext() != null ) {
            mTts = SpeechSynthesizer.get().init( getContext() ).build();
            viewCall(v -> v.onCallJavascriptInterface( "jsCall" , new JsCallModel( mTts ) ));
        }
    }

    @Override
    public void onLifePause() {
        super.onLifePause();

        getViewCount(viewCount -> BuriedPointEvent.get().onModuleOfCourseDescription(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                viewCount,
                System.currentTimeMillis() - mViewDuration,
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        ));
    }

    public boolean isLoginStatus() { return mUserModel.isLoginResult(); }

    public void initIntent(Intent intent) {
        if( intent == null ) {
            callError();
            return;
        }

        mSectionIds = intent.getIntegerArrayListExtra( Constant.SECTION_IDS );
        mAnswerBaseEntity = intent.getParcelableExtra( Constant.ANSWER_BASE_ENTITY );

        LogUtil.e( "NormalAnswerDesc -> initIntent -> " + mAnswerBaseEntity );
        if( mAnswerBaseEntity == null || mSectionIds == null || mSectionNum == -1 ) {
            callError();
            return;
        }

//        mSectionNum = intent.getIntExtra( Constant.SECTION_NUM, -1 );
    }

    public boolean isUnitComplete() {
        return mAnswerBaseEntity != null && mAnswerBaseEntity.getUnitStatus() == 2;
    }

    public boolean isVip() {
        return mUserModel.isVip();
    }

    public void loadAnswerDetail() {
        mReqAnswer.reqAnswerDetail(mAnswerBaseEntity.getUnitId(), data -> {
            mUnitStatus = data.getUnitStatus();
            mSectionNum = data.getSectionNum();
            String descUrl = data.getExplainH();

            if( data.getCode() == 0 ) {
                viewCall( NormalAnswerDescView::onCallNullUrl );
            }

            if( TextUtils.isEmpty( descUrl ) ) {
                descUrl = TextUtils.isEmpty( data.getExplain() ) ?
                        new AnswerDescH5().toFullUrl() :
                        data.getExplain();
            }

            String finalDescUrl = descUrl;
            viewCall(v -> {
                v.onCallLoadUrl( finalDescUrl );
//                //1.1.1版本
//                v.onCallShowTestBtn( /*mSectionNum == 0 && */mUnitStatus != 2 );
            });

            LogUtil.e("loadAnswerDetail -> " +
                    "code:" +  data.getCode() + " | " +
                    "Url:" + descUrl + " | " +
                    "UnitStatus:" + mUnitStatus + " | " +
                    "SectionNum:" + mSectionNum
            );
        });
    }

    public void startAnswer() {
        BuriedPointEvent.get().onCourseDescriptionOfStartButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );

        if( isUnitComplete() ) {
            BuriedPointEvent.get().onCourseDescriptionOfPracticeButton(
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

        StartUtil.startAnswerActivityOfNormal(
                mAnswerBaseEntity.setUnitStatus( mUnitStatus ),
                mSectionIds,
                mSectionNum
        );
        finish();
    }

    public void startTest() {
        StartUtil.startAnswerActivityOfModuleTest(
                mAnswerBaseEntity.setUnitStatus( mUnitStatus )
        );
        finish();
    }

    private boolean isBuriedPointSkipButton;
    public void buriedPointSkipButton() {
        if( isBuriedPointSkipButton ) return;
        isBuriedPointSkipButton = true;
        BuriedPointEvent.get().onCourseDescriptionOfPassTestButton(
                getContext(),
                mAnswerBaseEntity.getUnitId(),
                true
        );

        if( isUnitComplete() ) {
            BuriedPointEvent.get().onCourseDescriptionOfQuickLearn(
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

    public void startSkipTestLightningConsumeActivity() {
        StartUtil.startSkipTestLightningConsumeActivity( getActivity(), mAnswerBaseEntity );
    }

    private boolean isBuriedPointSkipDialogTrue;
    public void buriedPointSkipDialogTrue() {
        if( isBuriedPointSkipDialogTrue ) return;
        isBuriedPointSkipDialogTrue = true;
        BuriedPointEvent.get().onCourseDescriptionOfStartTestButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                true
        );

        if( isUnitComplete() ) {
            BuriedPointEvent.get().onCourseDescriptionOfQuickLearnPopupOfStartButton(
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

    private boolean isBuriedPointSkipDialogFalse;
    public void buriedPointSkipDialogFalse() {
        if( isBuriedPointSkipDialogFalse ) return;
        isBuriedPointSkipDialogFalse = true;
        BuriedPointEvent.get().onCourseDescriptionOfCancelTestButton( getContext() );

        if( isUnitComplete() ) {
            BuriedPointEvent.get().onCourseDescriptionOfQuickLearnPopupOfCancelButton(
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

    private void getViewCount(Consumer<Integer> call) {
        viewCall(v -> v.onCallEvaluateJavascript("javascript:playNum()", value -> {
            if( call == null ) return;
            call.accept(Integer.valueOf(
                    TextUtils.isEmpty( value ) || "null".equals( value ) ? "0" : value
            ));
        }));
    }

    @UiThread
    private void callError() {
        NormalAnswerDescView v = getView();
        if( v == null || v.getContext() == null ) return;
        //暂无数据
        v.oCallError( v.getContext().getResources().getString( R.string.stringNotData ) );
    }


}

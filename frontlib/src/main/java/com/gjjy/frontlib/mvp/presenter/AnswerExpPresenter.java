package com.gjjy.frontlib.mvp.presenter;

import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.view.AnswerExpView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

public class AnswerExpPresenter extends MvpPresenter<AnswerExpView> {
    @Model
    private UserModel mUserModel;
    @Constant.AnswerType
    private int mAnswerType;
    private int mAnswerExp = 0;
    private int mAnswerTodayExp = 0;
    private AnswerBaseEntity mAnswerBaseEntity;

    public AnswerExpPresenter( @NonNull AnswerExpView view) {
        super(view);
    }

    public void initIntent(Intent intent) {
        if( intent == null ) return;
        mAnswerType = intent.getIntExtra( Constant.ANSWER_TYPE, Constant.AnswerType.NORMAL );
        mAnswerExp = intent.getIntExtra( Constant.ANSWER_EXP, 0 );
        mAnswerTodayExp = intent.getIntExtra( Constant.ANSWER_TODAY_EXP, 0 );
        mAnswerBaseEntity = intent.getParcelableExtra( Constant.ANSWER_BASE_ENTITY );
    }

    public int getAnswerType() { return mAnswerType; }

    public void loadData() {
        int answerType = getAnswerType();
        int titleRes = R.string.stringAnswerExpTitle;
        int contentRes;
        switch ( answerType ) {
            case Constant.AnswerType.TEST:
                contentRes = R.string.stringAnswerExpContentOfTestOut;
                break;
            case Constant.AnswerType.SKIP_TEST:
                contentRes = R.string.stringAnswerExpContentOfSkip;
                break;
            case Constant.AnswerType.FAST_REVIEW:
                contentRes = R.string.stringAnswerExpContentOfFastReview;
                break;
            default:
                contentRes = R.string.stringAnswerExpContentOfNormal;
                break;
        }
        viewCall( v -> {
            if( getContext() == null ) return;
            Resources res = getContext().getResources();
            if( titleRes != 0 ) {
                v.onCallTitle( res.getString( titleRes ), mAnswerTodayExp );
            }
            if( contentRes != 0 ) {
                boolean isFastReview = mAnswerType == Constant.AnswerType.FAST_REVIEW;
                String xp = "<font color='" + ( isFastReview ? "#FFEC89" : "#FFB923" ) + "'>+%sXP</font>";
                String content = res.getString( contentRes );
                if( isFastReview ) {
                    v.onCallContentOfFastReview( xp, content, mAnswerExp );
                }else {
                    v.onCallContent( content + " " + xp, mAnswerExp );
                }
            }
        } );
    }

    public void buriedPointContinueButton() {
        if( mAnswerBaseEntity == null ) mAnswerBaseEntity = new AnswerBaseEntity();
        BuriedPointEvent.get().onGainExperiencePageOfContinueButton(
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

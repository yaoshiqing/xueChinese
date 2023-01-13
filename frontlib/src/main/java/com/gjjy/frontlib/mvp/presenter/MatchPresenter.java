package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.ModelType;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.MatchView;

/**
 * P层 - 匹配题
 */
public class MatchPresenter extends MvpPresenter<MatchView> implements IAnswer {
    @Model(ModelType.NEW_MODEL)
    private AnswerModel mAnswerModel;
    private PagerEntity mPagerData;

    private boolean isShowCheck;

    public MatchPresenter(@NonNull MatchView view) { super(view); }

//    @Override
//    public void onTriggerHiddenChanged(boolean hidden) {
//        super.onTriggerHiddenChanged(hidden);
//        mAnswerModel.onTriggerHiddenChanged();
//    }

    public void onPagerHiddenStatus(boolean hidden) {
        mAnswerModel.onTriggerHiddenChanged();
        if( hidden ) return;
        mAnswerModel.setShowToolbar( this, newBundle(), true );
    }

    @Override
    public void onLifeStart() {
        super.onLifeStart();
        viewCall(v -> {
            //        mAnswerModel.onLifeStart( mPagerData );
            //标题
            v.onCallTitle( mAnswerModel.getTitle( mPagerData ) );
            //选项布局
            v.onCallOptionsLayout(
                    mAnswerModel.getOptLayout( mPagerData ),
                    mAnswerModel.getOpts( mPagerData )
            );
            //是否显示检查按钮
            isShowCheck = !mAnswerModel.isFastReviewType() && !mAnswerModel.isWrongQuestionSetType();
            v.onCheckVisibility( isShowCheck ? View.VISIBLE : View.GONE );
        });
    }

    public void doArguments(Bundle args) {
        if( args == null ) return;
        mPagerData = mAnswerModel.onCallArguments( args );
    }

    public void refreshSnail() {
        viewCall( v -> v.onCallSpeed( mAnswerModel.getSpeed() ) );
    }

    public void setCheckResult(boolean isFull) {
        MatchView v = getView();
        if( v == null ) return;
        if( isFull ) {
            //埋点
            mAnswerModel.buriedPointCheckBtn( getContext(), true, mPagerData );
            if( isShowCheck ) {
                v.onEnableCheckBtn();
            }else {
                nextItemDelay( true );
            }
        }
    }

    @Override
    public void check() {
        MatchView v = getView();
        if( v == null ) return;
        //检查日志
        mAnswerModel.checkLog( getContext(), mPagerData );
        mAnswerModel.removeWrongQuestion( (AnswerActivity) v.getActivity(), mPagerData.getId(), true );
        nextItem( true );
    }

    @Override
    public void nextItem(boolean isCorrect) {
        setArgumentsToActivity( mAnswerModel.getNextArgs() );
        mAnswerModel.buriedPointContinueButton( getContext(), isCorrect, mPagerData );
        AnswerActivity activity = ((AnswerActivity) getActivity());
        if( activity == null ) return;
        //更新进度条
        activity.updatedPageSelected();
    }

    @Override
    public void nextItemDelay(boolean isCorrect) {
        post(() -> nextItem( isCorrect ), 1200);
    }
}

package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.ModelType;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.adapter.LabelDragAdapter;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.LabelTranslateView;

/**
 * P层 - 标签题-翻译
 */
public class LabelTranslatePresenter extends MvpPresenter<LabelTranslateView> implements IAnswer {
    @Model(ModelType.NEW_MODEL)
    private AnswerModel mAnswerModel;
    private PagerEntity mPagerData;
//    private int mSelectPosition = -1;

    private DialogOption mCheckDialog;
    private boolean isShowCheck;

    public LabelTranslatePresenter(@NonNull LabelTranslateView view) { super(view); }

//    @Override
//    public void onTriggerHiddenChanged(boolean hidden) {
//        super.onTriggerHiddenChanged(hidden);
//        mAnswerModel.onTriggerHiddenChanged();
//        //因为没有选项，所以默认为0
//        mAnswerModel.setSelectPosition( 1 );
//    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        viewCall(v -> v.dispatchTouchEvent( ev ));
    }

    public void onPagerHiddenStatus(boolean hidden) {
        mAnswerModel.onTriggerHiddenChanged();
        //因为没有选项，所以默认为0
        mAnswerModel.setSelectPosition( 1 );

        if( hidden ) {
            if( mCheckDialog != null && mCheckDialog.isShowing() ) mCheckDialog.dismiss();
            return;
        }
        mAnswerModel.setShowToolbar( this, newBundle(), true );
    }

    @Override
    public void onLifeStart() {
        super.onLifeStart();
        viewCall(v -> {
            //        mAnswerModel.onLifeStart( mPagerData );
            //标题
            v.onCallTitle( mAnswerModel.getTitle( mPagerData ) );
            //问题
            v.onCallQuestion( mAnswerModel.getQuestion( mPagerData ) );
            //选项布局
            refreshOptLayout();
            //是否显示检查按钮
            isShowCheck = !mAnswerModel.isFastReviewType() && !mAnswerModel.isWrongQuestionSetType();
            v.onCheckVisibility( isShowCheck ? View.VISIBLE : View.GONE );
        });
    }

    private boolean isRefreshOptLayout = false;
    public void refreshOptLayout() {
        if( mAnswerModel == null || isRefreshOptLayout ) return;
        if( mAnswerModel.isWrongQuestionSetType() ) isRefreshOptLayout = true;
        viewCall( v -> v.onCallOptionsLayout(
                mAnswerModel.getOptLayout( mPagerData ),
                mAnswerModel.getOpts( mPagerData )
        ));
    }

    public void doArguments(Bundle args) {
        if( args == null ) return;
        mPagerData = mAnswerModel.onCallArguments( args );
    }

    public boolean isShowCheck() { return isShowCheck; }

    public String getAnswer() { return mPagerData.getAnswerId(); }

    @Override
    public void check() {
        LabelTranslateView v = getView();
        if( v == null ) return;
        AnswerActivity activity = ((AnswerActivity)v.getActivity());
        if( activity == null ) return;

        StringBuilder sb = new StringBuilder();
        for( LabelDragAdapter.ItemData data : v.onCallDataList() ) {
            sb.append( data.getDataString() );
        }
        String explain = mAnswerModel.getExplain( mPagerData );
        //检查日志
        mAnswerModel.checkLog( getContext(), mPagerData );
        boolean isCorrect = sb.toString().equals( getAnswer() );

        //埋点
        mAnswerModel.buriedPointCheckBtn( getContext(), isCorrect, mPagerData );

        if( isCorrect ) {
            //移除错题
            mAnswerModel.removeWrongQuestion( activity, mPagerData.getId(), false );

            if( isShowCheck ) {
                mCheckDialog = activity.showCorrectDialog( explain, view -> nextItem( true ) );
            }else {
                nextItemDelay( true );
            }
        }else {
            //记录错题
            mAnswerModel.addWrongQuestion( activity, mPagerData.getId(), false );
//            if( mAnswerModel.getAnswerType() == Constant.AnswerType.TEST ) {
//                activity.onCallAddWrongQuestionSet( mPagerData.getId(), true );
//            }else if( mAnswerModel.getAnswerType() == Constant.AnswerType.NORMAL ) {
//                activity.onCallAddWrongQuestionSet( mPagerData.getId(), false );
//            }

            if( isShowCheck ) {
                mCheckDialog = activity.showErrorDialog( explain, view -> nextItem( false ) );
            }
        }
        //更新进度条
        activity.updatedPageSelected();
        //播放解析音频
        mAnswerModel.playParseUrl( getHandler(), mPagerData );
    }

    @Override
    public void nextItem(boolean isCorrect) {
        setArgumentsToActivity( mAnswerModel.getNextArgs() );
        mAnswerModel.buriedPointContinueButton( getContext(), isCorrect, mPagerData );
        mAnswerModel.cancelParseUrl();
    }

    @Override
    public void nextItemDelay(boolean isCorrect) {
        post(() -> nextItem( isCorrect ), 1200);
    }
}
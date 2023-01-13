package com.gjjy.frontlib.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.AnswerNodeView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

public class AnswerNodePresenter extends MvpPresenter<AnswerNodeView> {
    @Model
    private UserModel mUserModel;
    @Model
    private AnswerModel mAnswerModel;
    private int mRatingNum = 1;
    private int mRatingMax = 5;
    private int mSectionId;
    private String mSectionName = "";
    private boolean mAnswerStatus;

    public AnswerNodePresenter(@NonNull AnswerNodeView view) {
        super(view);
    }

    public void onVisibleChanged(boolean isVisible) {
        if( !isVisible ) return;
        mAnswerModel.setShowToolbar( this, newBundle(), false );

        //更新星星状态。因为当前进度的星星有动画从0开始，所以-1
        viewCall( v -> {
            v.onCallRating( mRatingNum - 1, mRatingMax, mAnswerStatus );
            //保存进度
            saveProgress();
        });
    }

    public void doArguments(Bundle args) {
        if( args == null ) return;
        LogUtil.e("AnswerNode -> Arguments:" + args);
        //评级
        mRatingNum = args.getInt( Constant.RATING_NUM, mRatingNum );
        //最大评级
        mRatingMax = args.getInt( Constant.MAX_RATING, mRatingMax );
        //小节Id
        mSectionId = args.getInt( Constant.ID_FOR_INT );
        //小节名称
        mSectionName = args.getString( Constant.NAME );
        //是否完成答题
        mAnswerStatus = args.getBoolean( Constant.ANSWER_RESULT );
    }

    public int getRatingMax() { return mRatingMax; }

    public int getRatingNum() { return mRatingNum; }

    public boolean isAnswerStatus() { return mAnswerStatus; }

    public void nextItem() {
        //游客模式弹出登录提示
        if( !mUserModel.isLoginResult() && mRatingNum < 5 ) {
            viewCall(AnswerNodeView::onStartNeedLoginActivity);
        }else {
            nextItemOfBundle();
        }

        BuriedPointEvent.get().onSectionCompletionPageOfContinueButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mSectionId,
                mSectionName
        );
    }

    @Nullable
    public Spanned getProgressText() {
        Context context = getContext();
        if( context == null ) return null;
        return Html.fromHtml(
                String.format(context.getResources().getString( R.string.stringNode ) +
                                "<font color='#F5A422'>%s</font>/%s",
                        mRatingNum,
                        mRatingMax
                )
        );
    }

    public void nextItemOfBundle() {
        Bundle args = new Bundle();
        args.putBoolean( Constant.NEXT_POSITION, true );
        setArgumentsToActivity( args );
    }

    public void saveProgress() {
//        //1.1.1
//        Bundle args = new Bundle();
//        args.putBoolean( Constant.SAVE_PROGRESS, true );
//        setArgumentsToActivity( args );
    }
}

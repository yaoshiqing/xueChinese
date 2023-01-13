package com.gjjy.frontlib.mvp.presenter;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.AnswerEndView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class AnswerEndPresenter extends MvpPresenter<AnswerEndView> {
    @Model
    private UserModel mUserModel;
    @Model
    private AnswerModel mAnswerModel;
    @Constant.AnswerType
    private int mAnswerType;
    private boolean isAnswerResult;
    private boolean isShowButton;
    private AnswerBaseEntity mAnswerBaseEntity;
    private int mOldScore = -1;
    private int mNewScore = -1;
    private String mTestOfIconResName;

    public AnswerEndPresenter(@NonNull AnswerEndView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        viewCall( v -> v.onCallIsShowCheckButton( isShowButton ) );
    }

    public void onVisibleChanged(boolean isVisible) {
        viewCall( v -> v.onCallIsShowCheckButton( isShowButton ) );
        if( isVisible ) {
            mAnswerModel.setShowToolbar( this, newBundle(), false );
            initUI();
            //播放音效
            playSound();
            //播放动画
            viewCall( v -> v.onCallIconType( getAnswerType() ) );
        }
    }

    private void playSound() {
        BaseActivity activity = ((BaseActivity)getActivity());
        if( activity == null ) return;
        switch ( mAnswerType ) {
            case Constant.AnswerType.TEST:
            case Constant.AnswerType.SKIP_TEST:
            case Constant.AnswerType.NORMAL:
                if( !isAnswerResult ) break;
                //完成小测验鼓励页音效
                activity.setCallResult( Constant.SoundType.SOUND_ANSWER_END_COMPLETE );
                break;
        }
    }
//    @Override
//    public void onTriggerHiddenChanged(boolean hidden) {
//        super.onTriggerHiddenChanged(hidden);
//        if( !hidden ) {
//            BaseActivity activity = ((BaseActivity)getActivity());
//            //完成小测验鼓励页音效
//            if( activity != null ) {
//                activity.setCallResult( Constant.SoundType.SOUND_ANSWER_END_COMPLETE) );
//            }
//            //播放动画
//            AnswerEndView v = getView();
//            if( v != null ) v.onCallIconType( getAnswerType() );
//        }
////        Bundle args = new Bundle();
////        args.putInt( Constant.TOOLBAR_VISIBILITY, hidden ? View.VISIBLE : View.GONE );
////        args.putLong( Constant.DELAY_MILLIS, 1 );
////        setArgumentsToActivity( args );
//    }

    public void doArguments(@Nullable Bundle args) {
        if( args == null ) return;
        mAnswerType = args.getInt( Constant.ANSWER_TYPE, Constant.AnswerType.NORMAL );
        isAnswerResult = args.getBoolean( Constant.ANSWER_RESULT, true );
        isShowButton = args.getBoolean( Constant.IS_SHOW_BUTTON, true );
        mAnswerBaseEntity = args.getParcelable( Constant.ANSWER_BASE_ENTITY );
    }

    public void setOldScore(int oldScore) { mOldScore = oldScore; }

    public void setNewScore(int newScore) { mNewScore = newScore; }

    public int getAnswerType() { return mAnswerType; }

    public boolean isAnswerResult() { return isAnswerResult; }

    public void nextItem() {
        if( isAnswerResult ) {
            nextItemOfBundle();
        }else {
            finish();
        }

//        //游客模式弹出登录提示
//        if( !mUserModel.isLoginResult() ) {
//            viewCall( AnswerEndView::onStartNeedLoginActivity );
//        }else {
//            nextItemOfBundle();
//        }
    }

    public void nextItemOfBundle() {
        Bundle args = new Bundle();
        args.putBoolean( Constant.NEXT_POSITION, true );
        setArgumentsToActivity( args );
    }

    public void initUI() {
        int answerType = getAnswerType();
        int iconTitleRes;
        int titleRes;
        int contentRes;
        int testOfStatus = getTestOfStatus();
        switch ( answerType ) {
            case Constant.AnswerType.TEST:
                switch( testOfStatus ) {
                    case 1:     //考试通过
                        iconTitleRes = R.string.stringTestPassIconTitle;
                        titleRes = R.string.stringTestPassTitle;
                        contentRes = R.string.stringTestPassContent;
                        mTestOfIconResName = "ic_answer_end_test_of_pass_icon_";
                        break;
                    case 2:     //挑战成功
                        iconTitleRes = R.string.stringTestSuccessIconTitle;
                        titleRes = R.string.stringTestSuccessTitle;
                        contentRes = R.string.stringTestSuccessContent;
                        mTestOfIconResName = "ic_answer_end_test_of_success_icon_";
                        break;
                    case 3:     //挑战失败
                        iconTitleRes = R.string.stringTestFailureIconTitle;
                        titleRes = R.string.stringTestFailureTitle;
                        contentRes = R.string.stringTestFailureContent;
                        mTestOfIconResName = "ic_answer_end_test_of_failure_icon_";
                        break;
                    case 4:     //成绩持平
                        iconTitleRes = R.string.stringTestFlatIconTitle;
                        titleRes = R.string.stringTestFlatTitle;
                        contentRes = R.string.stringTestFlatContent;
                        mTestOfIconResName = "ic_answer_end_test_of_flat_icon_";
                        break;
                    case 5:     //之前满分时刷新战绩
                        iconTitleRes = R.string.stringTestBeforeFullMarksIconTitle;
                        titleRes = R.string.stringTestBeforeFullMarksTitle;
                        contentRes = R.string.stringTestBeforeFullMarksContent;
                        mTestOfIconResName = "ic_answer_end_test_of_before_full_marks_icon_";
                        break;
                    default:    //考试不通过
                        iconTitleRes = R.string.stringTestNotPassIconTitle;
                        titleRes = R.string.stringTestNotPassTitle;
                        contentRes = R.string.stringTestNotPassContent;
                        mTestOfIconResName = "ic_answer_end_test_of_not_pass_icon_";
                        break;
                }
                break;
            case Constant.AnswerType.SKIP_TEST:
                iconTitleRes = 0;
                titleRes = isAnswerResult ? R.string.stringEndTitle : 0;
                contentRes = isAnswerResult ?
                        R.string.stringSkipTestEnd :
                        R.string.stringTestEndFailure;
                break;
            case Constant.AnswerType.FAST_REVIEW:
                iconTitleRes = 0;
                titleRes = R.string.stringReViewEndTitle;
                contentRes = R.string.stringFastReviewEnd;
                break;
            case Constant.AnswerType.INTRODUCE:
                iconTitleRes = 0;
                titleRes = R.string.stringEndTitle;
                contentRes = R.string.stringIntroduceEnd;
                break;
            default:
                iconTitleRes = 0;
                titleRes = R.string.stringEndTitle;
                contentRes = R.string.stringAnswerEnd;
                break;
        }

        viewCall(v -> {
            Resources res = v.getResources();
            if( res == null ) return;
            if( iconTitleRes != 0 ) v.onCallIconTitle( res.getString( iconTitleRes ) );
            if( titleRes != 0 ) {
                String title = res.getString( titleRes );
                if( answerType == Constant.AnswerType.TEST ) {
                    title = String.format( title, mNewScore + "%" );
                }
                v.onCallTitle( title );
            }
            if( contentRes != 0 ) {
                String content = res.getString( contentRes );
                if( answerType == Constant.AnswerType.TEST && testOfStatus != 1 ) {
                    content = String.format( content, mOldScore + "%" );
                }
                v.onCallContent( content );
            }
        });
    }

    public String getTestOfIconResName() { return mTestOfIconResName; }

    /**
     考试状态
     @return    -1：非成绩页面、0:考试不通过、1：考试通过、2：挑战成功、
                3：挑战失败、4：成绩持平、5：之前满分时刷新战绩
     */
    @IntRange( from = -1, to = 5)
    public int getTestOfStatus() {
        if( mOldScore == -1 || mNewScore == -1 ) return -1;
        //考试未通过
        if( mNewScore < 60 ) return 0;
        //考试通过
        if( mOldScore == 0 ) return 1;
        //当前满分刷新 or 挑战成功
        if( mNewScore == 100 || mNewScore > mOldScore ) return 2;
        //成绩持平
        if( mOldScore == mNewScore ) return 4;
        //之前满分刷新
        if( mOldScore == 100 ) return 5;
        //挑战失败
        return 3;
    }

    public void startAnswerActivityOfTest() {
        //首次通过
        if( getTestOfStatus() == 1 ) {
            if( !mUserModel.isVip() ) {
                com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( getActivity() );
            }
            buriedPointPassTestPageOfRefreshRecordButton();
            return;
        }else {
            int result = getBuriedPointTestOfStatus();
            if( result != -3 ) {
                BuriedPointEvent.get().onChallengeResultPageOfTryAgainButton(
                        getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result
                );
            }
        }
        mAnswerBaseEntity.setRecordRecord( true );
        StartUtil.startAnswerActivityOfTest( mAnswerBaseEntity );
    }

    public void buriedPointPassTestPageOfLaterButton() {
        BuriedPointEvent.get().onFailTestPageOfFailTestPage(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() )
        );

        if( getTestOfStatus() != 1 ) return;
        BuriedPointEvent.get().onPassTestPageOfLaterButton(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() )
        );
    }

    public void buriedPointPassTestPageOfRefreshRecordButton() {
        int result = getBuriedPointTestOfStatus();
        if( result == -3 ) return;
        BuriedPointEvent.get().onPassTestPageOfRefreshRecordButton(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result
        );
    }

    private int getBuriedPointTestOfStatus() {
        switch( getTestOfStatus() ) {
            case -1:            //非成绩页面
                return -3;
            case 1: case 2:     //成功
                return 0;
            case 4:             //持平
                return -2;
        }
        return -1;              //失败
    }
}

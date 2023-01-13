package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.ModelType;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.TranslateView;

/**
 * P层 - 翻译题
 */
public class TranslatePresenter extends MvpPresenter<TranslateView> implements IAnswer {
    @Model(ModelType.NEW_MODEL)
    private AnswerModel mAnswerModel;
    private PagerEntity mPagerData;
//    private int mSelectPosition = -1;

    private DialogOption mCheckDialog;
    private boolean isShowCheck;


    public TranslatePresenter(@NonNull TranslateView view) { super(view); }

//    @Override
//    public void onTriggerHiddenChanged(boolean hidden) {
//        super.onTriggerHiddenChanged(hidden);
//        mAnswerModel.onTriggerHiddenChanged();
//
//        TranslateView v = getView();
//        if( v != null  && !hidden ) post(v::onPlay, 500);
//    }

    public void onPagerHiddenStatus(boolean hidden) {
        mAnswerModel.onTriggerHiddenChanged();
        if( hidden ) {
            if( mCheckDialog != null && mCheckDialog.isShowing() ) mCheckDialog.dismiss();
            return;
        }
        mAnswerModel.setShowToolbar( this, newBundle(), true );
        /* 语音链接 */
        viewCall( v -> v.onCallAudioUrl( mAnswerModel.getAudio( mPagerData ) ) );
        viewCall( TranslateView::onPlay, 500 );
    }

    @Override
    public void onLifeStart() {
        super.onLifeStart();
        viewCall( v -> {
            v.onCallTitle( mAnswerModel.getTitle( mPagerData ) );
            //问题
            v.onCallQuestion( mAnswerModel.getQuestion( mPagerData ) );
            //选项布局
            v.onCallOptionsLayout(
                    mAnswerModel.getOptLayout( mPagerData ),
                    mAnswerModel.getOpts( mPagerData )
            );
            //是否显示检查按钮
            isShowCheck = !mAnswerModel.isFastReviewType() && !mAnswerModel.isWrongQuestionSetType();
            v.onCheckVisibility( isShowCheck ? View.VISIBLE : View.GONE );
//        //标题
//        v.onCallTitle( toTitle( mPagerData.getTitle() ) );
//        //问题
//        v.onCallQuestion( mPagerData.getQuestion() );
//        /* 语音链接 */
//        String audioText = mPagerData.getAudioUrl();
//        v.onCallAudioUrl( audioText != null ? audioText : mPagerData.getQuestion() );
//        //选项布局
//        v.onCallOptionsLayout( mPagerData.getOptLayout(), mPagerData.getOpts() );
        } );
    }

    public boolean isAllowInitLayout() {
        return mAnswerModel != null && !mAnswerModel.isWrongQuestionSetType();
    }

    public void doArguments(Bundle args) {
        if( args == null ) return;
        mPagerData = mAnswerModel.onCallArguments( args );
//        if( args == null ) return;
//        PagerEntity pager = args.getParcelable( Constant.PAGER_DATA );
//        if( pager != null ) mPagerData = pager;
    }

    public void refreshSnail() {
        viewCall( v -> v.onCallSpeed( mAnswerModel.getSpeed() ) );
    }

    public void setSelectPosition(int position) {
        mAnswerModel.setSelectPosition( position );
//        mSelectPosition = position;
    }

    public boolean isShowCheck() { return isShowCheck; }

    public int getAnswerType() { return mAnswerModel.getAnswerType(); }

    @Override
    public void check() {
        TranslateView v = getView();
        if( v == null ) return;
        AnswerActivity activity = ((AnswerActivity)v.getActivity());
        if( activity == null ) return;
        int answerId = mPagerData.getAnswerIdOfInt();
        String explain = mAnswerModel.getExplain( mPagerData );
        //更新进度条
        activity.updatedPageSelected();
        //检查日志
        mAnswerModel.checkLog( getContext(), mPagerData );
        boolean isCorrect = mAnswerModel.check( answerId );

        //埋点
        mAnswerModel.buriedPointCheckBtn( getContext(), isCorrect, mPagerData );

        if( isCorrect ) {
            //移除错题
            mAnswerModel.removeWrongQuestion( activity, mPagerData.getId(), false );

            v.onCallOptionsCorrect();
            if( isShowCheck ) {
//                OptionsEntity data = mPagerData.getOpts()[ answerId - 1 ];
//                String s = data.getPinyinString() + "\n" + data.getDataString();
                mCheckDialog = activity.showCorrectDialog(explain, view -> nextItem( true ));
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

            v.onCallOptionsError();
            if( isShowCheck ) {
                mCheckDialog = activity.showErrorDialog(explain, view -> nextItem( false ));
            }
        }
        //更新进度条
        activity.updatedPageSelected();
        //播放解析音频
        mAnswerModel.playParseUrl( getHandler(), mPagerData );
    }
//    private String getAnswerText() {
//        if( mSelectPosition == -1 ) return null;
//        String key = getTitleKey( mPagerData.getTitle() );
//        if( key != null ) return key;
//        OptionsEntity opt = mPagerData.getOpts()[ mPagerData.getAnswerId() - 1 ];
//        StringBuilder sb = new StringBuilder();
//        for( String s : opt.getPinyin() ) sb.append( s ).append(" ");
//        sb.append("\n");
//        for( String s : opt.getData() ) sb.append( s );
//        sb.append("\n");
//        if( mPagerData.getQuestion() != null ) sb.append( mPagerData.getQuestion() );
//        return sb.length() == 2 ? null : sb.toString();
//    }
//    private String getTitleKey(String s) {
//        int start = s.indexOf("<");
//        int end = s.indexOf(">");
//        return start != -1 && end != -1 ? s.substring( start + 1, end ) : null;
//    }

//    private String toTitle(String s) {
//        return s.replaceAll("<", "“").replaceAll(">", "”");
//    }

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

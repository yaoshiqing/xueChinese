package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.mvp.model.SetUpModel;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.mvp.model.AnswerModel;
import com.gjjy.frontlib.mvp.view.VoiceView;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.gjjy.speechsdk.OnResultListener;
import com.gjjy.speechsdk.evaluator.SpeechEvaluator;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;
import com.gjjy.speechsdk.evaluator.parser.result.Result;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.ModelType;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * P层 - 语音题
 */
public class VoicePresenter extends MvpPresenter<VoiceView> implements IAnswer {
    @Model(ModelType.NEW_MODEL)
    private AnswerModel mAnswerModel;
    @Model
    private SetUpModel mSetUpModel;
    private String mText;
    private PagerEntity mPagerData;

//    private final SpeechSynthesizer mTts;
    private final SpeechEvaluator mIse;
    private final OnResultListener mOnResultListener;

    private final List<Syll> mStatusList = new ArrayList<>();

    private boolean mHidden = true;

    public VoicePresenter(@NonNull VoiceView view) {
        super(view);
//        mTts = SpeechSynthesizer.get();
        mIse = SpeechEvaluator.get();
        //语音测评回调结果
        mOnResultListener = new OnResultListener() {
            @Override
            public void onResult(Result result, boolean isLast) {
                LogUtil.e("Voice onResult Hidden:" + mHidden );
                //跳过非当前页面
                if( mHidden ) return;
                viewCall( VoiceView::onEndRecording );
                doVoiceResult( result );
            }
            @Override
            public void onError(int errCode, String msg) {
                LogUtil.e("Voice onResult Hidden:" + mHidden );
                //跳过非当前页面
                if( mHidden ) return;
                viewCall( VoiceView::onEndRecording );
            }
        };
        mIse.addOnResultListener( mOnResultListener );
    }

//    public SpeechSynthesizer getTts() { return mTts; }

    public String getSaveSpeechFilePath() { return mIse.getSaveSpeechFilePath(); }

    public int getAnswerType() { return mAnswerModel.getAnswerType(); }

    public void refreshSnail() {
        viewCall( v -> v.onCallSpeed( mAnswerModel.getSpeed() ) );
    }

    private void doVoiceResult(Result result) {
        if( result == null ) return;
        mStatusList.clear();
        mStatusList.addAll( result.toSyll( mText ) );
        //回调测评结果
        viewCall( v -> {
            //改变测评状态
            v.onCallStatus( mStatusList );
            v.onResult( getStatusResult() );
        });
    }

    public void onPagerHiddenStatus(boolean hidden) {
        mHidden = hidden;
        mAnswerModel.onTriggerHiddenChanged();
        if( hidden ) return;
        mAnswerModel.setShowToolbar( this, newBundle(), true );
        /* 语音链接 */
        viewCall( v -> {
            String audioUrl = mAnswerModel.getAudio( mPagerData );
            v.onCallAudioUrl( !TextUtils.isEmpty( audioUrl ) ? audioUrl : mText );
        } );
        viewCall( VoiceView::onPlay, 500 );
    }

    @Override
    public void onLifeStart() {
        super.onLifeStart();
        viewCall(v -> {
            //问题
            v.onCallTitle( mAnswerModel.getVoiceQuestion( mPagerData ) );
            //语音链接
            OptionsEntity opt = mAnswerModel.getVoiceOptEntity( mPagerData );
            if( opt != null ) {
//                int len = 0;
//                int count = 0;
//                for (int i = 0; i < opt.getData().length; i++) {
//                    String s = opt.getData()[ i ];
//                    if( TextUtils.isEmpty( s ) ) continue;
//                    len += s.length();
//                    count++;
//                    if( len > 7 ) {
//                        break;
////                    len = 0;
//
//                    }
//                }
//                v.onCallSetLayoutManager( /*opt.getPinyinLength()*/count );
                mText = opt.getDataString();
                v.onCallData( opt.getPinyin(), opt.getData() );
            }
//        //是否显示检查按钮
//        boolean isShowCheck = !mAnswerModel.isFastReviewType() && !mAnswerModel.isWrongQuestionSetType();
//        v.onCheckVisibility( isShowCheck ? View.VISIBLE : View.GONE );
            v.onShowAudioBtn( !mAnswerModel.isNullAudioUrl( mPagerData ) );
        }, 100);
    }

    @Override
    public void onLifePause() {
        super.onLifePause();
        onCancelRecording();
    }

    @Override
    public void onLifeDestroy() {
        mIse.removeOnResultListener( mOnResultListener );
        super.onLifeDestroy();
    }

    public void onStartRecording() {
        if( mText != null ) mIse.start( mText );
        mAnswerModel.buriedPointVoiceRecordButton( getContext(), mPagerData );
    }

    public void onStopRecording() { mIse.stop(); }

    public void onCancelRecording() { mIse.cancel(); }

    public void doArguments(Bundle args) {
        if( args == null ) return;
        mPagerData = mAnswerModel.onCallArguments( args );
    }

    @Override
    public void check() {
        VoiceView v = getView();
        if( v == null ) return;
        boolean result = getStatusResult();
        //埋点
        mAnswerModel.buriedPointCheckBtn( getContext(), result, mPagerData );
        if( result ) {
            mAnswerModel.removeWrongQuestion( (AnswerActivity) v.getActivity(), mPagerData.getId(), true );
        }else {
            mAnswerModel.addWrongQuestion( (AnswerActivity) v.getActivity(), mPagerData.getId(), true );
        }

        //检查日志
        mAnswerModel.checkLog( getContext(), mPagerData );
        nextItem( true );
    }

    @Override
    public void nextItem(boolean isCorrect) {
        AnswerActivity activity = ((AnswerActivity)getActivity());
        if( activity == null ) return;
        post(() -> setArgumentsToActivity( mAnswerModel.getNextArgs() ));
        mAnswerModel.buriedPointContinueButton( getContext(), isCorrect, mPagerData );
        //更新进度条
        activity.updatedPageSelected();
    }

    @Override
    public void nextItemDelay(boolean isCorrect) { }

    private boolean getStatusResult() {
        if( mStatusList.size() == 0 ) {
            return false;
        }else {
            for( Syll syll : mStatusList ) {
                if( syll.getDpMessage() != 0 ) return false;
            }
        }
        return true;
    }
}

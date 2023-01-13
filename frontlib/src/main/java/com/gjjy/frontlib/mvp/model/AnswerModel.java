package com.gjjy.frontlib.mvp.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.mvp.model.SetUpModel;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.ybear.ybmediax.media.MediaXC;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.handler.Handler;
import com.ybear.mvp.model.MvpModel;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.mvp.view.MvpViewable;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.LogUtil;

public class AnswerModel extends MvpModel {
//    private static final String FLAG_START = "<";
//    private static final String FLAG_END = ">";

//    private PagerEntity mPagerData;
    @Model
    private SetUpModel mSetUpModel;
    private int mSelectPosition;
    private int mAnswerType;
    private AnswerBaseEntity mAnswerBaseEntity;

//    private MediaX mMediaX;
    private final MediaXC mMediaXC = MediaXC.get();
    private final int mMediaXCTag = mMediaXC.createTag();
    private DOM.OnResultListener mDOMResultListener;
//    private String mPlayParseUrl;

//    @Override
//    public void onCreateModel() {
//        super.onCreateModel();
//        mDOMResultListener = (i, o) -> {
//            if( !TextUtils.isEmpty( mPlayParseUrl ) && i == Constant.SoundType.SOUND_PLAY_COMPLETION ) {
//                mMediaXC.play( mMediaXCTag, mPlayParseUrl );
//                mPlayParseUrl = null;
//            }
//        };
//        DOM.getInstance().registerResult( mDOMResultListener );
//    }

    @Override
    public void onDestroyModel() {
        super.onDestroyModel();
        DOM.getInstance().unRegisterResult( mDOMResultListener );
        LogUtil.e("AnswerModel -> onDestroyModel");
    }

    public void onTriggerHiddenChanged() {
        mSelectPosition = -1;
//        mPlayParseUrl = null;
    }

//    public void onLifeStart(PagerEntity pager) { mPagerData = pager; }

    public PagerEntity onCallArguments(Bundle args) {
        if( args == null ) return null;
        mAnswerType = args.getInt( Constant.ANSWER_TYPE );
        mAnswerBaseEntity = args.getParcelable( Constant.ANSWER_BASE_ENTITY );
        return args.getParcelable( Constant.PAGER_DATA );
    }

    public void setSelectPosition(int position) { mSelectPosition = position; }
//    public int getSelectPosition() { return mSelectPosition; }

    public void checkLog(Context context, PagerEntity data) {
//        if( !LogUtil.isDebug() ) return;
//        ToastManage.get().showToastForLong(
//                context, "[Debug]" +
//                        "\n题目ID：" + data.getId() +
//                        "\n题目类型ID：" + data.getQuestionTypeId() +
//                        "\n题目类型：" + data.getPagerType() +
//                        "\n音频链接：" + data.getAudioUrl() +
//                        "\n视频链接：" + data.getVideo() +
//                        "\n答案选项：" + data.getAnswerId(),
//                new Build().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM )
//        );
    }
    public boolean check(int answerId) {
        return mSelectPosition != -1 && mSelectPosition == answerId;
    }

    public String getExplain(PagerEntity data) { return data.getExplain(); }

    public Bundle getNextArgs() {
        Bundle args = new Bundle();
        args.putBoolean( Constant.NEXT_POSITION, true );
        return args;
    }

    @Nullable
    public String getTitle(PagerEntity data) { return data != null ? data.getTitle() : ""; }

    @NonNull
    public String getQuestion(PagerEntity data) {
        if( data == null ) return "";
        String question = data.getQuestion();
        return TextUtils.isEmpty( question ) ? "" : question;
    }

    @NonNull
    public String getVoiceQuestion(PagerEntity data) {
        if( data == null ) return "";
        OptionsEntity[] opts = data.getOpts();
        if( opts != null && opts.length > 0 ) {
            String question = opts[ 0 ].getQuestion();
            return TextUtils.isEmpty( question ) ? "" : question;
        }
        return "";
    }

    @NonNull
    public String getAudio(PagerEntity data) {
        if( data == null ) return "";
        String url = data.getAudio();
        String[] keys = data.getKeyword();
        return !TextUtils.isEmpty( url ) ?
                url : ( keys == null || keys.length == 0 ?
                "" : keys[ 0 ] );
    }

    @NonNull
    public String getVideo(PagerEntity data) {
        if( data == null ) return "";
        String video = data.getVideo();
        return TextUtils.isEmpty( video ) ? "" : video;
    }

    @Nullable
    public OptionsEntity[] getOpts(PagerEntity data) {
        if( data == null ) return null;
        return data.getOpts();
    }

    public int getOptLayout(PagerEntity data) {
        if( data == null ) return OptionsLayout.NONE;
        return data.getOptLayout();
    }

    @Nullable
    public OptionsEntity getVoiceOptEntity(PagerEntity data) {
        if( data == null ) return null;
        OptionsEntity[] opts = getOpts( data );
        return opts != null && opts.length > 0 ? opts[ 0 ] : null;
    }

    public boolean isNullAudioUrl(PagerEntity data) {
        return data != null && "--".equals( data.getAudio() );
    }

    public boolean isWrongQuestionSetType() {
        return mAnswerType == Constant.AnswerType.ERROR_MAP;
    }

    public boolean isFastReviewType() {
        return mAnswerType == Constant.AnswerType.FAST_REVIEW;
    }

    public int getAnswerType() { return mAnswerType; }

    public void addWrongQuestion(AnswerActivity activity,
                                 int id,
                                 boolean isSkip,
                                 @NonNull Function<Integer, Boolean> call) {
        Boolean isCall = call.apply( mAnswerType );
        LogUtil.e( "addWrongQuestion -> " +
                "isCall:" + isCall + " | " +
                "mAnswerType:" + mAnswerType
        );
        if( isCall == null || !isCall ) return;
        activity.onCallWrongAnswer( isSkip );
        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:
                activity.onCallAddWrongQuestionSet( id, false );
                break;
            case Constant.AnswerType.TEST:
                activity.onCallAddWrongQuestionSet( id, true );
                break;
            case Constant.AnswerType.SKIP_TEST:
                activity.onCallWrongAnswerOfModuleTest();
                break;

        }
    }

    public void addWrongQuestion(AnswerActivity activity, int id, boolean isSkip) {
        addWrongQuestion(activity, id, isSkip, input -> true);
    }

    public void removeWrongQuestion(AnswerActivity activity,
                                    int id,
                                    boolean isSkip,
                                    @NonNull Function<Integer, Boolean> call) {
        Boolean isCall = call.apply( mAnswerType );
        LogUtil.e( "removeWrongQuestion -> " +
                "isCall:" + isCall + " | " +
                "mAnswerType:" + mAnswerType
        );
        if( isCall == null || !isCall ) return;
        activity.onCallCorrectAnswer( isSkip );
        switch ( mAnswerType ) {
            case Constant.AnswerType.TEST:
                activity.onCallRemoveWrongQuestionSet( id, true );
                break;
            case Constant.AnswerType.NORMAL:
                activity.onCallRemoveWrongQuestionSet( id, false );
                break;
        }
    }

    public void removeWrongQuestion(AnswerActivity activity, int id, boolean isSkip) {
        removeWrongQuestion(activity, id, isSkip, input -> true);
    }

    public void buriedPointCheckBtn(Context context, boolean isCorrect, PagerEntity data) {
        if( mAnswerBaseEntity == null ) return;
        if( mAnswerType == Constant.AnswerType.TEST ) {
            BuriedPointEvent.get().onTestOutStudyPageOfCheckButton(
                    context,
                    isCorrect,
                    data.getQuestionTypeId(),
                    String.valueOf( data.getQuestionTypeId() ),
                    data.getId(),
                    mAnswerBaseEntity.getCategoryId(),
                    mAnswerBaseEntity.getCategoryName(),
                    mAnswerBaseEntity.getLevelId(),
                    mAnswerBaseEntity.getLevelName(),
                    mAnswerBaseEntity.getUnitId(),
                    mAnswerBaseEntity.getUnitName()
            );
            return;
        }
        BuriedPointEvent.get().onStudyPageOfCheckButton(
                context,
                isCorrect,
                data.getQuestionTypeId(),
                String.valueOf( data.getQuestionTypeId() ),
                data.getId(),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }

    public void buriedPointContinueButton(Context context, boolean isCorrect, PagerEntity data) {
        if( mAnswerBaseEntity == null ) return;
        if( mAnswerType == Constant.AnswerType.TEST ) {
            BuriedPointEvent.get().onTestOutStudyPageOfContinueButton(
                    context,
                    isCorrect,
                    data.getQuestionTypeId(),
                    String.valueOf( data.getQuestionTypeId() ),
                    data.getId(),
                    mAnswerBaseEntity.getCategoryId(),
                    mAnswerBaseEntity.getCategoryName(),
                    mAnswerBaseEntity.getLevelId(),
                    mAnswerBaseEntity.getLevelName()
            );
            return;
        }
        BuriedPointEvent.get().onStudyPageOfContinueButton(
                context,
                data.getQuestionTypeId(),
                String.valueOf( data.getQuestionTypeId() ),
                data.getId(),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName(),
                mAnswerBaseEntity.getUnitId(),
                mAnswerBaseEntity.getUnitName()
        );
    }

    public void buriedPointVoiceRecordButton(Context context, PagerEntity data) {
        BuriedPointEvent.get().onStudyPageSpeakingOfRecordButton(
                context,
                data.getId(),
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName()
        );
    }

//    private static final AnswerMediaXManage mAMXM = new AnswerMediaXManage();
    public void playParseUrl(Handler h, PagerEntity data) {
        h.post( () -> {
            String url = data.getAudioUrl();
            LogUtil.e("AnswerModel -> playParseUrl -> " + url);
            if( TextUtils.isEmpty( url ) ) return;
            mMediaXC.play( mMediaXCTag, url );
//            mMediaXC.add( result -> {
//                if( TextUtils.isEmpty( mPlayParseUrl ) ) return;
////                mMediaXC.setSpeed( getSpeed() );
//                mMediaXC.play( mMediaXCTag, mPlayParseUrl );
//            }, mPlayParseUrl = url );
        }, 1200);
    }

    public void cancelParseUrl() {
//        mMediaXC.pause( mMediaXCTag );
//        new Thread( () -> {
//            try { Thread.sleep( 200 ); } catch(InterruptedException ignored) { }
//            mMediaX.stop();
//            mAMXM.recycle( mMediaX );
//        } ).start();

    }

    public float getSpeed() {
        float speed = mSetUpModel.isSkipSnail() ? 1.0F : 0.75F;
        LogUtil.e( "AnswerModel -> getSpeed:" + speed );
        return speed;
    }

    public void setShowToolbar(@NonNull MvpPresenter<? extends MvpViewable> p, Bundle args, boolean isShow) {
        //显示/隐藏toolbar
        args.putInt( Constant.TOOLBAR_VISIBILITY, isShow ? View.VISIBLE : View.GONE );
        p.setArgumentsToActivity( args );
    }
}
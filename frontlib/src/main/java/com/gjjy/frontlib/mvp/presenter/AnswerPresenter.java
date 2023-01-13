package com.gjjy.frontlib.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gjjy.basiclib.api.entity.AnswerExpEntity;
import com.gjjy.basiclib.api.entity.AnswerQuestionContentEntity;
import com.gjjy.basiclib.api.entity.AnswerQuestionEntity;
import com.gjjy.basiclib.api.entity.AnswerQuestionOptionEntity;
import com.gjjy.basiclib.api.entity.WrongQuestionSetQuestionEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.ReqAnswerModel;
import com.gjjy.basiclib.mvp.model.SetUpModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.annotations.PagerType;
import com.gjjy.frontlib.entity.AnswerEntity;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.mvp.model.AnswerHelperModel;
import com.gjjy.frontlib.mvp.view.AnswerView;
import com.gjjy.frontlib.ui.activity.AnswerActivity;
import com.gjjy.frontlib.ui.fragment.AnswerEndFragment;
import com.gjjy.frontlib.ui.fragment.AnswerNodeFragment;
import com.gjjy.frontlib.ui.fragment.HearingFragment;
import com.gjjy.frontlib.ui.fragment.LabelHearingFragment;
import com.gjjy.frontlib.ui.fragment.LabelTranslateFragment;
import com.gjjy.frontlib.ui.fragment.TranslateFragment;
import com.gjjy.frontlib.ui.fragment.VoiceFragment;
import com.gjjy.speechsdk.evaluator.SpeechEvaluator;
import com.ybear.ybmediax.media.MediaXC;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.mvp.view.fragment.MvpFragment;
import com.ybear.ybcomponent.widget.FragmentViewPager;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.IOUtil;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * P层 - 答题页面-主容器
 */
public class AnswerPresenter extends MvpPresenter<AnswerView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqAnswerModel mReqAnswer;
    @Model
    private SetUpModel mSetUpModel;
    @Model
    private OtherModel mOtherModel;
    @Model
    private AnswerHelperModel mAnswerHelperModel;

    private FragmentViewPager mPager;
    private int mWrongAnswerCount = 0;
    private int mWrongAnswerLastCountOfModuleTest = 3;
    private int mConsecutiveErrorCount = 0;
    private int mTestEncourageCount = 0;
    private int mContinuousAnswerCorrectCont = 0;
//    private int mAnswerErrorPosition = -1;

    private int mId;
    private ArrayList<SectionIds> mIds = new ArrayList<>();
    private int mNodeNum;
    private final int mNodeCount = 5;
    private int nNextIndex;
    private int nNextSize = -1;
    @Constant.AnswerType
    private int mAnswerType;
    private int mAnswerStatus;
    private int mWrongQuestionSetTotalPage = -1;
    private int mWrongQuestionSetCurrentPage = 0;
    private int mReviewLevelId = 0;
    private boolean mIsShowError = false;
    private int mOldScore = -1;
    private int mNewScore = -1;
    private int mCurrentItem = 0;
    private boolean isSaveAward = false;
    private boolean isError = false;
    private AnswerBaseEntity mAnswerBaseEntity;

    private List<PagerEntity> mDataList;
    private final List<String> mKeywordList = new ArrayList<>();
    private final List<Integer> mContinuouslyCorrectList = new ArrayList<>();
    private int mSingleContinuouslyCorrectCount;
    private boolean isNextSaveProgressSuccess;

    public AnswerPresenter(@NonNull AnswerView view) { super(view); }

    public void initIntent(Intent intent) {
        if( intent == null ) {
            callError();
            return;
        }
        //课程类型
        mAnswerType = intent.getIntExtra( Constant.ANSWER_TYPE, -1 );
        if( mAnswerType == -1 ) {
            callError();
            return;
        }

        mAnswerBaseEntity = intent.getParcelableExtra( Constant.ANSWER_BASE_ENTITY );
        if( mAnswerBaseEntity == null ) mAnswerBaseEntity = new AnswerBaseEntity();

        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:        //练习
                mIds = intent.getParcelableArrayListExtra( Constant.SECTION_IDS );
                mId = mAnswerBaseEntity.getUnitId();
                mAnswerStatus = mAnswerBaseEntity.getUnitStatus();
                mNodeNum = intent.getIntExtra( Constant.SECTION_NUM, 0 );
                if( mNodeNum >= mNodeCount ) mNodeNum = 0;
                break;
            case Constant.AnswerType.TEST:        //阶段考试
                mIds = new ArrayList<>();
                mId = mAnswerBaseEntity.getLevelId();
                mAnswerStatus = mAnswerBaseEntity.getLevelStatus();
                mNodeNum = mNodeCount;
//                int id = intent.getIntExtra( Constant.ID_FOR_INT, -1 );
                if( mId != -1 ) mIds.add( new SectionIds( mId ) );
                break;
            case Constant.AnswerType.SKIP_TEST:   //跳级考试
                mId = mAnswerBaseEntity.getUnitId();
                mAnswerStatus = mAnswerBaseEntity.getUnitStatus();
                mNodeNum = mNodeCount;
//                int id = intent.getIntExtra( Constant.ID_FOR_INT, -1 );
                if( mId != -1 ) mIds.add( new SectionIds( mId ) );
                break;
            case Constant.AnswerType.FAST_REVIEW:   //快速复习
                mIds = new ArrayList<>();
                mId = mAnswerBaseEntity.getCategoryId();
                mNodeNum = mNodeCount;
                if( mId != -1 ) mIds.add( new SectionIds( mId ) );
                break;
        }

        if( mId == -1 || mIds == null || mIds.size() == 0 ) {
            if( mAnswerType == Constant.AnswerType.ERROR_MAP ) return;
            callError();
            return;
        }

        LogUtil.e("initIntent -> " +
                "mId:" + mId + " | " +
                "mAnswerType:" + mAnswerType + " | " +
                "mAnswerStatus:" + mAnswerStatus + " | " +
                "mNodeNum:" + mNodeNum
        );
    }

    public void initAnswer() {
        LogUtil.e("isError -> " + isError);
        if( isError ) return;
        loadNextAnswer(mAnswerType, result -> {
            //更新进度
            if( result ) post(this::updatedPageSelected);
        });
    }

    public void increaseWrongAnswerCount(boolean isSkip) {
        if( !isSkip ) {
            //连续错题次数
            mConsecutiveErrorCount++;
            //错题总数
            mWrongAnswerCount++;

            //中断连续答对时加入到列表
            if( mSingleContinuouslyCorrectCount >= 2 ) {
                mContinuouslyCorrectList.add( mSingleContinuouslyCorrectCount );
            }
            mSingleContinuouslyCorrectCount = 0;
        }
        mContinuousAnswerCorrectCont = 0;
    }

    public void increaseCorrectAnswerCount(boolean isSkip) {
        if( !isSkip ) {
            mSingleContinuouslyCorrectCount++;
        }
        //连击
        if( ++mContinuousAnswerCorrectCont >= 3 ) {
            viewCall( v -> v.onCallCombo( mContinuousAnswerCorrectCont ) );
        }
    }

    public void increaseWrongAnswerLastCountOfModuleTest() {
        mWrongAnswerLastCountOfModuleTest--;
    }

    public int getWrongAnswerLastCountOfModuleTest() { return mWrongAnswerLastCountOfModuleTest; }

    public int getSectionCount() { return nNextSize; }

    public boolean isWrongQuestionSetType() {
        return mAnswerType == Constant.AnswerType.ERROR_MAP;
    }

//    public void setAnswerErrorPosition(int position) {
//        mAnswerErrorPosition = position;
//    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mReqAnswer.setUid( mUserModel.getUid(), mUserModel.getToken() );
        mOtherModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
        init();
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        init();
    }

    @Override
    public void onLifeDestroy() {
        isDestroy = true;
        //退出时保存奖励和保存进度
//        endSave(r -> DOM.getInstance().setResult( DOMConstant.NOTIFY_FRONT_LIST ));
        endSave(r -> {});
        //释放资源
        Context context = getContext();
        //释放语音合成资源
        SpeechSynthesizer.get().release();
        /* 语音测评资源 */
        SpeechEvaluator ise = SpeechEvaluator.get();
        //删除回放的音频文件
        if( context != null ) IOUtil.deleteFile( context, ise.getSaveSpeechFilePath() );
        //释放语音测评资源
        ise.release();
        //进度保存结果
        DOM.getInstance().setResult( DOMConstant.ANSWER_NEXT_SAVE_PROGRESS_SUCCESS, isNextSaveProgressSuccess );
        super.onLifeDestroy();
    }

    private boolean isDestroy = false;
    private boolean isEndSave = false;
    private void endSave(@NonNull Consumer<Boolean> call) {
        if( isEndSave ) {
            call.accept( false );
            LogUtil.e( "endSave -> Saved it before." );
            return;
        }

        Consumer<Boolean> callSaveProgress = isSave -> {
            boolean isWrongQuestionSet = mAnswerType == Constant.AnswerType.ERROR_MAP;
            boolean isModuleTest = mAnswerType == Constant.AnswerType.SKIP_TEST;
            //错题和跳级考试没有保存节点，需要退出时保存一下
            if( isWrongQuestionSet || ( isModuleTest && isSave ) ) {
                saveProgress( r -> call.accept( true ) );
            }else {
                call.accept( false );
            }
            LogUtil.e("endSave -> " +
                    "isWrongQuestionSet:" + isWrongQuestionSet + " | " +
                    "isModuleTest:" + isModuleTest + " | " +
                    "isSave:" + isSave
            );
        };

        LogUtil.e("endSave -> " +
                "mNodeNum:" + mNodeNum + " | " +
                "mNodeCount:" + mNodeCount + " | " +
                "nNextIndex:" + nNextIndex + " | " +
                "nNextSize:" + nNextSize
        );

        if( mNodeNum >= mNodeCount && nNextIndex >= nNextSize ) {
            //保存奖励
            saveAward( r -> callSaveProgress.accept( true ) );
        }else {
            //保存节点
            callSaveProgress.accept( false );
        }
        isEndSave = true;
    }

    private void init() {
        AnswerActivity activity = (AnswerActivity) getActivity();
        if ( activity == null ) return;
        //初始化语音合成
        SpeechSynthesizer.get().init( activity ).build();
        //初始化语音测评
        SpeechEvaluator.get()
                .init( activity )
                .setPlev( true )
                .build();
    }

    /**
     * 题目数据
     * @param list      题目数据
     */
    public void dataCall(@Nullable List<AnswerQuestionEntity> list,
                         boolean result, Consumer<Boolean> call) {
        if( list == null || list.size() == 0 ) {
            List<MvpFragment> mFragmentList = new ArrayList<>();
            PagerEntity pager = new PagerEntity();
            pager.setPagerType( PagerType.NOT_PAGER );
            MvpFragment f = mAnswerHelperModel.createMvpFragmentAndData(
                    pager, newBundle(), mAnswerBaseEntity, mAnswerType
            );

            if( f != null ) mFragmentList.add( f );

            viewCall(v -> {
                v.onCallFragment( true, mFragmentList );
                v.onCallNotAnswer( mAnswerType );
                v.onCallShowLoadingDialog( false );
            });
            return;
        }

        nNextSize = list.size();
//        nNextIndex = 0;
        AnswerEntity aeData = new AnswerEntity();
        List<PagerEntity> pageList = new ArrayList<>();
        //清空关键词
        mKeywordList.clear();
        //清空所有页面列表
        mFragmentList.clear();
        LinkedHashSet<String> audioUrlSet = new LinkedHashSet<>();
        for( AnswerQuestionEntity data : list ) {
            if( data == null ) continue;
            String[] answerKey = data.getKeyword();
            //添加关键词
            if( answerKey != null && answerKey.length > 0 ) {
                mKeywordList.addAll( Arrays.asList( answerKey ) );
            }

            AnswerQuestionContentEntity content = data.getContent();
            PagerEntity page = new PagerEntity();
            page.setId( data.getQuestionId() );
            page.setTitle( content.getTitle() );
            page.setKeyword( data.getKeyword() );
            page.setAnswerId( data.getQuestionId() );
            page.setAudio( content.getAudio() );
            page.setAudioUrl( content.getAudioUrl() );
            //添加音频到列表
            audioUrlSet.add( content.getAudio() );
            audioUrlSet.add( page.getAudioUrl() );
            page.setVideo( content.getVideo() );
            page.setQuestion( content.getSentence() );
            page.setQuestionTypeId( data.getQuestionTypeId() );
            page.setExplain( content.getExplain() );
            List<AnswerQuestionOptionEntity> optData = content.getOption();
            OptionsEntity[] optList;
            boolean isLongText = false;
            if( optData != null && optData.size() > 0 ) {
                optList = new OptionsEntity[ optData.size() ];
                for (int i = 0; i < optData.size(); i++) {
                    AnswerQuestionOptionEntity aqOpt = optData.get( i );
                    OptionsEntity opt = new OptionsEntity();
                    opt.setData( aqOpt.getLang() );
                    opt.setPinyin( aqOpt.getPinyin() );
                    opt.setImgUrl( aqOpt.getImage() );
                    opt.setQuestion( aqOpt.getEnglish() );
                    opt.setAudioUrl( aqOpt.getAudio() );
                    //添加音频到列表
                    audioUrlSet.add( aqOpt.getAudio() );
                    if( aqOpt.getLang() != null ) {
                        StringBuilder lang = new StringBuilder();
                        for( String s : aqOpt.getLang() ) lang.append( s );
                        if( !isLongText ) {
                            isLongText =
                                    lang.toString().length() > 4 &&
                                            TextUtils.isEmpty( opt.getImgUrl() );
                        }
                    }
                    opt.setOptType( data.getOptionTypeId() );
                    opt.setTag( aqOpt.getPinyinSort() );
                    optList[ i ] = opt;
                    page.setAnswerId( content.getAnswer() );
                }
            }else {
                optList = new OptionsEntity[ 1 ];
                OptionsEntity opt = new OptionsEntity();
                opt.setPinyin( content.getPinyin() );
                opt.setData( content.getLang() );
                opt.setQuestion( content.getSentence() );
                optList[ 0 ] = opt;
            }

            int typeId = data.getQuestionTypeId();
            int optLayout = content.getTypesetting() == 1 ? OptionsLayout.LINEAR :
                    typeId == 1008 ? OptionsLayout.VIDEO_GRID : OptionsLayout.GRID;
            int pageType = PagerType.NONE;
            switch ( typeId ) {
                case 1001:      //选择题
                    pageType = PagerType.SELECT;
                    break;
                case 1002:      //听力题
                    pageType = PagerType.HEARING;
                    break;
                case 1003:      //听力标签题
                    pageType = PagerType.LABEL_HEARING;
                    optLayout = OptionsLayout.NONE;
                    break;
                case 1005:      //翻译标签题
                    pageType = PagerType.LABEL_TRANSLATE;
                    optLayout = OptionsLayout.NONE;
                    break;
                case 1006:      //语音题
                    pageType = PagerType.VOICE;
                    optLayout = OptionsLayout.NONE;
                    break;
                case 1004:      //翻译题
                    pageType = PagerType.TRANSLATE;
                    optLayout = OptionsLayout.LINEAR;
                    break;
                case 1007:      //匹配题
                    pageType = PagerType.MATCH;
                    optLayout = OptionsLayout.LINEAR_GRID;
                    break;
                case 1008:      //视频题
                    pageType = PagerType.VIDEO;
                    break;
            }
            page.setPagerType( pageType );
            page.setOptLayout( optLayout );
            page.setOpts( optList );
            pageList.add( page );
            addFragments( page, false );
        }


        aeData.setDataList( pageList );
        mDataList = aeData.getContent();
        addFragments( null, true );

        Consumer<Boolean> downloadCall = downloadResult -> viewCall( v -> {
            //用于跳题（无题可跳默认为第一页）
            toItem( mCurrentItem );
            updatedPageSelected();
            v.onCallShowLoadingDialog( false );
            if( call != null ) call.accept( result );
        } );
        MediaXC.get().add( null, audioUrlSet );

        post( () -> downloadCall.accept( true ), audioUrlSet.size() > 20 ? 3000 : 0 );

        LogUtil.e( "Answer -> dataCall -> audioUrlSet size:" + audioUrlSet.size() );
    }

    public int getAnswerType() { return mAnswerType; }

    public int getCurrentAnswerId() {
        int index = mPager.getCurrentItem();
        PagerEntity data = mDataList.get( index < mDataList.size() ? index : mDataList.size() - 1 );
        return data == null ? -1 : data.getId();
    }

    /**
     * 初始化ViewPager
     * @param pager     ViewPager
     */
    public void initFragmentViewPager(FragmentViewPager pager) {
        //禁用滑动
        pager.setEnableScroll( mAnswerType == Constant.AnswerType.ERROR_MAP );
        //启用隐藏改变通知
        pager.setEnableVisibleChanged( true );
        //初始化Pager页
        pager.setFragmentActivity( (FragmentActivity) getActivity() );
        if( mPager == null ) mPager = pager;
    }

    /**
     * 练习模式下加载下一小节的数据
     */
    private void loadNextAnswer(@Constant.AnswerType int type, Consumer<Boolean> call) {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );

        LogUtil.e("loadNextAnswer -> mNodeNum:" + mNodeNum);
        switch ( type ) {
            case Constant.AnswerType.NORMAL:                //练习模式
                int index = mNodeNum < mIds.size() ? mNodeNum : mIds.size() - 1;
                mReqAnswer.reqAnswerLearn(
                        mIds.get( index ).getSectionId(), list ->
                        dataCall( list, true, call )
                );
                break;
            case Constant.AnswerType.TEST:                  //阶段考试
                mReqAnswer.reqAnswerTest(
                        mIds.get( 0 ).getSectionId(), list ->
                        dataCall( list, true, call )
                );
                break;
            case Constant.AnswerType.SKIP_TEST:           //跳级考试
                mReqAnswer.reqAnswerExam(
                        mIds.get( 0 ).getSectionId(), list ->
                                dataCall( list, true, call )
                );
                break;
            case Constant.AnswerType.FAST_REVIEW:           //快速复习
                mReqAnswer.reqReviewQuestion(mId, data -> {
                    List<AnswerQuestionEntity> list = data.getQuestion();
                    boolean isNull = list == null || list.size() == 0;
                    if( isNull ) list = new ArrayList<>();
                    mReviewLevelId = data.getLevelId();
                    //当前进度
                    mCurrentItem = data.getProgress();
                    nNextIndex = mCurrentItem;
                    //题目排序
                    Collections.sort(list, (o1, o2) -> o1.getIndex() - o2.getIndex());
                    dataCall( list, isNull, call );
                });
                break;
            case Constant.AnswerType.ERROR_MAP:             //错题集
                if( mWrongQuestionSetTotalPage > 0 && mWrongQuestionSetCurrentPage == mWrongQuestionSetTotalPage ) {
                    onNextItemEnd();
                    break;
                }
                mReqAnswer.reqWrongQuestionSet(++mWrongQuestionSetCurrentPage, 10, data -> {
                    if( data == null ) {
                        dataCall( null, false, call );
                        return;
                    }
                    WrongQuestionSetQuestionEntity question = data.getQuestion();
                    List<AnswerQuestionEntity> list = null;
                    if( question != null ) {
                        mWrongQuestionSetTotalPage = question.getLastPage();
                        mWrongQuestionSetCurrentPage = question.getCurrentPage();
                        list = question.getData();
                    }
                    //当前进度
                    mCurrentItem = data.getProgress() - 1;
                    nNextIndex = mCurrentItem;
                    dataCall( list, list == null, call );
                });
                break;
        }
    }

    private final List<MvpFragment> mFragmentList = new LinkedList<>();

    @UiThread
    private void addFragments(@Nullable PagerEntity data, boolean isEndPage) {
        if( data == null && isEndPage ) {
            //正常模式下添加里程碑页面
            if( mAnswerType == Constant.AnswerType.NORMAL ) {
                addNodePage( mNodeNum, mIds.size() );
            }

            //添加结束页
            if( mAnswerType != Constant.AnswerType.ERROR_MAP && mNodeNum >= mNodeCount - 1 ) {
                addEndPage( mAnswerStatus != 2, false );
            }

            viewCall( v -> v.onCallFragment( true, mFragmentList ) );

            LogUtil.e( "addFragments -> " +
                    "mAnswerType:" + mAnswerType + " | " +
                    "mNodeNum:" + mNodeNum + " | " +
                    "mNodeCount:" + mNodeCount + " | " +
                    "Size:" + mDataList.size() + " | " +
                    "mFragmentList:" + mFragmentList.size()
            );
        }else {
            MvpFragment fragment;
            fragment = mAnswerHelperModel.createMvpFragmentAndData(
                    data, newBundle(), mAnswerBaseEntity, mAnswerType
            );
            if( fragment != null ) mFragmentList.add( fragment );
        }
        LogUtil.e( "addFragments -> isEndPage:" + isEndPage + " | " + data );
    }

    public int getNextNum() { return nNextIndex; }

    /**
     * 添加里程碑页面
     * @param index         当前下标
     * @param count         最大评级
     */
    private void addNodePage(int index, int count) {
        Bundle arg = new Bundle();
        //当前评级
        arg.putInt( Constant.RATING_NUM, index + 1 );
        //最大评级
        arg.putInt( Constant.MAX_RATING, count );
        if( mAnswerType == Constant.AnswerType.NORMAL ) {
            SectionIds sIds = mIds.get( index );
            arg.putInt( Constant.ID_FOR_INT, sIds.getSectionId() );
            arg.putString( Constant.NAME, sIds.getSectionName() );
        }
        MvpFragment fragment = new AnswerNodeFragment();
        mFragmentList.add( fragment );
        fragment.setArguments(  arg );
    }

    private void addEndPage(boolean result, boolean isNext) {
        MvpFragment fragment = new AnswerEndFragment();
        Bundle arg = newBundle();
        //题目类型
        arg.putInt( Constant.ANSWER_TYPE, mAnswerType );
        //是否完成答题
        arg.putBoolean( Constant.ANSWER_RESULT, result );
        //基础数据
        arg.putParcelable( Constant.ANSWER_BASE_ENTITY, mAnswerBaseEntity );
        fragment.setArguments( arg );
        if( isNext ) {
            mFragmentList.add( nNextIndex + 1, fragment );
        }else {
            mFragmentList.add( fragment );
        }
        LogUtil.e( "addEndPage -> " +
                "result:" + result + " | " +
                "isNext:" + isNext + " | " +
                "index:" + ( mFragmentList.size() - 1 ) + " | " +
                "f:" + fragment
        );
    }

    @UiThread
    public void setToolbarVisibility(@Nullable Bundle args) {
        AnswerView v = getView();
        if( args == null || v == null ) return;
        int vis = args.getInt( Constant.TOOLBAR_VISIBILITY, -1 );
        long millis = args.getLong( Constant.DELAY_MILLIS, 0 );
        if( vis != -1 ) post(() -> v.onCallAnswerBarVisibility( vis ), millis);

    }

    public void addWrongQuestionSet(int questionId, boolean isUpload) {
        boolean isNormal = mAnswerType == Constant.AnswerType.NORMAL;
//        boolean isTest = mAnswerType == Constant.AnswerType.TEST;
        if( isNormal ) nNextIndex--;

        //加入错题
        if( isUpload ) {
            mReqAnswer.reqAddWrongQuestionSet(questionId, result -> {
                //错题集只会在每次进入时，首次答错才会回调提示
                if( !mIsShowError ) {
                    mIsShowError = true;
                    viewCall( v -> v.onCallWrongQuestionSetDaoResult( result ) );
                }
            });
            return;
        }

        PagerEntity data = null;
        for (PagerEntity pe : mDataList) {
            if( pe.getId() != questionId ) continue;
            data = pe;
            break;
        }
        Fragment f = mAnswerHelperModel.createMvpFragmentAndData(
                data, newBundle(), mAnswerBaseEntity, mAnswerType
        );
        if( f == null ) {
            LogUtil.e("addWrongQuestionSet -> create Fragment Error.");
            return;
        }

        mPager.addFragment( mPager.getFragmentCount() - 2, f );
        mPager.notifyAdapter();
    }

    public void removeWrongQuestionSet(int questionId, boolean isUpload) {
        //重置连错次数
        mConsecutiveErrorCount = 0;
        if( isUpload ) mReqAnswer.reqRemoveWrongQuestionSet( questionId, null );
    }

    /**
     * 每个节点才会发起保存（鼓励页）
     //     * @param args  保存信息
     */
    public void saveProgressOfBundle(/*1.1.1 @Nullable Bundle args*/Consumer<Boolean> call) {
        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:
                if( ++mNodeNum >= mNodeCount ) mNodeNum = mNodeCount;
                LogUtil.e( "saveProgressOfBundle -> NORMAL -> nNextIndex:" + nNextIndex );
                break;
            case Constant.AnswerType.SKIP_TEST:
                mNodeNum = mNodeCount;
                break;
            case Constant.AnswerType.FAST_REVIEW:
                break;
        }
        saveProgress(result -> {
            if( !result && mNodeNum > 0 ) mNodeNum--;
            if( call != null ) call.accept( result );
            LogUtil.d("saveProgressOfBundle -> saveProgress -> " +
                    "saveProgress:" + result + " | " +
                    "mNodeNum:" + mNodeNum
            );
        });
        LogUtil.d("saveProgressOfBundle -> " +
                "mAnswerType:" + mAnswerType + " | " +
                "nNextIndex:" + nNextIndex + " | " +
                "nNextSize:" + nNextSize + " | " +
                "mNodeNum:" + mNodeNum
        );
    }

    public void nextItemOfBundle(@Nullable Bundle args) {
        if( args == null ) return;
        boolean isNext = args.getBoolean( Constant.NEXT_POSITION );
        if( !isNext ) return;

        switch ( mAnswerType ) {
            case Constant.AnswerType.TEST:
                break;
            case Constant.AnswerType.SKIP_TEST:
                /* 模块测试答错三次退出 */
                if( /* mAnswerStatus != 2 && */ mWrongAnswerLastCountOfModuleTest <= 0 ) {
                    addEndPage( false, true );
                    viewCall( v -> {
                        v.onCallFragment( false, mFragmentList );
                        post( this::nextItem );
                    });
                    LogUtil.e( "nextItemOfBundle -> End skip test" );
                    return;
                }
                break;
        }
        boolean isSkipPage = false;
        int tmpNextNum = nNextIndex;
        int toItemIndex = 0;
        //1.1.2 启用跳题模式时更新nNextIndex
        if( isExistEnableSkip() ) {
//            toItemIndex = doToItem( mCurrentItem + 1 ) - 1;
            toItemIndex = getNextIndex( mCurrentItem + 1 );
            tmpNextNum += toItemIndex - mCurrentItem;
            LogUtil.d( "nextItemOfBundle -> " + tmpNextNum + " | nNextIndex:" + nNextIndex );
            isSkipPage = isSkipPage( toItemIndex );
            if( !isSkipPage && tmpNextNum == nNextSize - 1 ) tmpNextNum -= 1;
            //正常流程到第四题时下标为4，跳题时为5，所以减一
            if( tmpNextNum == nNextSize ) tmpNextNum -= 1;
        }

        LogUtil.d("nextItemOfBundle -> " +
                        "tmpNextNum:" + tmpNextNum + " | " +
                        "nNextIndex:" + nNextIndex + " | " +
                        "nNextSize:" + nNextSize + " | " +
                        "mNodeNum:" + mNodeNum + " | " +
                        "mNodeCount:" + mNodeCount + " | " +
                        "toItemIndex:" + toItemIndex + " | " +
                        "mCurrentItem+1:" + ( mCurrentItem + 1 ) + " | " +
                        "getCurrentItem:" + mPager.getCurrentItem() + " | " +
                        "mPager.getFragmentCount():" + mPager.getFragmentCount() + " | " +
//                        "isAnswerPage:" + isAnswerPage + " | " +
                        "isExistEnableSkip:" + isExistEnableSkip() + " | " +
                        "isSkipPage:" + isSkipPage
//                "index:" + index
        );

        //保存节点 1.1.2
        boolean isNormal = mAnswerType == Constant.AnswerType.NORMAL;
        boolean isTest = mAnswerType == Constant.AnswerType.TEST;
        if( ( isNormal || isTest ) && tmpNextNum + 1 == nNextSize ) {
//            nNextIndex -= 1;
            viewCall( v -> v.onCallShowLoadingDialog( true ) );
            saveProgressOfBundle(result -> {
                viewCall( v -> {
//                nNextIndex -= 1;
                    nextItem();
                    v.onCallShowLoadingDialog( false );
                }, 1000 );
                isNextSaveProgressSuccess = result;
            });
            return;
        }

        if( tmpNextNum >= nNextSize && mNodeNum < mNodeCount ) {
            switch ( mAnswerType ) {
                case Constant.AnswerType.NORMAL:
                    //切换下一题
                    loadNextAnswer(Constant.AnswerType.NORMAL, r -> { if( r ) post( this::nextItem ); });
                    break;
                case Constant.AnswerType.ERROR_MAP:
                    loadNextAnswer(Constant.AnswerType.ERROR_MAP, r -> {
                        if( r ) post( this::nextItem );
                    });
                    break;
            }
            return;
        }
        post( this::nextItem );
    }

    @UiThread
    public void nextItem() {
        if( mPager == null ) return;
        int index = mCurrentItem + 1;

        //鼓励页
        if( mAnswerType == Constant.AnswerType.NORMAL && mConsecutiveErrorCount >= 3 ) {
            mConsecutiveErrorCount = 0;
            StartUtil.startEncourageActivity();
        }

        //小测验鼓励页
        if( mAnswerType == Constant.AnswerType.TEST && mTestEncourageCount >= 5 ) {
            mTestEncourageCount = 0;
            StartUtil.startAnswerTestNodeActivity();
        }

        if( nNextIndex++ >= nNextSize && mNodeNum < mNodeCount ) {
            nNextIndex = 0;
        }

        if( mNodeNum >= mNodeCount && index >= mPager.getFragmentCount() ) {
            //结束
            if( isSaveAward ) {
                onNextItemEnd();
            }else {
                if( index >= mPager.getFragmentCount() ) {
                    onNextItemEnd();
                }else {
                    endSave( r -> {
                        //下一页（等待经验值页面打开完毕）
                        post( () -> toItem( index ), 500 );
                    } );
                }
            }
        }else {
            //下一页
            toItem( index );
        }
        //更新进度
        updatedPageSelected();

        LogUtil.e("nextItem -> " +
                "index:" + index + " | " +
                "count:" + mPager.getFragmentCount() + " | " +
                "nNextIndex:" + nNextIndex + " | " +
                "nNextSize:" + nNextSize + " | " +
                "mNodeNum:" + mNodeNum + " | " +
                "mNodeCount:" + mNodeCount + " | " +
                "isSaveAward:" + isSaveAward
        );
    }

    /**
     * 跳转到当前下标
     * @param index     当前下标
     */
    private void toItem(int index) {
        mCurrentItem = getNextIndex( index );
        nNextIndex += mCurrentItem - index;
        post( () -> mPager.setCurrentItem( mCurrentItem, true ) );

        LogUtil.e("toItem -> " +
                "index:" + index + " | " +
                "nNextIndex:" + nNextIndex + " | " +
                "mNodeNum:" + mNodeNum + " | " +
                "mCurrentItem:" + mCurrentItem + " | " +
                "fragmentCount:" + mPager.getFragmentCount()
        );
    }

    /**
     * 根据当前下标递归判断下一个下标的题目是否允许被展示，递归至允许被展示的题目下标时返回
     * @param index     当前下标
     * @return          允许被展示的题型下标
     */
    private int getNextIndex(int index) {
        if( index < 0 ) index = 0;
        Fragment f = mPager.getFragment( index );
        if( isEnableHearing( f ) || isEnableVoice( f ) || isEnableTranslate( f ) ) {
            index = getNextIndex( ++index );
        }
        LogUtil.e( "getNextIndex -> " + index );
        return index;
    }

    private boolean isExistEnableSkip() {
        return isSkipType() && mSetUpModel.isExistEnableSkip();
    }

    private boolean isEnableHearing(Fragment f) {
        return isSkipType() && mSetUpModel.isSkipHearing() &&
                ( f instanceof HearingFragment || f instanceof LabelHearingFragment);
    }

    private boolean isEnableVoice(Fragment f) {
        return isSkipType() && mSetUpModel.isSkipVoice() && f instanceof VoiceFragment;
    }

    private boolean isEnableTranslate(Fragment f) {
        return isSkipType() && mSetUpModel.isSkipTranslate() &&
                ( f instanceof TranslateFragment || f instanceof LabelTranslateFragment);
    }

    private boolean isSkipPage(int index) {
        Fragment f = mPager.getFragment( index );
        return isEnableHearing( f ) || isEnableVoice( f ) || isEnableTranslate( f );
    }

    private boolean isSkipType() {
        return mAnswerType != Constant.AnswerType.SKIP_TEST && mAnswerType != Constant.AnswerType.TEST;
    }

    /**
     * 保存进度
     * @param call      保存结果
     */
    private void saveProgress(@Nullable Consumer<Boolean> call) {
        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:            //正常模式
                mReqAnswer.reqSaveLearnProgressAndOpenLE( mId, mNodeNum, mContinuouslyCorrectList, r -> {
                            doAnswerExp( r );
                            if( call != null ) call.accept( r.isSuccess() );
                            LogUtil.d( "Answer -> saveProgress -> NORMAL:" + r.isSuccess() );
                });
                break;
            case Constant.AnswerType.SKIP_TEST:         //跳级考试
                mReqAnswer.reqSaveExamProgressAndOpenLEE( mId, mContinuouslyCorrectList, mWrongAnswerCount, r -> {
                            doAnswerExp( r );
                            if( call != null ) call.accept( r.isSuccess() );
                            LogUtil.d( "Answer -> saveProgress -> SKIP_TEST:" + r.isSuccess() );
                });
                break;
            case Constant.AnswerType.TEST:              //阶段考试
                //保存爱心
                mReqAnswer.reqAnswerOpenHeart( mId, mWrongAnswerCount, mContinuouslyCorrectList, nNextSize, data -> {
                    isSaveAward = true;
                    AnswerEndFragment f = (AnswerEndFragment) mFragmentList.get( mFragmentList.size() - 1 );
                    mOldScore = data.getOldScore();
                    mNewScore = data.getNewScore();
                    if( f != null ) f.setScore( mOldScore, mNewScore );

                    //处理经验值
                    doAnswerExp( data );
                    if( call != null ) call.accept( data.isSuccess() );
                    LogUtil.d( "Answer -> saveProgress -> TEST:" + data.isSuccess() );
                } );
                break;
            case Constant.AnswerType.FAST_REVIEW:       //快速复习
                int progress = mCurrentItem >= nNextSize ? 0 : mCurrentItem;
                //保存快速复习进度
                mReqAnswer.reqReviewProgress( mReviewLevelId, progress, call );
                LogUtil.d( "Answer -> saveProgress -> FAST_REVIEW -> progress:" + progress );
                break;
            case Constant.AnswerType.ERROR_MAP:         //错题集
                //保存错题集进度
                mReqAnswer.reqSaveErrorRecord( nNextIndex + 1, call );
                LogUtil.d( "Answer -> saveProgress -> ERROR_MAP" );
                break;
        }
    }

    private void updateKeyword(Consumer<Boolean> call) {
        if( isAnswerStatusOfComplete() ) {
            LogUtil.e("updateKeyword -> type:" + mAnswerType + " | Not update");
            return;
        }
        mOtherModel.uploadRecordKeyword(mKeywordList, result -> {
            if( call != null ) call.accept( result );
        });

        LogUtil.e("updateKeyword -> type:" + mAnswerType + " | " +
                Arrays.toString( mKeywordList.toArray( new String[0] ) )
        );
    }

    /**
     * 更新进度
     */
    @UiThread
    public void updatedPageSelected(int offset) { onPageSelected( nNextIndex + offset ); }

    /**
     增加小测验鼓励页的次数（达量展示）
     */
    @UiThread
    public void incrementTestEncourageCount() {
        mTestEncourageCount++;
        if( mAnswerType != Constant.AnswerType.ERROR_MAP ) nextPageSelected( nNextIndex + 1 );
    }

    /**
     * 更新进度
     */
    @UiThread
    private void updatedPageSelected() { updatedPageSelected( 0 ); }

    public void onPageSelected(int position) {
        nextPageSelected( position );
        switch ( mAnswerType ) {
            case Constant.AnswerType.ERROR_MAP:
                nNextIndex = position;
                break;
            case Constant.AnswerType.FAST_REVIEW:
                saveProgress( null );
                break;
        }
        LogUtil.e("onPageSelected -> position:" + position);
    }

    private void nextPageSelected(int pos) {
        viewCall(v -> v.onCallPageSelected(
                pos,
                nNextIndex != nNextSize - 1 ? R.string.stringCheck : R.string.stringContinue,
                nNextIndex == 0 ? View.GONE : View.VISIBLE
        ));
    }

    public void onUpdatedCurrentItem(boolean isNext) { }

    public void buriedPointCloseButton(boolean isClose) {
        if( mAnswerBaseEntity == null ) mAnswerBaseEntity = new AnswerBaseEntity();
        String uid = mUserModel.getUid();
        String userName = mUserModel.getUserName( getResources() );

        if( mAnswerType == Constant.AnswerType.TEST && mAnswerBaseEntity.isRecordRecord() ) {
            int result = nNextIndex >= nNextSize - 1 ? 0 : nNextIndex + 1;
            if( result == 0 ) {
                result = mNewScore >= 60 && mNewScore > mOldScore ?
                        0 : mNewScore == mOldScore ?
                        -2 : -1;
            }
            if( mOldScore == 0 ) {
                BuriedPointEvent.get().onTestOfRefreshRecordPageOfRefreshRecordButton(
                        getContext(), uid, userName, result
                );
            }else {
                BuriedPointEvent.get().onFailTestPageOfTryAgainButton(
                        getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() ), result == 0
                );
            }

        }

        BuriedPointEvent.get().onTestoutOfCloseButton(
                getContext(),
                uid, userName,
                mAnswerBaseEntity.getCategoryId(),
                mAnswerBaseEntity.getCategoryName(),
                mAnswerBaseEntity.getLevelId(),
                mAnswerBaseEntity.getLevelName()
        );

        if( isAnswerStatusOfComplete() ) {
            BuriedPointEvent.get().onQuickLearnExercisePageOfCloseButton(
                    getContext(),
                    isClose,
                    uid, userName,
                    mAnswerBaseEntity.getCategoryId(),
                    mAnswerBaseEntity.getCategoryName(),
                    mAnswerBaseEntity.getLevelId(),
                    mAnswerBaseEntity.getLevelName(),
                    mAnswerBaseEntity.getUnitId(),
                    mAnswerBaseEntity.getUnitName()
            );
        }
    }

    private void onNextItemEnd() {
        LogUtil.e( "onNextItemEnd -> " +
                "Type:" + mAnswerType + " | " +
                "mAnswerStatus:" + mAnswerStatus + " | " +
                "mNodeNum:" + mNodeNum
        );
        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:            //正常模式
            case Constant.AnswerType.SKIP_TEST:         //跳级考试
                boolean isComplete = isAnswerStatusOfComplete();
                //已完成的，不需要领取闪电
                onCallEndResult( isComplete, isComplete ? 0 : 1 );
                break;
            case Constant.AnswerType.TEST:              //阶段考试
                onCallEndResult( true, 2 );
                break;
            case Constant.AnswerType.FAST_REVIEW:       //快速复习
                onCallEndResult( true, 0 );
                BuriedPointEvent.get().onPhaseReviewCompletionPageOfContinueButton(
                        getContext()
                );
                break;
            default:
                onCallEndResult( true, 0 );
                break;
        }
    }

    private void saveAward(@Nullable Consumer<Boolean> call) {
        if( isSaveAward ) {
            if( call != null ) call.accept( true );
            return;
        }
        switch ( mAnswerType ) {
            case Constant.AnswerType.NORMAL:                    //正常模式
            case Constant.AnswerType.SKIP_TEST:                 //跳级考试
                //已完成，不需要领取闪电
                if( isAnswerStatusOfComplete() ) {
                    isSaveAward = true;
                    if( call != null ) call.accept( true );
                    break;
                }
                switch ( mAnswerType ) {
                    case Constant.AnswerType.NORMAL:            //正常模式
                        isSaveAward = true;
                        if( call != null ) call.accept( true );
                        //保存关键字
                        if( nNextIndex >= nNextSize ) updateKeyword( null );
                        break;
                    case Constant.AnswerType.SKIP_TEST:         //跳级考试
                        //上传关键字
                        updateKeyword(r -> {
                            isSaveAward = true;
                            if( call != null ) call.accept( r );
                        });
                        break;
                }
                break;
            case Constant.AnswerType.FAST_REVIEW:               //快速复习
                //领取奖励
                mReqAnswer.reqReviewFinishAndOpenExp( mId, r -> {
                    isSaveAward = true;
                    doAnswerExp( r );
                    if( call != null ) call.accept( r.isSuccess() );
                } );
                break;
            default:
                isSaveAward = true;
                if( call != null ) call.accept( true );
                break;
        }
    }

    private void doAnswerExp(@NonNull AnswerExpEntity data) {
        if( isDestroy || data.getExp() == 0 ) {
            DOM.getInstance().setResult( DOMConstant.ANSWER_EXP_NEXT, false );
            return;
        }
        StartUtil.startAnswerExpActivity( mAnswerType, data, mAnswerBaseEntity );
    }

    /**
     * 处理结果
     * @param result            处理结果
     * @param playSoundType     0：无，1：闪电，2：红心
     */
    private void onCallEndResult(@NonNull Boolean result, int playSoundType) {
        viewCall(v -> {
            finish();
            if( result ) {
                //完成时播放相应的音效
                startCompleteSound( playSoundType );
            }
        });
    }

    public boolean isAnswerStatusOfComplete() { return mAnswerStatus == 2; }

    private void startCompleteSound(int playSoundType) {
        post(() -> {
            int soundResult = 0;
            if( playSoundType == 1 ) {     //闪电
                soundResult = Constant.SoundType.SOUND_GET_LIGHTNING;
            }
            if( soundResult == 0 ) return;
            BaseActivity activity = (BaseActivity) getActivity();
            if( activity == null ) return;
            activity.setCallResult( soundResult );
        }, 1000);
    }

    @UiThread
    private void callError() {
        isError = true;
        AnswerView v = getView();
        if( v == null || v.getContext() == null ) return;
        //暂无数据
        v.onCallError( v.getContext().getResources().getString( R.string.stringNotData ) );
    }
}
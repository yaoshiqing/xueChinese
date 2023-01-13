package com.gjjy.frontlib;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.api.entity.AnswerExpEntity;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.basiclib.utils.Constant;
import com.ybear.mvp.view.fragment.MvpFragment;

import java.util.ArrayList;

public class StartUtil {


    /**
     * 答题介绍页
     * @param data          基础信息
     * @param sectionIds    小节ids
     * @param sectionNum    当前进度
     */
    public static void startNormalAnswerDescActivity(AnswerBaseEntity data,
                                                     @NonNull ArrayList<SectionIds> sectionIds,
                                                     int sectionNum) {
        ARouter.getInstance()
                .build( "/front/normalAnswerDescActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .withParcelableArrayList( Constant.SECTION_IDS, sectionIds )
                .withInt( Constant.SECTION_NUM, sectionNum )
                .navigation();
    }

    /**
     * 练习答题页
     */
    public static void startAnswerActivityOfNormal(AnswerBaseEntity data,
                                                   ArrayList<Integer> sectionIds,
                                                   int sectionNum) {
        ARouter.getInstance()
                .build( "/front/answerActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .withIntegerArrayList( Constant.SECTION_IDS, sectionIds )
                .withInt( Constant.SECTION_NUM, sectionNum )
                .withInt( Constant.ANSWER_TYPE, Constant.AnswerType.NORMAL )
                .navigation();
    }

    /**
     * 考试答题页
     */
    public static void startAnswerActivityOfTest(AnswerBaseEntity data) {
        ARouter.getInstance()
                .build( "/front/answerActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .withInt( Constant.ANSWER_TYPE, Constant.AnswerType.TEST )
                .navigation();
    }

    /**
     * 刷新答题分数页面
     */
    public static void startAnswerTestRefreshScoreActivity(AnswerBaseEntity data) {
        ARouter.getInstance()
                .build( "/front/answerTestRefreshScoreActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .navigation();
    }

    /**
     * 跳级答题页
     */
    public static void startAnswerActivityOfModuleTest(AnswerBaseEntity data) {
        ARouter.getInstance()
                .build( "/front/answerActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .withInt( Constant.ANSWER_TYPE, Constant.AnswerType.SKIP_TEST )
                .navigation();
    }

    /**
     * 启动快速复习页面
     */
    public static void startAnswerActivityOfFastReview(AnswerBaseEntity data) {
        ARouter.getInstance()
                .build( "/front/answerActivity" )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, data )
                .withInt( Constant.ANSWER_TYPE, Constant.AnswerType.FAST_REVIEW )
                .navigation();
    }

    /**
     * 启动错题集页面
     */
    public static void startAnswerOfWrongQuestionSetActivity() {
        ARouter.getInstance()
                .build( "/front/answerActivity" )
//                .withParcelable( Constant.ANSWER_BASE_ENTITY, new AnswerBaseEntity() )
                .withInt( Constant.ANSWER_TYPE, Constant.AnswerType.ERROR_MAP )
                .navigation();
    }

    public static void startReViewActivity() {
        ARouter.getInstance().build( "/front/reViewActivity" ).navigation();
    }

    public static void startFastReViewActivity() {
        ARouter.getInstance().build( "/front/fastReViewActivity" ).navigation();
    }

    public static void startWordsListActivity() {
        ARouter.getInstance().build( "/front/wordListActivity" ).navigation();
    }

    public static void startSearchWordsActivity() {
        ARouter.getInstance().build( "/front/searchWordsActivity" ).navigation();
    }

    public static void startNeedLoginActivity(MvpFragment fragment,
                                              @PageName String pageName) {
        Postcard p = ARouter.getInstance().build("/login/needLoginActivity");
        LogisticsCenter.completion( p );
        Intent intent = new Intent( fragment.getContext(), p.getDestination() );
        intent.putExtra( Constant.PAGE_NAME, pageName );
        intent.putExtra( Constant.IS_FULL_SCREEN, true );
        fragment.startActivityForResult(
                intent,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_NEED_LOGIN_RESULT
        );
    }

    public static void startEncourageActivity() {
        ARouter.getInstance().build( "/front/encourageActivity" ).navigation();
    }

    public static void startIntroduceActivity() {
        ARouter.getInstance().build( "/front/introduceActivity" ).navigation();
    }

    public static void startAnswerExpActivity(@Constant.AnswerType int answerType,
                                              @NonNull AnswerExpEntity expData,
                                              AnswerBaseEntity baseData) {
        ARouter.getInstance()
                .build( "/front/answerExpActivity" )
                .withInt( Constant.ANSWER_TYPE, answerType )
                .withInt( Constant.ANSWER_EXP, expData.getExp() )
                .withInt( Constant.ANSWER_TODAY_EXP, expData.getTodayExp() )
                .withParcelable( Constant.ANSWER_BASE_ENTITY, baseData )
                .navigation();
    }

    public static void startAnswerTestNodeActivity() {
        ARouter.getInstance().build( "/front/answerTestNodeActivity" ).navigation();
    }

    public static void startSkipTestLightningConsumeActivity(Activity activity,
                                                             AnswerBaseEntity baseData) {
        Postcard p = ARouter.getInstance().build("/front/skipTestLightningConsumeActivity");
        LogisticsCenter.completion( p );
        Intent intent = new Intent( activity, p.getDestination() );
        intent.putExtra( Constant.ANSWER_BASE_ENTITY, baseData );
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(
                activity, p, com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_SKIP_TEST_LIGHTNING
        );
    }

    /**
     刷新成绩 - 闪电不足
     @param activity        activity
     */
    public static void startRefreshScoreLackLightningActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build("/front/refreshScoreLackLightningActivity");
        LogisticsCenter.completion( p );
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(
                activity, p, com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LACK_LIGHTNING_OF_REFRESH_SCORE
        );
    }
}

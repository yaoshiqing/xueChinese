package com.gjjy.basiclib.mvp.model;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.api.entity.AnswerDetailEntity;
import com.gjjy.basiclib.api.entity.AnswerExpEntity;
import com.gjjy.basiclib.api.entity.AnswerQuestionEntity;
import com.gjjy.basiclib.api.entity.OpenLightningResultEntity;
import com.gjjy.basiclib.api.entity.ReviewEntity;
import com.gjjy.basiclib.api.entity.ReviewNewCountEntity;
import com.gjjy.basiclib.api.entity.ReviewStatusEntity;
import com.gjjy.basiclib.api.entity.WrongQuestionSetEntity;
import com.ybear.ybnetworkutil.request.Request;
import com.gjjy.basiclib.api.apiAnswer.AddWrongQuestionSetApi;
import com.gjjy.basiclib.api.apiAnswer.AnswerDetailApi;
import com.gjjy.basiclib.api.apiAnswer.AnswerExamApi;
import com.gjjy.basiclib.api.apiAnswer.AnswerLearnApi;
import com.gjjy.basiclib.api.apiAnswer.AnswerTestApi;
import com.gjjy.basiclib.api.apiAnswer.WrongQuestionSetApi;
import com.gjjy.basiclib.api.apiAnswer.RemoveWrongQuestionSetApi;
import com.gjjy.basiclib.api.apiAnswer.SaveErrorRecordApi;
import com.gjjy.basiclib.api.apiAnswer.SaveExamProgressAndOpenLEEApi;
import com.gjjy.basiclib.api.apiAnswer.SaveLearnProgressAndOpenLEApi;
import com.gjjy.basiclib.api.apiUserLevel.AnswerOpenHeartAndExpApi;
import com.gjjy.basiclib.api.apiUserLevel.ReviewFinishAndOpenExpApi;
import com.gjjy.basiclib.api.apiUserLevel.ReviewNewCountApi;
import com.gjjy.basiclib.api.apiUserLevel.ReviewProgressApi;
import com.gjjy.basiclib.api.apiUserLevel.ReviewQuestionApi;
import com.gjjy.basiclib.api.apiUserLevel.ReviewStatusApi;

import java.util.Arrays;
import java.util.List;

/**
 * 模块答题
 */
public class ReqAnswerModel extends BasicGlobalReqModel{
    private void callbackAnswerQuestionList(@NonNull Request api,
                                            Consumer<List<AnswerQuestionEntity>> call) {
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList(s, AnswerQuestionEntity.class ) );
        });
    }

    /**
     * 获取模块详情
     * @param call          请求结果
     */
    public void reqAnswerDetail(int unitId, Consumer<AnswerDetailEntity> call) {
        AnswerDetailApi api = new AnswerDetailApi();
        api.addParam( "unit_id", unitId );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            AnswerDetailEntity data = toReqEntity( s, AnswerDetailEntity.class );
            call.accept( data == null ? new AnswerDetailEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 获取模块练习题目
     * @param sectionId     小节id
     * @param call          请求结果
     */
    public void reqAnswerLearn(int sectionId, Consumer<List<AnswerQuestionEntity>> call) {
        AnswerLearnApi api = new AnswerLearnApi();
        api.addParam( "section_id", sectionId );
        callbackAnswerQuestionList( api, call );
        reqApi( api );
    }

    /**
     * 获取模块考试题目
     * @param unitId        模块id
     * @param call          请求结果
     */
    public void reqAnswerExam(int unitId, Consumer<List<AnswerQuestionEntity>> call) {
        AnswerExamApi api = new AnswerExamApi();
        api.addParam( "unit_id", unitId );
        callbackAnswerQuestionList( api, call );
        reqApi( api );
    }

    /**
     * 获取阶段考试题目
     * @param levelId       阶段id
     * @param call          请求结果
     */
    public void reqAnswerTest(int levelId, Consumer<List<AnswerQuestionEntity>> call) {
        AnswerTestApi api = new AnswerTestApi();
        api.addParam( "level_id", levelId );
        callbackAnswerQuestionList( api, call );
        reqApi( api );
    }

    /**
     1.1.2
     保存开始学习进度并领取闪电和经验值
     @param unitId          模块id
     @param sectionNum      完成小节数量
     @param row             连对数量数组，格式举例：[2,4]，表示连对了2题和4题
     @param call            请求结果
     */
    public void reqSaveLearnProgressAndOpenLE(int unitId, int sectionNum, List<Integer> row,
                                              Consumer<OpenLightningResultEntity> call) {
        //（isModelFullUnlock。题目全解锁）
        if( Config.isModelFullUnlock ) {
            OpenLightningResultEntity data = new OpenLightningResultEntity();
            data.setCode( 1 );
            data.setExp( 40 );
            data.setTodayExp( 400 );
            data.setOpenLightning( true );
            call.accept( data );
            return;
        }

        SaveLearnProgressAndOpenLEApi api = new SaveLearnProgressAndOpenLEApi();
        api.addParam( "unit_id", unitId );
        api.addParam( "section_num", sectionNum );
        api.addParam( "row", listIntToArrayInt( row ) );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            OpenLightningResultEntity data = toReqEntity( s, OpenLightningResultEntity.class );
            call.accept( data == null ? new OpenLightningResultEntity() : data );
        });
        reqApi( api );
    }

    /**
     1.1.2
     保存跳级考试进度并领取闪电和经验值
     @param unitId          模块id
     @param row             连对数量数组，格式举例：[2,4]，表示连对了2题和4题
     @param errorNum        错题总数量
     @param call            请求结果
     */
    public void reqSaveExamProgressAndOpenLEE(int unitId, List<Integer> row, int errorNum,
                                              Consumer<OpenLightningResultEntity> call) {
        //（isModelFullUnlock。题目全解锁）
        if( Config.isModelFullUnlock ) {
            OpenLightningResultEntity data = new OpenLightningResultEntity();
            data.setCode( 1 );
            data.setExp( 40 );
            data.setTodayExp( 400 );
            data.setOpenLightning( true );
            call.accept( data );
            return;
        }

        SaveExamProgressAndOpenLEEApi api = new SaveExamProgressAndOpenLEEApi();
        api.addParam( "unit_id", unitId );
        api.addParam( "row", listIntToArrayInt( row ) );
        api.addParam( "error_num", errorNum );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            OpenLightningResultEntity data = toReqEntity( s, OpenLightningResultEntity.class );
            call.accept( data == null ? new OpenLightningResultEntity() : data );
        });
        reqApi( api );
    }



//    /**
//     * 1.1.1
//     * 保存模块进度
//     * @param unitId        模块id
//     * @param sectionNum    完成小节数量
//     * @param call          请求结果
//     */
//    public void reqAnswerSaveProgress(int unitId, int sectionNum, Consumer<Boolean> call) {
//        AnswerSaveProgressApi api = new AnswerSaveProgressApi();
//        api.addParam( "unit_id", unitId );
//        api.addParam( "section_num", sectionNum );
//        api.setCallbackString((s, isResponse) -> {
//            if( call != null ) call.accept( toReqEntityOfBase( s ).isSuccess() );
//        });
//        reqApi( api );
//    }

//    /**
//     * 1.1.1
//     * 领取模块闪电
//     * @param unitId        模块id
//     * @param call          请求结果
//     */
//    public void reqAnswerOpenLightning(int unitId, Consumer<Boolean> call) {
//        AnswerOpenLightningApi api = new AnswerOpenLightningApi();
//        api.addParam( "unit_id", unitId );
//        api.setCallbackString((s, isResponse) -> {
//            if( call == null ) return;
////            BaseReqEntity data = toReqEntityOfBase( s );
//            call.accept( true );
//        });
//        reqApi( api );
//    }

    /**
     1.1.2
     领取阶段心形
     @param levelId         阶段id
     @param errorCount      错题总数量
     @param row             连对数量数组，格式举例：[2,4]，表示连对了2题和4题
     @param count           题目总数量
     @param call            请求结果
     */
    public void reqAnswerOpenHeart(int levelId, int errorCount, List<Integer> row, int count,
                                   Consumer<AnswerExpEntity> call) {
        //（isModelFullUnlock。题目全解锁）
        if( Config.isModelFullUnlock ) {
            AnswerExpEntity data = new AnswerExpEntity();
            data.setCode( 1 );
            data.setExp( 40 );
            data.setTodayExp( 400 );
            data.setOldScore( 100 );
            data.setNewScore( 70 );
            call.accept( data );
            return;
        }

        AnswerOpenHeartAndExpApi api = new AnswerOpenHeartAndExpApi();
        api.addParam( "level_id", levelId );
        //添加exp参数
        toExpRequest( api, levelId, errorCount, row );
        api.addParam( "total_num", count );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
//            BaseReqEntity data = toReqEntityOfBase( s );
//            call.accept( true );
            AnswerExpEntity data = toReqEntity( s, AnswerExpEntity.class );
            call.accept( data == null ? new AnswerExpEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 获取快速复习分类信息
     * @param call          请求结果
     */
    public void reqReviewStatus(Consumer<List<ReviewStatusEntity>> call) {
        ReviewStatusApi api = new ReviewStatusApi();
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList(s, ReviewStatusEntity.class ) );
        });
        reqApi( api );
    }

    /**
     * 刷新成绩
     * @param call          请求结果
     */
    public void reqRefreshScore(Consumer<Boolean> call) {
        ReviewProgressApi api = new ReviewProgressApi();
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     * 保存快速复习进度
     * @param levelId       阶段id
     * @param progress      进度数量
     * @param call          请求结果
     */
    public void reqReviewProgress(int levelId, int progress, Consumer<Boolean> call) {
        ReviewProgressApi api = new ReviewProgressApi();
        api.addParam( "level_id", levelId );
        api.addParam( "progress", progress );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     1.1.2
     保存完成状态并领取经验值
     @param categoryId      分类id
     @param call            请求结果
     */
    public void reqReviewFinishAndOpenExp(int categoryId, Consumer<AnswerExpEntity> call) {
        //（isModelFullUnlock。题目全解锁）
        if( Config.isModelFullUnlock ) {
            AnswerExpEntity data = new AnswerExpEntity();
            data.setCode( 1 );
            data.setExp( 40 );
            data.setTodayExp( 400 );
            call.accept( data );
            return;
        }

        ReviewFinishAndOpenExpApi api = new ReviewFinishAndOpenExpApi();
        api.addParam( "category_id", categoryId );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            AnswerExpEntity data = toReqEntity(s, AnswerExpEntity.class );
            call.accept( data == null ? new AnswerExpEntity() : data );
        });
        reqApi( api );
    }

//    /**
//     1.1.1
//     * 保存复习题为完成状态
//     * @param categoryId    分类id
//     * @param call          请求结果
//     */
//    public void reqReviewFinish(int categoryId, Consumer<ReviewStatusEntity> call) {
//        ReviewFinishApi api = new ReviewFinishApi();
//        api.addParam( "category_id", categoryId );
//        api.setCallbackString((s, isResponse) -> {
//            ReviewStatusEntity data = toReqEntity(s, ReviewStatusEntity.class );
//            if( call != null ) call.accept( data == null ? new ReviewStatusEntity() : data );
//        });
//        reqApi( api );
//    }

    /**
     * 获取分类下的复习题目
     * @param categoryId    分类id
     * @param call          请求结果
     */
    public void reqReviewQuestion(int categoryId, Consumer<ReviewEntity> call) {
        ReviewQuestionApi api = new ReviewQuestionApi();
        api.addParam( "category_id", categoryId );

        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            ReviewEntity data = toReqEntity( s, ReviewEntity.class );
            call.accept( data == null ? new ReviewEntity() : data );
        });
//        callbackAnswerQuestionList( api, call );
        reqApi( api );
    }

    /**
     * 获取新复习阶段总数量
     * @param call          请求结果
     */
    public void reqReviewNewCount(Consumer<ReviewNewCountEntity> call) {
        ReviewNewCountApi api = new ReviewNewCountApi();
        api.setCallbackString((s, isResponse) -> {
            ReviewNewCountEntity data = toReqEntity( s, ReviewNewCountEntity.class );
            if( call != null ) call.accept( data == null ? new ReviewNewCountEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 添加错题集
     * @param questionId    题目id
     * @param call          请求结果
     */
    public void reqAddWrongQuestionSet(int questionId, Consumer<Boolean> call) {
        AddWrongQuestionSetApi api = new AddWrongQuestionSetApi();
        api.addParam( "question_id", questionId );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     * 移除错题集
     * @param questionId    题目id
     * @param call          请求结果
     */
    public void reqRemoveWrongQuestionSet(int questionId, Consumer<Boolean> call) {
        RemoveWrongQuestionSetApi api = new RemoveWrongQuestionSetApi();
        api.addParam( "question_id", questionId );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     * 获取错题集列表
     * @param page          第几页
     * @param pageSize      每页显示记录数
     * @param call          请求结果
     */
    public void reqWrongQuestionSet(int page, int pageSize, Consumer<WrongQuestionSetEntity> call) {
        WrongQuestionSetApi api = new WrongQuestionSetApi();
        api.addParam( "page", page );
        api.addParam( "pagesize", pageSize );
        api.setCallbackString((s, isResponse) -> {
            WrongQuestionSetEntity data = toReqEntity( s, WrongQuestionSetEntity.class );
            if( call != null ) call.accept( data == null ? new WrongQuestionSetEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 保存错题记录
     * @param progress      进度数量
     * @param call          请求结果
     */
    public void reqSaveErrorRecord(int progress, Consumer<Boolean> call) {
        SaveErrorRecordApi api = new SaveErrorRecordApi();
        api.addParam( "progress", progress );
        callbackSuccess( api, call );
        reqApi( api );
    }

    private void toExpRequest(Request api, int levelId, int errorCount, List<Integer> row) {
        api.addParam( "level_id", levelId );
        api.addParam( "error_num", errorCount );
        api.addParam( "row", listIntToArrayInt( row ) );
    }

    private String listIntToArrayInt(List<Integer> list) {
        return Arrays.toString( list.toArray( new Integer[ 0 ] ) );
    }
}
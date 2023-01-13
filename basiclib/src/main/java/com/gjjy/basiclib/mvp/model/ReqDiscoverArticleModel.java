package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.alibaba.fastjson.JSONObject;
import com.gjjy.basiclib.api.entity.DiscoveryDetailEntity;
import com.gjjy.basiclib.api.entity.DiscoveryListEntity;
import com.gjjy.basiclib.api.entity.CommentsDataChildEntity;
import com.gjjy.basiclib.api.entity.CommentsDataEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonRecordEntity;
import com.gjjy.basiclib.api.entity.DiscoveryMorePageEntity;
import com.ybear.ybnetworkutil.request.Request;
import com.ybear.ybutils.utils.ObjUtils;
import com.gjjy.basiclib.api.apiDiscoverArticle.EditCollectionApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.ReqCollectionListsApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.ReqDetailApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.ReqListsApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.ReqReadListsApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.ReqTopListsApi;
import com.gjjy.basiclib.api.apiDiscoverArticle.SaveReadRecordApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.AddFirstLevelTalkApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.AddPraiseApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.AddSecondLevelTalkApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.FirstLevelListsApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.SecondLevelListsApi;
import com.gjjy.basiclib.api.apiUserDiscoverArticleTalk.TopCommentsApi;

import java.util.List;

/**
 * 发现模块
 */
public class ReqDiscoverArticleModel extends BasicGlobalReqModel{

    private void callFindList(Request api, Consumer<List<DiscoveryListEntity>> call) {
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList( s, DiscoveryListEntity.class ) );
        });
    }

    private void callFindMorePage(Request api, Consumer<DiscoveryMorePageEntity> call) {
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            DiscoveryMorePageEntity data = toReqEntityNotBase( s, DiscoveryMorePageEntity.class );
            call.accept( data == null ? new DiscoveryMorePageEntity() : data );
        });
    }

    /**
     * 每日聆听列表
     * @param call          请求结果
     */
    public void reqListenDailyList(Consumer<List<DiscoveryListEntity>> call) {
        ReqTopListsApi api = new ReqTopListsApi();
        api.addParam( "discover_type_id", 1001 );
        callFindList( api, call );
        reqApi( api );
    }

    /**
     * 热门视频列表
     * @param call          请求结果
     */
    public void reqPopularVideosList(Consumer<List<DiscoveryListEntity>> call) {
        ReqTopListsApi api = new ReqTopListsApi();
        api.addParam( "discover_type_id", 1002 );
        callFindList( api, call );
        reqApi( api );
    }

    /**
     * 专项学习列表
     * @param call          请求结果
     */
    public void reqTargetedLearningList(Consumer<List<DiscoveryListEntity>> call) {
        ReqTopListsApi api = new ReqTopListsApi();
        api.addParam( "discover_type_id", 1003 );
        callFindList( api, call );
        reqApi( api );
    }

    /**
     * 每日聆听子列表
     * @param page          第几页
     * @param call          请求结果
     */
    public void reqListenDailyMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        ReqListsApi api = new ReqListsApi();
        api.addParam( "discover_type_id", 1001 );
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        callFindMorePage( api, call );
        reqApi( api );
    }

    /**
     * 热门视频子列表
     * @param page          第几页
     * @param call          请求结果
     */
    public void reqPopularVideosMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        ReqListsApi api = new ReqListsApi();
        api.addParam( "discover_type_id", 1002 );
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        callFindMorePage( api, call );
        reqApi( api );
    }

    /**
     * 专项学习子列表
     * @param page          第几页
     * @param call          请求结果
     */
    public void reqTargetedLearningMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        ReqListsApi api = new ReqListsApi();
        api.addParam( "discover_type_id", 1003 );
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        callFindMorePage( api, call );
        reqApi( api );
    }

    /**
     * 获取收藏列表
     * @param page          第几页
     * @param call          请求结果
     */
    public void reqCollectionList(int page, Consumer<DiscoveryMorePageEntity> call) {
        ReqCollectionListsApi api = new ReqCollectionListsApi();
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        callFindMorePage( api, call );
        reqApi( api );
    }

    /**
     * 获取看过列表
     * @param page          第几页
     * @param call          请求结果
     */
    public void reqHaveReadList(int page, Consumer<DiscoveryMorePageEntity> call) {
        ReqReadListsApi api = new ReqReadListsApi();
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        callFindMorePage( api, call );
        reqApi( api );
    }

    /**
     * 获取每日聆听 或者 热门视频 文章详情
     * @param id        文章id
     * @param call      请求结果
     */
    public void reqFindDetail(long id, Consumer<DiscoveryDetailEntity> call) {
        ReqDetailApi api = new ReqDetailApi();
        api.addParam( "discover_article_id", id );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            DiscoveryDetailEntity data = toReqEntity( s, DiscoveryDetailEntity.class );
            call.accept( data == null ? new DiscoveryDetailEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 更改文章收藏状态
     * @param id        文章id
     * @param call      处理结果
     */
    public void editCollectStatus(long id, boolean isCollect, Consumer<Boolean> call) {
        EditCollectionApi api = new EditCollectionApi();
        api.addParam( "discover_article_id", id );
        api.addParam( "is_collection", isCollect ? 1 : 0 );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     * 保存文章记录
     * @param id        文章id
     * @param call      处理结果
     */
    public void saveReadRecord(long id, DiscoveryDetailJsonRecordEntity record, Consumer<Boolean> call) {
        SaveReadRecordApi api = new SaveReadRecordApi();
        api.addParam( "discover_article_id", id );
        api.addParam( "json_record", JSONObject.toJSONString( record ) );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     评论列表
     @param discoverArticleId       文章Id
     @param sortType                评论类型。1：最新，2：最热
     @param page                    返回页数
     @param call                    处理结果
     */
    public void reqCommentsData(long discoverArticleId,
                                int sortType,
                                int page,
                                Consumer<CommentsDataEntity> call) {
        FirstLevelListsApi api = new FirstLevelListsApi();
        api.addParam( "discover_article_id", discoverArticleId );
        api.addParam( "sort_type", sortType );
        api.addParam( "page", page );
        api.addParam( "pagesize", 8 );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            CommentsDataEntity data = toReqEntityNotBase( s, CommentsDataEntity.class );
            call.accept( data == null ? new CommentsDataEntity() : data );
        });
        reqApi( api );
    }

    /**
     评论子列表
     @param talkId                  评论Id
     @param page                    返回页数
     @param call                    处理结果
     */
    public void reqCommentsChildData(int talkId, int page, int pageSize,
                                     Consumer<CommentsDataEntity> call) {
        SecondLevelListsApi api = new SecondLevelListsApi();
        api.addParam( "talk_id", talkId );
        api.addParam( "page", page );
        api.addParam( "pagesize", pageSize );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            CommentsDataEntity data = toReqEntityNotBase( s, CommentsDataEntity.class );
            call.accept( data != null ? data : new CommentsDataEntity() );
        });
        reqApi( api );
    }

    /**
     添加一级评论
     @param discoverArticleId   文章表自增ID
     @param content             评论内容
     @param call                处理结果
     */
    public void reqAddComments(long discoverArticleId, String content, Consumer<Integer> call) {
        AddFirstLevelTalkApi api = new AddFirstLevelTalkApi();
        api.addParam( "discover_article_id", discoverArticleId );
        api.addParam( "content", content );
        api.setCallbackString( (s, isResponse) -> {
            if( call != null ) call.accept( ObjUtils.parseInt( toReqEntityOfValue( s, "talk_id" ) ) );
        } );
        reqApi( api );
    }

    /**
     添加二级评论
     @param targetTalkId        回复目标的talk_id
     @param content             评论内容
     @param call                处理结果
     */
    public void reqAddChildComments(int targetTalkId, String content, Consumer<Integer> call) {
        AddSecondLevelTalkApi api = new AddSecondLevelTalkApi();
        api.addParam( "target_talk_id", targetTalkId );
        api.addParam( "content", content );
        api.setCallbackString( (s, isResponse) -> {
            if( call != null ) call.accept( ObjUtils.parseInt( toReqEntityOfValue( s, "talk_id" ) ) );
        } );
        reqApi( api );
    }

    /**
     获取文章置顶评论
     @param talkId          评论id
     @param interactType    评论类型。1：点赞。2：评论
     @param call            处理结果
     */
    public void reqTopCommentsList(int talkId, int interactType,
                                   Consumer<CommentsDataChildEntity> call) {
        TopCommentsApi api = new TopCommentsApi();
        api.addParam( "talk_id", talkId );
        api.addParam( "interact_type", interactType );
        api.setCallbackString( (s, isResponse) -> {
            if( call == null ) return;
            CommentsDataChildEntity data = toReqEntity( s, CommentsDataChildEntity.class );
            call.accept( data == null ? new CommentsDataChildEntity() : data );
        } );
        reqApi( api );
    }

    /**
     文章评论点赞
     @param talkId              评论表自增ID
     @param call                处理结果
     */
    public void reqCommentsLike(int talkId, Consumer<Boolean> call) {
        AddPraiseApi api = new AddPraiseApi();
        api.addParam( "talk_id", talkId );
        callbackSuccess( api, call );
        reqApi( api );
    }
}
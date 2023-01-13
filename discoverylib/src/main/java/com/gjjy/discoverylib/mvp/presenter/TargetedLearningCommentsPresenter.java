package com.gjjy.discoverylib.mvp.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.time.DateTime;
import com.gjjy.discoverylib.adapter.CommentListAdapter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.TargetedLearningCommentsView;
import com.gjjy.basiclib.api.entity.CommentsDataChildEntity;
import com.gjjy.basiclib.api.entity.CommentsDataEntity;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TargetedLearningCommentsPresenter extends MvpPresenter<TargetedLearningCommentsView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;
    private long mDiscoverArticleId;
    private int mTouchMainCommentsPos = -1;
    private int mTouchChildCommentsPos = -1;
    private CommentListAdapter.ItemData mItemData;
    private boolean isShowHotComments = true;
    private int mDataPage = 1;
    private final SparseIntArray mChildDataPageSparse = new SparseIntArray();
    private final SparseBooleanArray mChildDataNextSparse = new SparseBooleanArray();
//    private int mChildDataCount = 0;

    public TargetedLearningCommentsPresenter(@NonNull TargetedLearningCommentsView view) {
        super(view);
    }

    @Override
    public void onLifeActivityCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeActivityCreate( savedInstanceState );
        viewCall( v -> v.onCallUserPhoto( mUserModel.getAvatarUrl() ) );

//        refreshDataList();
    }

    @Override
    public void onDestroyPresenter() {
        super.onDestroyPresenter();
        mChildDataPageSparse.clear();
        mChildDataNextSparse.clear();
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }
    public long getUserId() { return mUserModel.getUserId(); }

    public void setShowHotComments(boolean showHot) { isShowHotComments = showHot; }
    public boolean isShowHotComments() { return isShowHotComments; }

    public boolean isLoginResult() { return mUserModel.isLoginResult(); }

    public long getDiscoverArticleId() { return mDiscoverArticleId; }

    public void setSendInfo(CommentListAdapter.ItemData data, int mainPos, int childPos) {
        mItemData = data;
        mTouchMainCommentsPos = mainPos;
        mTouchChildCommentsPos = childPos;
        LogUtil.e( "setSendInfo -> " +
                "mainPos:" + mainPos + " | " +
                "childPos:" + childPos + " | \n" +
                "data:" + data
        );
    }

    public void queryDataList(long discoverArticleId, int page, boolean isCache) {
        mDiscoverArticleId = discoverArticleId;
        mDataPage = page;
        Consumer<CommentsDataEntity> call = data -> viewCall( v -> {
            v.onCallCommentData( toCommentData( data ), page, isShowHotComments, false );
//            if( data.getData().size() == 0 ) mDataPage--;
            if( mDataPage >= data.getLastPage() ) mDataPage = -1;
        });
        //热门列表
        if( isShowHotComments ) {
            mDisArtModel.reqCommentsDataOfHot( discoverArticleId, page, isCache, call );
            return;
        }
        //最新列表
        mDisArtModel.reqCommentsDataOfNew( discoverArticleId, page, isCache, call );
    }

    private int hasTopCommentTalkId = 0;
    public boolean isHaveTopComment() { return hasTopCommentTalkId != 0; }

    public void queryDataListAndTopData(long discoverArticleId, int topTalkId, int topInteractType) {
        if( topTalkId <= 0 || topInteractType <= 0 ) {
            hasTopCommentTalkId = 0;
            queryDataList( discoverArticleId, 1, false );
        }else {
            queryTopDataList(
                    topTalkId,
                    topInteractType,
                    result -> queryDataList( discoverArticleId, 1, false )
            );
        }
    }

    public void nextDataList() {
        if( mDataPage == -1 ) {
            viewCall( v -> v.onCallCommentData( null, mDataPage, isShowHotComments, false ) );
            return;
        }
        queryDataList( mDiscoverArticleId, ++mDataPage, false );
    }

    private boolean isQuerying = false;
    public void queryChildDataList(int talkId, int pos, int count, int page, int pageSize,
                                   boolean compelQuery, Consumer<Boolean> call) {
        if( isQuerying && !compelQuery ) return;
        mChildDataPageSparse.put( talkId, page );
//        mChildDataCount = count;
        isQuerying = true;
        mDisArtModel.reqCommentsChildData( talkId, page, pageSize, data -> {
            //移除之前评论过的消息
            if( mSendTalkId.size() > 0 ) {
                Iterator<CommentsDataChildEntity> iterator = data.getData().iterator();
                for( Integer sendTalkId : mSendTalkId ) {
                    while( iterator.hasNext() ) {
                        if( iterator.next().getTalkId() == sendTalkId ) {
                            iterator.remove();
                        }
                    }
                }
                mSendTalkId.clear();
            }
            viewCall( v -> {
                boolean isNotify = count == -1 || pos == count - 1;
                v.onCallCommentChildData(
                        talkId,
                        toCommentData( data ),
                        isShowHotComments,
                        isNotify
                );
                isQuerying = false;
                if( call != null ) call.accept( !isNotify );
            });
        });
    }

    public void queryChildDataList(int talkId, int pos, int count, int page, int pageSize,
                                   boolean compelQuery) {
        queryChildDataList( talkId, pos, count, page, pageSize, compelQuery, null );
    }

    public void startQueryChildDataList(List<CommentListAdapter.ItemData> list, int startPos) {
        queryChildDataList( list.get( startPos ).getTalkId(), startPos, list.size(),
                1, 2, true, result -> {
            if( !result ) return;
            startQueryChildDataList( list, startPos + 1 );
        } );
    }

    public void nextChildDataList(int talkId, int pos) {
        int page = mChildDataPageSparse.get( talkId ) + 1;
        boolean topNext = mChildDataNextSparse.get( talkId );
        mChildDataPageSparse.put( talkId, page );
        if( !topNext ) {
            mChildDataNextSparse.put( talkId, true );
            queryChildDataList( talkId, pos, -1, 2, 2, true );
            post( () -> {
                queryChildDataList(
                        talkId, pos, -1, 5, 1, true
                );
                mChildDataPageSparse.put( talkId, 1 );
            }, 100);
        }else{
            queryChildDataList( talkId, pos, -1, page, 5, false );
        }
    }

    public void queryTopDataList(int talkId, int interactType, @NonNull Consumer<Integer> call) {
        mDisArtModel.reqTopComments( talkId, interactType, data -> {
            int mainTalkId = data.getTalkId();
            if( mainTalkId == 0 ) {
                call.accept( 0 );
                return;
            }
            CommentsDataEntity commentsData = new CommentsDataEntity();
            CommentsDataEntity childCommentsData = new CommentsDataEntity();
            List<CommentsDataChildEntity> list = new ArrayList<>();
            list.add( data );
            commentsData.setData( list );
            childCommentsData.setData( data.getSecondLevel() );
            viewCall( v -> v.onCallCommentData(
                    toCommentData( commentsData ), 0, true, true
            ));
            viewCall( v -> {
                v.onCallCommentChildData(
                        mainTalkId, toCommentData( childCommentsData ), true,  false
                );
                call.accept( hasTopCommentTalkId = mainTalkId );
            });
        } );
    }

    private final List<Integer> mSendTalkId = new ArrayList<>();
    public void sendMsg(String content) {
        final long discoverArticleId = mDiscoverArticleId;
        final int touchMainPos = mTouchMainCommentsPos;
        final int touchChildPos = mTouchChildCommentsPos;
        int sendTalkId = mItemData == null ? -1 : mItemData.getTalkId();
        Consumer<Integer> call = talkId -> {
            boolean result = talkId > 0;
            //埋点
            buriedPointSendComments( result );
            CommentListAdapter.ItemData data = result ? new CommentListAdapter.ItemData() : null;
            if( data == null ) {
                viewCall( v -> v.onCallSendMsgResult(
                        null, -1, -1, false
                ));
                return;
            }
            mSendTalkId.add( talkId );
            data.setTalkId( talkId );
            data.setUserId( mUserModel.getUserId() );
            data.setUserName( mUserModel.getUserName( getResources() ) );
            data.setTargetTalkId( sendTalkId );
            data.setTargetUserId( mItemData == null ? -1 : mItemData.getUserId() );
            data.setTargetNickname( mItemData == null ? null : mItemData.getUserName() );
            data.setDiscoverArticleId( discoverArticleId );
            data.setPhotoUrl( mUserModel.getAvatarUrl() );
            data.setContent( content );
            data.setVip( mUserModel.isVip() );
            data.setVipStatus( mUserModel.getVipStatus() );
            data.setTimeStamp( System.currentTimeMillis() );
            viewCall(v -> {
                v.onCallSendMsgResult( data, touchMainPos, touchChildPos, true );
                setSendInfo(  null, -1, -1 );
            });
        };
        //没有内容
        if( TextUtils.isEmpty( content ) ) return;
        if( touchChildPos == -1 ) {
            //一级评论
            mDisArtModel.reqAddComments( discoverArticleId, content, call );
        }else {
            //二级评论
            mDisArtModel.reqAddChildComments( sendTalkId, content, call );
        }
        LogUtil.e( "sendMsg -> " +
                "mainPos:" + touchMainPos + " | " +
                "touchChildPos:" + touchChildPos + " | " +
                "daId:" + discoverArticleId + " | " +
                "stId:" + sendTalkId + " | " +
                "content:" + content
        );
    }

    public void like() {
        Consumer<Boolean> call = result -> viewCall(v -> v.onCallLikeResult(
                mTouchMainCommentsPos, mTouchChildCommentsPos, result
        ));
        if( mItemData == null ) {
            call.accept( false );
            return;
        }
        int talkId = mItemData.getTalkId();
        if( mTouchMainCommentsPos >= 0 ) {
            List<CommentListAdapter.ItemData> replyList = mItemData.getReplyMsgList();
            if( mTouchChildCommentsPos >= 0 && mTouchChildCommentsPos < replyList.size() ) {
                talkId = replyList.get( mTouchChildCommentsPos ).getTalkId();
            }
        }
        mDisArtModel.reqCommentsLike( talkId, call );
    }

    private CommentListAdapter.CommentData toCommentData(CommentsDataEntity data) {
        if( data == null ) return null;
        CommentListAdapter.CommentData retData = new CommentListAdapter.CommentData();
        retData.setItemTotalCount( data.getTotal() );
        retData.setCurrentPage( data.getCurrentPage() );
        retData.setLastPage( data.getLastPage() );

        List<CommentsDataChildEntity> childList = data.getData();
        List<CommentListAdapter.ItemData> itemList = retData.getItemList();
        if( childList == null ) return retData;
        itemList.clear();
        for( CommentsDataChildEntity child : childList ) {
            //跳过置顶的id
            if( hasTopCommentTalkId != 0 && child.getTalkId() == hasTopCommentTalkId ) continue;
            CommentListAdapter.ItemData item = new CommentListAdapter.ItemData();
            item.setTalkId( child.getTalkId() );
            item.setUserId( child.getUserId() );
            item.setDiscoverArticleId( child.getDiscoverArticleId() );
            item.setUserName( child.getNickname() );
            item.setTargetTalkId( child.getTargetTalkId() );
            item.setTargetUserId( child.getTargetUserId() );
            item.setTargetNickname( child.getTargetNickname() );
            item.setPhotoUrl( child.getAvatarUrl() );
            item.setContent( child.getContent() );
            item.setLikeCount( child.getPraiseNum() );
            item.setLike( child.isPraise() );
            item.setVip( child.isVip() );
            item.setVipStatus( child.getVipStatus() );
            String createTime = child.getCreateTime();
            if( !TextUtils.isEmpty( createTime ) ) item.setTimeStamp( DateTime.parse( createTime ) );
            itemList.add( item );
        }
        return retData;
    }

    private void buriedPointSendComments(boolean result) {
        BuriedPointEvent.get().onVideoGrammarCourseDetailPageOfCommentPageOfCommentButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                result
        );
    }

//    public static void main(String[] args) {
//        MethodScheduling ms = MethodScheduling.create();
//        //A的处理
//        ms.add( new MethodScheduling.Run(){
//            @Override
//            public void next(MethodScheduling ms, Object data) {
//                new A( s -> {
//                    System.out.println( "A -> " + s + " | data:" + data );
//                    ms.nextOfPoll();
//                } );
//            }
//        } );
//        //B的处理
//        ms.add( new MethodScheduling.Run(){
//            @Override
//            public void next(MethodScheduling ms, Object data) {
//                new B( s -> {
//                    System.out.println( "B -> " + s + " | data:" + data );
//                    ms.nextOfPoll();
//                } );
//            }
//        } );
//        //C的处理
//        ms.add( new MethodScheduling.Run(){
//            @Override
//            public void next(MethodScheduling ms, Object data) {
//                new C( s -> {
//                    System.out.println( "C -> " + s + " | data:" + data );
//                    ms.nextOfPoll();
//                } );
//            }
//        } );
//        //开始
//        ms.next();
//    }
//
//    public static class MethodScheduling {
//        public interface Call { void next(MethodScheduling ms, Object data); }
//
//        public abstract static class Run implements Call {
//            private Object mData;
//            Run setData(Object data) {
//                mData = data;
//                return this;
//            }
//            Object getData() { return mData; }
//        }
//        private MethodScheduling() {}
//        public static MethodScheduling create() { return new MethodScheduling(); }
//
//        private static final Queue<Run> mQueue = new LinkedList<>();
//
//
//        public void add(@NonNull Run run, @Nullable Object data) {
//            mQueue.add( run.setData( data ) );
//        }
//        public void add(@NonNull Run run) { add( run, null ); }
//        public void nextOfPeek() { next( true ); }
//        public void nextOfPoll() { next( false ); }
//        public void next() { nextOfPoll(); }
//        private void next(boolean isPeek) {
//            Run run = isPeek ? mQueue.peek() : mQueue.poll();
//            if( run != null ) run.next( this, run.getData() );
//        }
//
//    }
//
//    public static class A {
//        public A(Consumer<String> call) {
//            call.accept( "this A class" );
//        }
//    }
//
//    public static class B {
//        public B(Consumer<String> call) {
//            call.accept( "this B class" );
//        }
//    }
//
//    public static class C {
//        public C(Consumer<String> call) {
//            call.accept( "this C class" );
//        }
//    }
}
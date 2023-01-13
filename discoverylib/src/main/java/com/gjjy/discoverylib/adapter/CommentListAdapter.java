package com.gjjy.discoverylib.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.gjjy.discoverylib.R;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.MessageTimeStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentListAdapter extends
        BaseRecyclerViewAdapter<CommentListAdapter.ItemData, BaseViewHolder> {

    public @interface ClickType {
        int LIKE = 1;
        int REPLY_MSG = 2;
        int MORE = 3;
        int VIEW_COMMENTS = 4;
    }
    public interface OnCommentsClickBtnListener {
        void onClick(View v, @Nullable ItemData mainData, ItemData data, @ClickType int type,
                     int mainPos, int childPos);
    }

    private final RequestManager mGlide;
    private final CommentData mCommentData;
    private final ItemData mMainItemData;
    private final int mMainPos;
    private int mDefaultTableCheckId;

    private OnCommentsClickBtnListener mOnCommentsClickBtnListener;

    private CommentListAdapter(@NonNull RequestManager glide, @NonNull CommentData data,
                               ItemData mainItemData, int mainPos) {
        super( data.getItemList() );
        mCommentData = data;
        mGlide = glide;
        mMainItemData = mainItemData;
        mMainPos = mainPos;
        setEnableTouchStyle( false );
    }
    public CommentListAdapter(@NonNull RequestManager glide, @NonNull CommentData data) {
        this( glide, data, null, -1 );
    }
//    public CommentListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> data,
//                              ItemData mainItemData, int mainPos) {
//        this( glide, new CommentData( data ), mainItemData, mainPos );
//    }
//    public CommentListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> data, int mainPos) {
//        this( glide, new CommentData( data ), null, mainPos );
//    }


    public void switchTrendingType() {
        mDefaultTableCheckId = R.id.targeted_learning_details_comments_rb_trending_btn;
    }

    public void switchNewType() {
        mDefaultTableCheckId = R.id.targeted_learning_details_comments_rb_new_btn;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if( viewType == 0 ) {
            return new CommentHolder( parent, R.layout.item_comment, mMainItemData != null );
        }
        return new CommentTypeHolder( parent, R.layout.block_comments_table );
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder h, int pos) {
        super.onBindViewHolder( h, pos );
        ItemData data = getItemData( pos );
        if( data == null ) return;

        //最热，最新 类型按钮
        if( h instanceof CommentTypeHolder ) {
            CommentTypeHolder holder = (CommentTypeHolder) h;
            RadioGroup rgTable = holder.getTable();
            if( mDefaultTableCheckId != 0 ) rgTable.check( mDefaultTableCheckId );
            rgTable.setOnCheckedChangeListener( (group, checkedId) -> {
                if( mOnCommentsTypeCheckedChangeListener == null ) return;
                mOnCommentsTypeCheckedChangeListener.onCheckedChanged( group, checkedId );
            } );
            return;
        }

        //常规消息
        CommentHolder holder = (CommentHolder) h;
        boolean isNullMainData = mMainItemData == null;
        //二级给一级评论，否则就是二级给二级评论
        boolean isSameTalk = !isNullMainData && data.getTargetTalkId() == mMainItemData.getTalkId();
        String guestName = getResources( holder ).getString( R.string.stringSignature );
        //昵称
        String userName = TextUtils.isEmpty( data.getUserName() ) ? guestName : data.getUserName();

        if( holder.isShowUserInfo() ) {
            String photoUrl = data.getPhotoUrl();
            //头像
            if( TextUtils.isEmpty( photoUrl ) ) {
                holder.getPhoto().setImageResource( R.drawable.ic_photo_user );
            }else {
                mGlide.load( data.getPhotoUrl() ).into( holder.getPhoto() );
            }
            //vip图标
            holder.getVipIcon().setVisibility( data.isVip() ? View.VISIBLE : View.GONE );
            holder.getVipIcon().setImageResource( data.getVipStatus() == 3 ?
                    R.drawable.ic_photo_user_vip_official_icon :
                    R.drawable.ic_photo_user_vip_icon
            );
            //用户名
            holder.getUserName().setText( userName );
        }
        TextView tvContext = holder.getContent();
        //评论内容
        String content = data.getContent();
        if( !TextUtils.isEmpty( content ) ) {
            String targetName = data.getTargetNickname();
            if( TextUtils.isEmpty( targetName ) ) targetName = guestName;
            tvContext.setText( Html.fromHtml( getContent(
                    userName,
                    isNullMainData ? null : isSameTalk ? null : targetName,
                    content,
                    mMainPos == -1
            )));
            boolean isShowMoreContent = tvContext.getLineCount() > 4;
            TextView tvMoreContentBtn = holder.getMoreContentBtn();
            //展示更多评论内容
            tvMoreContentBtn.setVisibility( isShowMoreContent ? View.VISIBLE : View.GONE );
            if( isShowMoreContent ) {
                tvContext.setMaxLines( 4 );
                tvMoreContentBtn.setOnClickListener( v -> tvContext.setMaxLines( Integer.MAX_VALUE ) );
            }
        }

        //喜欢的数量
        int likeCount = data.getLikeCount();
        holder.getLikeBtn().setText( likeCount > 0 ? ObjUtils.toNumberSize( likeCount ) : "" );
        //是否喜欢
        holder.getLikeBtn().setCompoundDrawablesWithIntrinsicBounds(
                data.isLike() ?
                        R.drawable.ic_comments_like_selected_btn :
                        R.drawable.ic_comments_like_un_selected_btn,
                0, 0, 0
        );
        //时间
        holder.getTime().setText(
                DateTime.toMessageTime( data.getTimeStamp(), new MessageTimeStyle(
                        getResources( holder ),
                        R.string.stringCommentTimeOfRecently,
                        R.string.stringCommentTimeOfMinutesAgo,
                        R.string.stringCommentTimeOfHoursAgo,
                        R.string.stringCommentTimeOfYesterday,
                        R.string.stringCommentTimeOfDayAgo
                ))
        );
        CommentData replyMsgData = data.getReplyMsgData();
        RecyclerView rvChild = holder.getChildListView();
        //展示回复消息
        if( replyMsgData != null ) {
            CommentListAdapter adapter = new CommentListAdapter( mGlide, replyMsgData, data, pos );
            rvChild.setLayoutManager( new LinearLayoutManager( getContext( holder ) ) );
            rvChild.setAdapter( adapter );
            rvChild.setVisibility( View.VISIBLE );
            adapter.setOnCommentsClickBtnListener( mOnCommentsClickBtnListener );
            holder.getMainDiv().setVisibility( replyMsgData.getItemList().size() > 0 ? View.VISIBLE : View.GONE );
        }else {
            rvChild.setVisibility( View.GONE );
            holder.getMainDiv().setVisibility( View.GONE );
        }
        //点击时的数据
//        ItemData clickData = isSameTalk ? mMainItemData : data;

//        holder.getDiv().setVisibility( mMainItemData == null ? View.GONE : View.VISIBLE );
        //更多回复消息按钮的文本（ View comments(N) ）
        if( mMainItemData != null ) {
            //隐藏更多回复消息按钮
            holder.setLastChildItemDiv( 1 );
            //处理结尾的消息
            if( pos == getItemCount() - 1 ) {
                TextView tvViewComments = holder.getViewCommentsBtn();
                int childItemCount = mCommentData.getItemList().size();
                int notShowCount = mCommentData.getItemTotalCount() - childItemCount;
//                LogUtil.e( "mCommentDataCommentData:" + mCommentData.getItemTotalCount() + " | " + childItemCount );
                boolean isShowViewComments = notShowCount > 0;
                tvViewComments.setText((
                        getResources( holder ).getString( R.string.stringViewComments ) +
                                ( isShowViewComments ? ( "(" + notShowCount + ")" ) : "" )
                ));
                //显示/隐藏更多回复消息按钮
                holder.setLastChildItemDiv( childItemCount > 0 ? isShowViewComments ? 2 : 1 : 0 );
                //点击事件
                //更多二级评论
                tvViewComments.setOnClickListener( v ->
                        onCommentsClickBtnListener( v, data, ClickType.VIEW_COMMENTS, pos )
                );
            }
        }

        /* 点击事件 */
        //喜欢
        holder.getLikeBtn().setOnClickListener(v ->
                onCommentsClickBtnListener( v, data, ClickType.LIKE, pos )
        );
        //回复
        holder.getReplyMsgBtn().setOnClickListener(v ->
                onCommentsClickBtnListener( v, data, ClickType.REPLY_MSG, pos )
        );
        //其他
        holder.getMoreBtn().setOnClickListener(v ->
                onCommentsClickBtnListener( v, data, ClickType.MORE, pos )
        );

        if( mMainPos == -1 ) {
            RecyclerView.LayoutParams lpItemView = (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
            lpItemView.bottomMargin = pos == getItemCount() - 1 ?
                    Utils.dp2Px( getContext( holder ), 10 ) :
                    0;
        }else {
            FrameLayout.LayoutParams lpDiv = (FrameLayout.LayoutParams) holder.getDiv().getLayoutParams();
            lpDiv.bottomMargin = pos == getItemCount() - 1 ?
                    0 :
                    Utils.dp2Px( getContext( holder ), 10 );
        }
    }

    @Override
    public int getItemViewType(int position) {
        //0：常规消息，1：热门，最新按钮
        return mMainPos == -1 && position == 0 ? 1 : 0;
    }

    private String getContent(@NonNull String userName, @Nullable String targetName, String content,
                              boolean isMain) {
        String nameStyle = "<b><font color='#333333'>%s</font></b>";
        //不带@和带@
        String showName = TextUtils.isEmpty( targetName ) ?
                userName + ": " : userName + "<br>@" + targetName + ": ";
        //一级评论和二级评论
        nameStyle = String.format( nameStyle, isMain ? "" : showName );
        //评论内容
        return nameStyle + String.format(
                "<font color='#666666' size='%s'>%s</font>",
                isMain ? 14 : 12,
                content
        );
    }

    private void onCommentsClickBtnListener(View v, ItemData data, @ClickType int type, int pos) {
        if( mOnCommentsClickBtnListener == null ) return;
        mOnCommentsClickBtnListener.onClick( v, mMainItemData, data, type, mMainPos, pos );
    }

    public void setOnCommentsClickBtnListener(OnCommentsClickBtnListener l) {
        this.mOnCommentsClickBtnListener = l;
    }

    private RadioGroup.OnCheckedChangeListener mOnCommentsTypeCheckedChangeListener;
    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener l) {
        mOnCommentsTypeCheckedChangeListener = l;
    }

    public static class CommentHolder extends BaseViewHolder {
        private final LinearLayout llUserInfo;
        private final ShapeImageView llPhoto;
        private final ImageView ivVipIcon;
        private final TextView tvUserName;
        private final TextView tvContent;
        private final TextView tvMoreContentBtn;
        private final TextView tvLikeBtn;
        private final ImageView ivReplyMsgBtn;
        private final ImageView ivMoreBtn;
        private final TextView tvTime;
        private final View vMainDiv;
        private final View vDiv;
        private final TextView tvViewCommentsBtn;
        private final RecyclerView rvChildListView;

        public CommentHolder(@NonNull ViewGroup parent, int itemRes, boolean isChildLayout) {
            super( parent, itemRes );

            llUserInfo = findViewById( R.id.item_comment_ll_user_info );
            llPhoto = findViewById( R.id.item_comment_siv_photo );
            ivVipIcon = findViewById( R.id.item_comment_iv_vip_icon );
            tvUserName = findViewById( R.id.item_comment_tv_user_name );
            tvContent = findViewById( R.id.item_comment_tv_content );
            tvMoreContentBtn = findViewById( R.id.item_comment_tv_more_content_btn );
            tvLikeBtn = findViewById( R.id.item_comment_tv_like_btn );
            ivReplyMsgBtn = findViewById( R.id.item_comment_iv_reply_msg_btn );
            ivMoreBtn = findViewById( R.id.item_comment_iv_more_btn );
            tvTime = findViewById( R.id.item_comment_tv_time );
            vMainDiv = findViewById( R.id.item_comment_v_main_div );
            vDiv = findViewById( R.id.item_comment_v_div );
            tvViewCommentsBtn = findViewById( R.id.item_comment_tv_view_comments_btn );
            rvChildListView = findViewById( R.id.item_comment_rv_child_list_view );

            /* 子布局下的处理 */
            //显示/隐藏用户头像
            llUserInfo.setVisibility( isChildLayout ? View.GONE : View.VISIBLE );
            //交互区设置/取消左右边距
            LinearLayout llInteractive = findViewById( R.id.item_comment_ll_interactive );
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llInteractive.getLayoutParams();
            lp.setMarginStart( Utils.dp2Px( getContext(), isChildLayout ? 0 : 62 ) );
            lp.setMarginEnd( Utils.dp2Px( getContext(), isChildLayout ? 0 : 20 ) );
            //显示/隐藏子列表
            rvChildListView.setVisibility( isChildLayout ? View.VISIBLE : View.GONE );
        }

        public boolean isShowUserInfo() {
            return llUserInfo.getVisibility() == View.VISIBLE;
        }

        /**
         启用/禁用更多评论按钮
         @param type  0：隐藏全部，1：显示Div，2：显示ViewComments
         */
        public void setLastChildItemDiv(int type) {
            vDiv.setVisibility( type == 1 ? View.VISIBLE : View.GONE );
            tvViewCommentsBtn.setVisibility( type == 2 ? View.VISIBLE : View.GONE );
        }

        public ShapeImageView getPhoto() { return llPhoto; }
        public ImageView getVipIcon() { return ivVipIcon; }
        public TextView getUserName() { return tvUserName; }
        public TextView getContent() { return tvContent; }
        public TextView getMoreContentBtn() { return tvMoreContentBtn; }
        public TextView getLikeBtn() { return tvLikeBtn; }
        public ImageView getReplyMsgBtn() { return ivReplyMsgBtn; }
        public ImageView getMoreBtn() { return ivMoreBtn; }
        public TextView getTime() { return tvTime; }
        public View getDiv() { return vDiv; }
        public View getMainDiv() { return vMainDiv; }
        public TextView getViewCommentsBtn() { return tvViewCommentsBtn; }
        public RecyclerView getChildListView() { return rvChildListView; }
    }

    public static class CommentTypeHolder extends BaseViewHolder {
        public CommentTypeHolder(@NonNull ViewGroup parent, int itemRes) {
            super( parent, itemRes );
        }

        public RadioGroup getTable() { return (RadioGroup) itemView; }
    }

    public static class CommentData implements IItemData {
        private int itemTotalCount;
        private int currentPage;
        private int lastPage;
        @NonNull
        private final List<ItemData> itemList = new ArrayList<>();

        public CommentData() { }

        @NonNull
        @Override
        public String toString() {
            return "CommentData{" +
                    "itemTotalCount=" + itemTotalCount +
                    ", currentPage=" + currentPage +
                    ", lastPage=" + lastPage +
                    ", itemList=" + Arrays.toString( itemList.toArray() ) +
                    '}';
        }

        public void setCommentData(@NonNull CommentData data, int pos) {
            itemTotalCount = data.itemTotalCount;
            currentPage = data.currentPage;
            lastPage = data.lastPage;
            List<ItemData> dataItemList = data.getItemList();
            if( pos > -1 && pos < itemList.size() ) {
                itemList.addAll( pos, dataItemList );
            }else {
                itemList.addAll( dataItemList );
            }
        }

        public void clearCommentData() {
            itemList.clear();
        }

        public void setCommentData(@NonNull CommentData data) {
            setCommentData( data, -1 );
        }

        public int getItemTotalCount() { return itemTotalCount; }
        public void setItemTotalCount(int itemTotalCount) { this.itemTotalCount = itemTotalCount; }

        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

        public int getLastPage() { return lastPage; }
        public void setLastPage(int lastPage) { this.lastPage = lastPage; }

        @NonNull
        public List<ItemData> getItemList() { return itemList; }
        public void setItemList(List<ItemData> itemList) { this.itemList.addAll( itemList ); }
    }

    public static class ItemData implements IItemData {
        private int talkId;
        private long userId;
        private long discoverArticleId;
        private String userName;
        private String photoUrl;
        private int targetTalkId;
        private long targetUserId;
        private String targetNickname;
        private boolean isVip;
        private int vipStatus;
        private String content;
        private int likeCount;
        private boolean isLike;
        private long timeStamp;
        private final CommentData replyMsgData = new CommentData();

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "talkId=" + talkId +
                    ", userId=" + userId +
                    ", discoverArticleId=" + discoverArticleId +
                    ", userName='" + userName + '\'' +
                    ", photoUrl='" + photoUrl + '\'' +
                    ", targetTalkId=" + targetTalkId +
                    ", targetUserId=" + targetUserId +
                    ", targetNickname='" + targetNickname + '\'' +
                    ", isVip=" + isVip +
                    ", vipStatus=" + vipStatus +
                    ", content='" + content + '\'' +
                    ", likeCount=" + likeCount +
                    ", isLike=" + isLike +
                    ", timeStamp=" + timeStamp +
                    ", replyMsgData=" + replyMsgData +
                    '}';
        }

        public int getTalkId() { return talkId; }
        public void setTalkId(int talkId) { this.talkId = talkId; }

        public long getUserId() { return userId; }
        public void setUserId(long userId) { this.userId = userId; }

        public long getDiscoverArticleId() { return discoverArticleId; }
        public void setDiscoverArticleId(long id) { discoverArticleId = id; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

        public int getTargetTalkId() { return targetTalkId; }
        public void setTargetTalkId(int targetTalkId) { this.targetTalkId = targetTalkId; }

        public long getTargetUserId() { return targetUserId; }
        public void setTargetUserId(long targetUserId) { this.targetUserId = targetUserId; }

        public String getTargetNickname() { return targetNickname; }
        public void setTargetNickname(String name) { targetNickname = name; }

        public boolean isVip() { return isVip; }
        public void setVip(boolean vip) { isVip = vip; }

        public int getVipStatus() { return vipStatus; }
        public void setVipStatus(int vipStatus) { this.vipStatus = vipStatus; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public int getLikeCount() { return likeCount; }
        public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

        public boolean isLike() { return isLike; }
        public void setLike(boolean like) { isLike = like; }

        public long getTimeStamp() { return timeStamp; }
        public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

        public CommentData getReplyMsgData() { return replyMsgData; }
        public void setReplyMsgData(CommentData data) {
            replyMsgData.setCommentData( data );
        }
        public void addReplyMsgData(List<ItemData> list) {
            replyMsgData.getItemList().addAll( list );
        }
        @NonNull
        public List<ItemData> getReplyMsgList() {
            return replyMsgData.getItemList();
        }
    }
}

package com.gjjy.discoverylib.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.holder.BaseFindHolder;
import com.gjjy.discoverylib.adapter.holder.ListenDailyMainHolder;
import com.gjjy.discoverylib.adapter.holder.ListenDailyMoreHolder;
import com.gjjy.discoverylib.adapter.holder.MyCourseHolder;
import com.gjjy.discoverylib.adapter.holder.PopularVideosMainHolder;
import com.gjjy.discoverylib.adapter.holder.PopularVideosMoreHolder;
import com.gjjy.discoverylib.utils.StartUtil;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 每日聆听、热门视频、我的课程适配器
 */
public class DiscoveryListAdapter extends
        BaseRecyclerViewAdapter<DiscoveryListAdapter.ItemData, BaseFindHolder> {

    @IntDef({ ListType.MAIN_LIST, ListType.MORE_LIST, ListType.MY_COURSE_LIST })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ListType {
        int MAIN_LIST = 0;
        int MORE_LIST = 1;
        int MY_COURSE_LIST = 2;
    }

    private final BaseActivity mActivity;
    @NonNull
    private final RequestManager mGlide;
    private final RequestOptions mRequestOptions = new RequestOptions()
            .transform( new CenterCrop(), new RoundedCorners( 10 ) );
    @ListType
    private final int mListType;
    private final String mUid;
    private final String mUserName;
    private boolean mIsVip;

    public DiscoveryListAdapter(BaseActivity activity, @NonNull RequestManager glide,
                                @ListType int type,
                                @NonNull List<ItemData> list,
                                String uid,
                                String name) {
        super(list);
        mActivity = activity;
        mGlide = glide;
        mListType = type;
        setEnableTouchStyle( false );
        mUid = uid;
        mUserName = name;
    }

    @NonNull
    @Override
    public BaseFindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch ( viewType ) {
            case 1:         //热门视频
                return getPopularVideosHolder( parent );
            case 2:         //我的课程
                return getMyCourseHolder( parent );
            default:        //每日聆听
                return getListenDailyHolder( parent );
        }
    }

    private BaseFindHolder getListenDailyHolder(@NonNull ViewGroup parent) {
        return mListType == ListType.MAIN_LIST ?
                new ListenDailyMainHolder( parent, R.layout.item_listen_daily_list ) :
                new ListenDailyMoreHolder( parent, R.layout.item_listen_daily_more_list );
    }

    private BaseFindHolder getPopularVideosHolder(@NonNull ViewGroup parent) {
        return mListType == ListType.MAIN_LIST ?
                new PopularVideosMainHolder( parent, R.layout.item_popular_videos_list ) :
                new PopularVideosMoreHolder( parent, R.layout.item_popular_videos_more_list );
    }

    private BaseFindHolder getMyCourseHolder(@NonNull ViewGroup parent) {
        return new MyCourseHolder( parent, R.layout.item_course_list );
    }

    @Override
    public void onBindViewHolder(@NonNull BaseFindHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        boolean isNew = data.isNew();
        boolean isVip = data.isVip();

        //是否为新内容
        if( holder.getNewFlag() != null ) {
            int resId = 0;
            switch( ( isNew ? 1 : 0 ) + ( isVip ? 1 : 0 ) ) {
                case 1:         //new or vip or free（true, false | false, true）
                    resId = isNew ? R.drawable.ic_new_flag : R.drawable.ic_vip_flag;
                    break;
                case 2:         //new + vip（true, true）
                    resId = R.drawable.ic_new_and_vip_flag;
                    break;
                default:        //不显示
                    break;
            }

            if( resId != 0 ) holder.getNewFlag().setImageResource( resId );
            holder.getNewFlag().setVisibility( resId == 0 ? View.GONE : View.VISIBLE );
        }

        //图片
        if( holder.getImg() != null ) {
            String imgUrl = data.getImgUrl();
            if( !TextUtils.isEmpty( imgUrl ) ) {
                int loadWaitResId = R.drawable.ic_discovery_square_img_load_wait_bg;
                mGlide.load( imgUrl ).apply( mRequestOptions )
                        .placeholder( loadWaitResId ).fallback( loadWaitResId ).error( loadWaitResId )
                        .into( holder.getImg() );
            }
        }
        //查看人数
        if( holder.getToViewCount() != null ) {
            holder.getToViewCount().setText( ObjUtils.toNumberSize( data.getToViewCount() ) );
        }
        //标题
        if( holder.getTitle() != null ) {
            String title = data.getTitle();
            if( !TextUtils.isEmpty( title ) ) holder.getTitle().setText( title );
        }
        //时长
        if( holder.getDuration() != null ) {
            holder.getDuration().setText( data.getTime() );
        }
        //内容
        if( holder.getContent() != null ) {
            String content = data.getContent();
            if( !TextUtils.isEmpty( content ) ) holder.getContent().setText( content );
        }
        //分类标签
        if( holder.getClassifyTag() != null ) {
            String classifyTag = data.getClassifyTag();
            if( !TextUtils.isEmpty( classifyTag ) ) holder.getClassifyTag().setText( classifyTag );
        }

        //我的课程
        if( holder instanceof MyCourseHolder ) {
            MyCourseHolder courseHolder = (MyCourseHolder) holder;
            //视频会有一个播放按钮
            courseHolder.getPlayImg()
                    .setVisibility( data.getTypeId() == 1002 ? View.VISIBLE : View.GONE );

            TextView tvDur = courseHolder.getDuration();
            //图标
            tvDur.setCompoundDrawablesWithIntrinsicBounds(
                    data.getTypeId() == 1001 ?
                            R.drawable.ic_course_list_listen_daily_progress :
                            R.drawable.ic_course_list_popular_videos_progress,
                    0, 0, 0
            );
            //进度百分比
            tvDur.setText( data.getTimeProgress() );
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<BaseFindHolder> adapter,
                            View v, ItemData data, int position) {
        super.onItemClick(adapter, v, data, position);
        if( !mIsVip && data.isVip() ) {
            if( mActivity == null ) return;
            mActivity.showUnlockVipDialog(
                    mUid, mUserName, data.getTypeId(), data.getId(), data.getTitle(),
                    data.getUnlockImgUrl(), data.getTitle(), data.getExplain()
            );
            return;
        }

        switch( data.getTypeId() ) {
            case 1001:      //每日聆听
                StartUtil.startListenDailyDetailsActivity( data.getId(),data.getVideoId(), data.getTitle() );
                break;
            case 1002:      //热门视频
                StartUtil.startPopularVideosDetailsActivity( data.getId(), data.getVideoId(),data.getTitle() );
                break;
            case 1003:      //专项学习
                StartUtil.startTargetedLearningDetailsActivity( data.getId() ,data.getVideoId());
                break;
        }
        LogUtil.e("DiscoveryListAdapter -> onItemClick -> data:" + data);
    }

    @Override
    public int getItemViewType(int position) {
        long typeId = 1001;
        try {
            typeId = getDataList().get( position ).getTypeId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 0：每日聆听，1：热门视频，2：我的课程
        return mListType == ListType.MY_COURSE_LIST ? 2 : typeId == 1001 ? 0 : 1;
    }

    public void setVip(boolean mIsVip) { this.mIsVip = mIsVip; }

    public static class ItemData implements IItemData {
        private long id;
        private int typeId;
        private boolean isNew;
        private boolean isVip;
        private String imgUrl;
        private String unlockImgUrl;
        private double toViewCount;
        private String title;
        private String content;
        private String explain;
        private String time;
        private String timeProgress;
        private String classifyTag;
        private String videoId;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", typeId=" + typeId +
                    ", isNew=" + isNew +
                    ", isVip=" + isVip +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", unlockImgUrl='" + unlockImgUrl + '\'' +
                    ", toViewCount=" + toViewCount +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", explain='" + explain + '\'' +
                    ", time='" + time + '\'' +
                    ", timeProgress='" + timeProgress + '\'' +
                    ", classifyTag='" + classifyTag + '\'' +
                    ", videoId='" + videoId + '\'' +
                    '}';
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExplain() {
            return explain;
        }

        public ItemData setExplain(String explain) {
            this.explain = explain;
            return this;
        }

        public String getClassifyTag() {
            return classifyTag;
        }

        public void setClassifyTag(String classifyTag) {
            this.classifyTag = classifyTag;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public boolean isNew() { return isNew; }
        public void setNew(boolean aNew) { isNew = aNew; }

        public boolean isVip() { return isVip; }
        public void setVip(boolean vip) { isVip = vip; }

        public String getImgUrl() { return imgUrl; }
        public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

        public String getUnlockImgUrl() { return unlockImgUrl; }
        public void setUnlockImgUrl(String unlockImgUrl) { this.unlockImgUrl = unlockImgUrl; }

        public double getToViewCount() { return toViewCount; }
        public void setToViewCount(double toViewCount) { this.toViewCount = toViewCount; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getTimeProgress() { return timeProgress; }

        public void setTimeProgress(String timeProgress) { this.timeProgress = timeProgress; }
    }
}

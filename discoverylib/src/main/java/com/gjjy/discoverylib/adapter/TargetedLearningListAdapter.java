package com.gjjy.discoverylib.adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gjjy.discoverylib.adapter.holder.TargetedLearningHolder;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.discoverylib.R;

import java.util.List;

/**
 * 专项学习列表适配器
 */
public class TargetedLearningListAdapter extends BaseRecyclerViewAdapter<TargetedLearningListAdapter.ItemData, TargetedLearningHolder> {

    @NonNull
    private final RequestManager mGlide;

    public TargetedLearningListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public TargetedLearningHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TargetedLearningHolder( parent, R.layout.item_targeted_learning );
    }

    @Override
    public void onBindViewHolder(@NonNull TargetedLearningHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        boolean isNew = data.isNew();
        boolean isVip = data.isVip();

        //是否为新内容
        if( holder.getNewFlag() != null ) {
            int resId = 0;
            switch( ( isNew ? 1 : 0 ) + ( isVip ? 1 : 0 ) ) {
                case 0:         //free（false, false）
                    resId = R.drawable.ic_free_flag;
                    break;
                case 1:         //new or vip or free（true, false | false, true）
                    resId = isNew ? R.drawable.ic_new_and_free_flag : R.drawable.ic_vip_flag;
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
                int loadWaitResId = R.drawable.ic_discovery_rect_img_load_wait_bg;
                mGlide.load( imgUrl )
                        .placeholder( loadWaitResId ).fallback( loadWaitResId ).error( loadWaitResId )
                        .into( new DrawableImageViewTarget( holder.getImg() ) {
                    @Override
                    public void onResourceReady(@NonNull Drawable res, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady( res, transition );
                        res.setColorFilter(getResources( holder ).getColor( R.color.colorD5 ), PorterDuff.Mode.MULTIPLY);
                    }
                } );
            }
        }
        //标题
        if( holder.getTitle() != null ) {
            String title = data.getTitle();
            if( !TextUtils.isEmpty( title ) ) holder.getTitle().setText( title );
        }
        //内容
        if( holder.getContent() != null ) {
            String content = data.getContent();
            if( !TextUtils.isEmpty( content ) ) holder.getContent().setText( content );
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<TargetedLearningHolder> adapter, View v, ItemData data, int position) {
        super.onItemClick(adapter, v, data, position);
//        if( !mIsVip && data.isVip() ) {
//            if( mActivity == null ) return;
//            mActivity.showUnlockVipDialog( data.getUnlockImgUrl(), data.getTitle(), data.getContent() );
//            return;
//        }
//        //每日聆听
//        if( data.getTypeId() == 1001 ) {
//            StartUtil.startListenDailyDetailsActivity( data.getId(), data.getTitle() );
//            return;
//        }
//        StartUtil.startPopularVideosDetailsActivity( data.getId(), data.getTitle() );
    }

    public static class ItemData implements IItemData {
        private long id;
        private boolean isNew;
        private boolean isVip;
        private String imgUrl;
        private String title;
        private String content;
        private String videoId;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", isNew=" + isNew +
                    ", isVip=" + isVip +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", videoId='" + videoId + '\'' +
                    '}';
        }

        public long getId() {
            return id;
        }

        public ItemData setId(long id) {
            this.id = id;
            return this;
        }

        public boolean isNew() {
            return isNew;
        }

        public ItemData setNew(boolean aNew) {
            isNew = aNew;
            return this;
        }

        public boolean isVip() {
            return isVip;
        }

        public ItemData setVip(boolean vip) {
            isVip = vip;
            return this;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public ItemData setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public ItemData setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getContent() {
            return content;
        }

        public ItemData setContent(String content) {
            this.content = content;
            return this;
        }

        public String getVideoId() {
            return videoId;
        }

        public ItemData setVideoId(String videoId) {
            this.videoId = videoId;
            return this;
        }
    }
}

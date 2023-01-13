package com.gjjy.discoverylib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.gjjy.discoverylib.R;

import java.util.ArrayList;
import java.util.List;

public class VerticalScrollSubtitlesView extends FrameLayout {
    public interface OnPositionChangedListener {
        void onPosition(int pos, int dur, boolean fromUser);
    }

    private RecyclerView rvListView;

    private VssAdapter mAdapter;
    private int mRvScrollY;
    private int mLastItemHeight = 0;
    private boolean isInterceptScroll = false;
    
    private OnPositionChangedListener mOnPositionChangedListener;

    public VerticalScrollSubtitlesView(@NonNull Context context) {
        this( context, null );
    }

    public VerticalScrollSubtitlesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public VerticalScrollSubtitlesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent( ev );
        int action = ev.getAction();
        isInterceptScroll = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;
        return true;
    }

    private void init() {
        addView( createListView() );

        initData();
        initListener();
    }

    private void initData() {
        rvListView.setItemViewCacheSize( 6 );
        mAdapter = new VssAdapter( new ArrayList<>() );
        rvListView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        rvListView.setAdapter( mAdapter );
    }
    
    private void initListener() {
        rvListView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                mRvScrollY += dy;
            }
        });
        //列表点击事件
        mAdapter.setOnItemClickListener( (adapter, view, data, i) ->
                setProgress( data.getDuration(), true )
        );
    }

    /**
     Item之间的间隔高度
     @return        高度
     */
    private int getItemDecorationSpace() {
        return ( (SpaceItemDecoration) rvListView.getItemDecorationAt( 0 ) ).getSpace();
    }

    private RecyclerView createListView() {
        rvListView = new RecyclerView( getContext() );
        ViewGroup.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rvListView.setLayoutParams( lp );
        rvListView.setOverScrollMode( OVER_SCROLL_NEVER );
        rvListView.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( getContext(), 20 ) ) );
        RecyclerView.ItemAnimator ia = rvListView.getItemAnimator();
        if( ia != null ) ia.setChangeDuration( 0 );
        ((SimpleItemAnimator) rvListView.getItemAnimator()).setSupportsChangeAnimations( false );
        return rvListView;
    }

    public void setData(List<VssAdapter.ItemData> data) {
        mAdapter.clearItemData();
        mAdapter.addItemData( data );
        mAdapter.notifyDataSetChanged();
    }

    private int toProgress(long dur) {
        return Math.round( dur / 1000F ) * 1000;
    }

    public void setProgress(long progress, boolean fromUser) {
        int roundPos = toProgress( progress );   //取整;
        int findPos = -1;
        for( int i = 0; i < mAdapter.getItemCount(); i++ ) {
            VssAdapter.ItemData data = mAdapter.getItemData( i );
            if( data == null ) continue;
            int durProgress = toProgress( data.getDuration() );
            int offset = 500;
            if( durProgress - offset <= roundPos && durProgress + offset >= roundPos ) {
                findPos = i;
                break;
            }
        }
//        LogUtil.e( "setProgress -> " +
//                "roundPos:" + roundPos + " | " +
//                "progress:" + progress + " | " +
//                "findPos:" + findPos + " | " +
//                "count:" + mAdapter.getItemCount()
//        );
        if( findPos != -1 ) setPosition( findPos, fromUser );
    }

    public void setPosition(int pos, boolean fromUser) {
         VssAdapter.ItemData data = mAdapter.getItemData( pos );
        //存在手势操作时跳过滑动
        if( isInterceptScroll ) {
            doPositionChanged( pos, data, fromUser );
            return;
        }

        //屏幕上一半Item的数量
        int holderHalfCount = mAdapter.getHolderCount() / 2;
        int height = ( height = data.getItemHeight() ) == 0 ? mLastItemHeight : height;

        if( height == 0 ) {
            /* 采用下标的方式滑动位置（解决首次滑动时，item处于复用或未创建阶段（不是从下标0先开始的）） */
            int toPos = pos + holderHalfCount;
            rvListView.smoothScrollToPosition(
                    toPos >= mAdapter.getItemCount() ? mAdapter.getItemCount() - 1 : toPos
            );
        }else {
            //前几个和后几个都不用滑动
            boolean isScroll = pos > holderHalfCount - 1 || mRvScrollY > ( height * pos ) / 2;
            if( height > 0 && isScroll ) {
                int space = getItemDecorationSpace();
                if( pos == 0 ) space += space;
                int halfHeight = ( rvListView.getHeight() / 2 ) - ( height / 2 );
                rvListView.smoothScrollBy(
                        0,
                        ( ( height + space ) * pos ) - mRvScrollY - halfHeight,
                        new DecelerateInterpolator(),
                        300
                );
                mLastItemHeight = height;
            }
        }

//        LogUtil.e( "setPosition -> " +
//                "h:" + height + " | " +
//                "pos:" + pos + " | " +
//                "rvY:" + mRvScrollY + " | " +
//                "dy:" + ( ( data.getItemHeight() * pos ) - mRvScrollY ) + " | " +
//                "hCount:" + mAdapter.getHolderCount()
//        );

        doPositionChanged( pos, data, fromUser );
    }

    private void doPositionChanged(int pos, VssAdapter.ItemData data, boolean fromUser) {
        post( () -> mAdapter.setCurrentShowType( pos ) );
        if( mOnPositionChangedListener != null ) {
            mOnPositionChangedListener.onPosition(
                    pos, data == null ? 0 : (int) data.getDuration(), fromUser
            );
        }
    }

    public void setOnPositionChangedListener(OnPositionChangedListener l) {
        this.mOnPositionChangedListener = l;
    }

    public static class VssAdapter
            extends BaseRecyclerViewAdapter<VssAdapter.ItemData, VssAdapter.VssHolder> {
        private int mCurrentPosition = 0;
        public VssAdapter(@NonNull List<ItemData> list) {
            super( list );
        }

        @NonNull
        @Override
        public VssHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if( viewType == 0 ) return new VssHolder( parent, R.layout.item_vss );
            //图片
            Context context = parent.getContext();
            ImageView iv = new ImageView( context );
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = Utils.dp2Px( context, 15 );
            lp.setMarginStart( Utils.dp2Px( context, 20 ) );
            lp.setMarginEnd( Utils.dp2Px( context, 20 ) );
            iv.setLayoutParams( lp );
            iv.setMinimumHeight( Utils.dp2Px( context, 130 ) );
            return new VssHolder( iv );
        }

        @Override
        public void onBindViewHolder(@NonNull VssHolder holder, int position) {
            super.onBindViewHolder( holder, position );
            ItemData data = getItemData( position );
            if( data == null ) return;

            //顶部大图
            if( !TextUtils.isEmpty( data.getTopImgUrl() ) ) {
                ImageView iv = (ImageView) holder.getItemView();
                Glide.with( iv ).load( data.getTopImgUrl() ).into( iv );
                return;
            }
            //字幕
            View itemView = holder.getItemView();
            int width = itemView.getWidth();
            int height = itemView.getHeight();
            if( width == 0 || height == 0 ) {
                itemView.measure( 0, 0 );
                width = itemView.getMeasuredWidth();
                height = itemView.getMeasuredHeight();
            }
            data.setItemWidth( width );
            data.setItemHeight( height );

            TextView tvPinyin = holder.getPinyin();
            TextView tvChineseChar = holder.getChineseChar();
            TextView tvTranslate = holder.getTranslate();

            tvPinyin.setText( data.getPinyin() );
            tvChineseChar.setText( data.getChineseChar() );
            tvTranslate.setText( data.getTranslate() );

            Resources res = getResources( holder );

            int defTextColor;
            if( position == 0 ) {
                defTextColor = res.getColor( R.color.color33 );
                tvPinyin.setTextColor( defTextColor );
                tvChineseChar.setTextColor( defTextColor );
                tvTranslate.setTextColor( res.getColor( R.color.color66 ) );
            }else {
                if( mCurrentPosition == position ) {              //当前展示的文本
                    int showColor = res.getColor( R.color.colorMain );
                    tvPinyin.setTextColor( showColor );
                    tvChineseChar.setTextColor( showColor );
                    tvTranslate.setTextColor( showColor );
                }else {//默认文本
                    defTextColor = res.getColor( R.color.color66 );
                    tvPinyin.setTextColor( defTextColor );
                    tvChineseChar.setTextColor( defTextColor );
                    tvTranslate.setTextColor( res.getColor( R.color.color99 ) );
                }
            }

            tvPinyin.setTextSize( position == 0 ? 14 : 12 );
            tvChineseChar.setTextSize( position == 0 ? 20 : 18 );
            tvTranslate.setTextSize( position == 0 ? 14 : 12 );

            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder
                    .getItemView()
                    .getLayoutParams();
            lp.topMargin = position == 0 ? Utils.dp2Px( getContext( holder ), 20 ) : 0;
            lp.bottomMargin = position == getItemCount() - 1 ? ( getHolderCount() / 2 ) * height : 0;
        }

        @Override
        public int getItemViewType(int position) {
            ItemData data = getItemData( position );
            //0：字幕，1：图片
            return data != null && TextUtils.isEmpty( data.getTopImgUrl() ) ? 0 : 1;
        }

        public void setCurrentShowType(int pos) {
            if( pos < 0 || pos >= getItemCount() ) return;
            mCurrentPosition = pos;
            notifyDataSetChanged();
        }

        public static class VssHolder extends BaseViewHolder {
            private final TextView tvPinyin;
            private final TextView tvChineseChar;
            private final TextView tvTranslate;

            public VssHolder(@NonNull ViewGroup parent, int itemRes) {
                super( parent, itemRes );
                tvPinyin = findViewById( R.id.item_vss_tv_pinyin );
                tvChineseChar = findViewById( R.id.item_vss_tv_chinese_char );
                tvTranslate = findViewById( R.id.item_vss_tv_translate );
            }

            public VssHolder(@NonNull ImageView itemView) {
                super( itemView );
                tvPinyin = tvChineseChar = tvTranslate = null;
            }

            public TextView getPinyin() { return tvPinyin; }

            public TextView getChineseChar() { return tvChineseChar; }

            public TextView getTranslate() { return tvTranslate; }
        }

        public static class ItemData implements IItemData {
            private String pinyin;
            private String chineseChar;
            private String translate;
            private long duration;

            private int itemWidth;
            private int itemHeight;
            private String topImgUrl;

            @NonNull
            @Override
            public String toString() {
                return "ItemData{" +
                        "pinyin='" + pinyin + '\'' +
                        ", chineseChar='" + chineseChar + '\'' +
                        ", translate='" + translate + '\'' +
                        ", duration=" + duration +
                        ", itemWidth=" + itemWidth +
                        ", itemHeight=" + itemHeight +
                        ", topImgUrl=" + topImgUrl +
                        '}';
            }

            public String getPinyin() { return pinyin; }
            public void setPinyin(String pinyin) { this.pinyin = pinyin; }

            public String getChineseChar() { return chineseChar; }
            public void setChineseChar(String chineseChar) { this.chineseChar = chineseChar; }

            public String getTranslate() { return translate; }
            public void setTranslate(String translate) { this.translate = translate; }

            public long getDuration() { return duration; }
            public void setDuration(long duration) { this.duration = duration; }

            public int getItemWidth() { return itemWidth; }
            public void setItemWidth(int itemWidth) { this.itemWidth = itemWidth; }

            public int getItemHeight() { return itemHeight; }
            public void setItemHeight(int itemHeight) { this.itemHeight = itemHeight; }

            public String getTopImgUrl() { return topImgUrl; }
            public void setTopImgUrl(String topImgUrl) { this.topImgUrl = topImgUrl; }
        }
    }
}

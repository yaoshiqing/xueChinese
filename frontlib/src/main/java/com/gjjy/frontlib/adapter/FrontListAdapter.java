package com.gjjy.frontlib.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.holder.ChildItemHolder;
import com.gjjy.frontlib.adapter.holder.FrontListHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.gjjy.frontlib.widget.FrontTestButton;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.widget.MaskImageView;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首页列表适配器
 */
public class FrontListAdapter
        extends BaseRecyclerViewAdapter<FrontListAdapter.ItemData, FrontListHolder> {
//    public @interface TestStatus {
//        int LOCK = 0;          //未解锁
//        int UNLOCK = 1;        //已解锁
//        int COMPLETE = 2;      //已完成
//    }

    public interface OnTestItemClickListener {
        void onItemClick(RecyclerView.Adapter<FrontListHolder> adapter,
                         View v, View itemView, ItemData data, int position);
    }

    public interface OnChildItemClickListener {
        void onClick(View v, View itemView, ItemData data, ChildItem childData,
                     int position, int itemPosition, boolean result);
    }

//    private int[] mPresetColors;
//    private int mColorIndex = 0;

    private final RequestManager mGlide;
    private OnChildItemClickListener mOnChildItemClickListener;
    private OnTestItemClickListener mOnTestItemClickListener;

    public FrontListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
    }

    @NonNull
    @Override
    public FrontListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        switch ( viewType ) {
            case 0:
                layout = R.layout.item_front_list_0;
                break;
            case 1:
                layout = R.layout.item_front_list_1;
                break;
            case 2:
                layout = R.layout.item_front_list_2;
                break;
            case 3:
                layout = R.layout.item_front_list_3;
                break;
            case 4:
                layout = R.layout.item_front_list_4;
                break;
            case 5:
                layout = R.layout.item_front_list_5;
                break;
            case 6:
                layout = R.layout.item_front_list_6;
                break;
            case 7:
                layout = R.layout.item_front_list_7;
                break;
            case 8:
                layout = R.layout.item_front_list_8;
                break;
            case 9:
                layout = R.layout.item_front_list_9;
                break;
            default:
                layout = R.layout.item_front_list_10;
                break;
        }

        if( mLockUnitColorFilter == null ) {
            mLockUnitColorFilter = new PorterDuffColorFilter(
                    parent.getResources().getColor( R.color.colorD8 ),
                    PorterDuff.Mode.MULTIPLY
            );
        }

        return new FrontListHolder( parent, layout );
    }


    private PorterDuffColorFilter mLockUnitColorFilter;

    private void bindChildItemViewHolder(ChildItemHolder holder, int mainPos, int childPos,
                                         ItemData mainData) {
        if( holder == null || mainData == null ) return;
        Context context = holder.getContext();
        ChildItem data = mainData.getData().get( mainPos );
        boolean isLockUnit = data.getUnitStatus() == 0;
        //子项图标链接
        String url = data.getImgUrl();
        //子项标题
        String title = data.getTitle();
        int iconWidth = Utils.dp2Px( context, 79 );
        int iconHeight = Utils.dp2Px( context, 74 );
        MaskImageView mivImg = holder.getImg();
        mivImg.setImageColorFilter( isLockUnit ? mLockUnitColorFilter : null );
        if( url != null ) {
            if( url.startsWith( "http" ) ) {
                mGlide.load( url ).into( new DrawableImageViewTarget( mivImg ) {
                    @Override
                    public void onResourceReady(@NonNull Drawable res,
                                                @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady( res, transition );
                        mivImg.setImageDrawable( res, iconWidth, iconHeight );
                    }
                });
            }else {
                try {
                    mivImg.setImageDrawable(
                            ResUtil.getDrawable( holder.getContext(), url ),
                            iconWidth,
                            iconHeight
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            mivImg.setImageResource( data.getImgResId(), iconWidth, iconHeight );
        }
        //当前进度
        mivImg.startAnimOfProgressTo(
                (int)( ( data.getSectionNumOfDouble() ) / data.getSectionIdsSize() * 100D )
        );


        if( title != null ) holder.getTitle().setText( title );

        holder.getReward().setImageResource(isLockUnit ?
                R.drawable.ic_front_child_knot_lock :
                R.drawable.ic_front_child_knot
        );
        //奖励显示状态
        holder.getReward().setVisibility( data.isComplete() ? View.INVISIBLE : View.VISIBLE );

        //子项点击事件
        View itemView = holder.getItemView();
        itemView.setFocusable( true );
        itemView.setClickable( true );
        itemView.setOnClickListener(v -> {
            LogUtil.e( data.toString() );
            if( mOnChildItemClickListener == null ) return;
            mOnChildItemClickListener.onClick(
                    holder.getItemView(), itemView, mainData, data,
                    childPos, mainPos, !isLockUnit
            );
        });
    }

    private void bindBgRes(@NonNull FrontListHolder holder, @NonNull ItemData data, int itemCount) {
        ViewGroup itemView = (ViewGroup) holder.getItemView();
        if( data.isIntroduce() ) {
            //引导模块
            itemView.setBackgroundResource( R.drawable.ic_front_list_1 );
        }else {
            int strResId = 0;
            if( itemCount <= 3 ) {
                /* 1~3个item */
                strResId = R.drawable.ic_front_list_123;
            }else if( itemCount <= 7 ) {
                /* 4~7个item */
                strResId = R.drawable.ic_front_list_4567;
            }else if( itemCount <= 10 ) {
                /* 8~10个item */
                strResId = R.drawable.ic_front_list_8910;
            }
            //设置背景
            if( strResId != 0 ) {
                itemView.setBackgroundResource( strResId );
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FrontListHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;
        List<ChildItem> itemList = data.getData();
        if( itemList == null || itemList.size() == 0 ) return;
        int itemCount = itemList.size();

        //绑定背景图片
        bindBgRes( holder, data, itemCount );

        //绑定子Item数据
        for (int i = 0; i < itemCount; i++) {
            ChildItemHolder ciHolder = holder.getChildItemHolder( i );
            if( ciHolder != null ) bindChildItemViewHolder( ciHolder, i, position, data );
        }

        /* 跳过引导模块 */
        if( data.isIntroduce() ) return;
        FrontTestButton htbTestBtn = holder.getTestBtn();
        int score = data.getScore();
//        boolean isClickTest = data.isEnableTestBtn() || position == 0;
//        boolean isEnableTest = data.getLevelStatus() != 2;
//        boolean isTestComplete = data.isTestComplete();
        //完成过答题后展示分数
        htbTestBtn.setScore( score );
//        //考试按钮文本
//        htbTestBtn.setText( isEnableTest ?
//                R.string.stringTestEnable :
//                R.string.stringTestDisable
//        );
        //考试按钮点击事件
        htbTestBtn.setOnClickListener(v -> {
            if( mOnTestItemClickListener == null) return;
//            int status;
//            switch( data.getLevelStatus() ) {
//                case 1:         //已解锁
//                    status = TestStatus.UNLOCK;
//                    break;
//                case 2:         //已完成
//                    status = TestStatus.COMPLETE;
//                    break;
//                default:        //未解锁
//                    status = TestStatus.LOCK;
//                    break;
//
//            }
            mOnTestItemClickListener.onItemClick(
                    this, holder.getItemView(), v, data, position
            );
        });
    }

    public void setOnChildItemClickListener(OnChildItemClickListener l) {
        mOnChildItemClickListener = l;
    }

    public void setOnTestBtnClickListener(OnTestItemClickListener l) {
        mOnTestItemClickListener = l;
    }

    @Override
    public int getItemViewType(int position) {
        ItemData data = getItemData( position );
        if( data == null || data.isIntroduce() ) return 0;
        return data.getData().size();
    }

    public static class ItemData extends BaseEntity implements IItemData {
        private String title;
        private List<ChildItem> data;
        private int levelStatus;
        private boolean testComplete;
        private boolean isIntroduce;
        private int score;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + getId() +
                    ", title=" + title +
                    ", data=" + data +
                    ", levelStatus=" + levelStatus +
                    ", isEnableTestBtn=" + isEnableTestBtn() +
                    ", isTestComplete=" + testComplete +
                    ", isIntroduce=" + isIntroduce +
                    ", score=" + score +
                    '}';
        }

        public boolean isEnableTestBtn() {
            if( data == null || data.size() == 0 ) return false;
            for( ChildItem item : data ) { if( !item.isComplete() ) return false; }
            return true;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<ChildItem> getData() { return data; }
        public void setData(List<ChildItem> data) { this.data = data; }

        public int getLevelStatus() { return levelStatus; }
        public void setLevelStatus(int levelStatus) { this.levelStatus = levelStatus; }

        public boolean isTestComplete() { return testComplete; }
        public void setTestComplete(boolean isComplete) { testComplete = isComplete; }

        public boolean isIntroduce() { return isIntroduce; }
        public void setIntroduce(boolean introduce) { isIntroduce = introduce; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    public static class ChildItem extends BaseEntity {
        private ArrayList<SectionIds> sectionIds;
        private int sectionNum;
        private String imgUrl;
        private int imgResId;
        private String title;
        private int unitStatus;
        private boolean isComplete;
        private int unlockSeconds = -1;
        private long timestamp;

        @NonNull
        @Override
        public String toString() {
            return "ChildItem{" +
                    "id=" + getId() +
                    ", sectionIds='" + (sectionIds != null ? Arrays.toString(sectionIds.toArray(new SectionIds[0])) : null) + '\'' +
                    ", sectionNum='" + sectionNum + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", imgResId='" + imgResId + '\'' +
                    ", title='" + title + '\'' +
                    ", unitStatus='" + unitStatus + '\'' +
                    ", isComplete='" + isComplete + '\'' +
                    ", unlockSeconds='" + unlockSeconds + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }

        public ArrayList<SectionIds> getSectionIds() { return sectionIds; }
        public void setSectionIds(ArrayList<SectionIds> sectionIds) { this.sectionIds = sectionIds; }
        public int getSectionIdsSize() { return sectionIds == null ? 0 : sectionIds.size(); }

        public int getSectionNum() { return sectionNum; }
        public double getSectionNumOfDouble() { return (double) sectionNum; }
        public void setSectionNum(int sectionNum) { this.sectionNum = sectionNum; }

        public String getImgUrl() { return imgUrl; }
        public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

        public int getImgResId() { return imgResId; }
        public void setImgResId(int imgResId) { this.imgResId = imgResId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public int getUnitStatus() { return unitStatus; }
        public void setUnitStatus(int unitStatus) { this.unitStatus = unitStatus; }

        public boolean isComplete() { return isComplete; }
        public void setComplete(boolean complete) { isComplete = complete; }

        public long getUnlockTimeMillis() {
            return unlockSeconds == -1 ? -1 : getTimestamp() + unlockSeconds * 1000;
        }
        public int getUnlockSeconds() { return unlockSeconds; }
        public void setUnlockSeconds(int unlockSeconds) { this.unlockSeconds = unlockSeconds; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}

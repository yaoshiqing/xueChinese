package com.gjjy.usercenterlib.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.HolderStatus;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.VouchersHolder;

import java.util.List;

/**
 * 优惠券列表适配器
 */
public class VouchersAdapter
        extends BaseRecyclerViewAdapter<VouchersAdapter.ItemData, VouchersHolder> {

    public VouchersAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle(false);
    }

    public void release() {
        for( ItemData itemData : getDataList() ) {
            CountDownTimer cdt = itemData.getCountDownTimer();
            if( cdt != null ) cdt.cancel();
        }
    }


    @NonNull
    @Override
    public VouchersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VouchersHolder( parent, R.layout.item_vouchers );
    }

    @Override
    public void onBindViewHolder(@NonNull VouchersHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if( data == null || data.getType() == Type.NONE ) return;
        int type = data.getType();

        //到期时间
        Drawable timeDrawable = getTimeDrawable( holder.getContext(), data.getDay(), type );
        if( timeDrawable != null ) holder.getTime().setImageDrawable( timeDrawable );

        //优惠券样式
        switch ( type ) {
            case Type.UNUSED:           //未使用
                holder.switchUnusedType();
                holder.getTimeInterval().setText(
                        getTimeInterval( data.getStartTime(), data.getEndTime() )
                );
                break;
            case Type.IN_USE:           //正在使用
                holder.switchInUseType(
                        data.getEndTime() - System.currentTimeMillis() >= 86400000
                );
                break;
            case Type.EXPIRED:          //已过期
                holder.switchExpiredType();
                break;
            case Type.FREE_EXPIRED:     //体验已过期
                holder.switchFreeExpiredType();
                break;
        }

        if( position == 0 ) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
            lp.topMargin = Utils.dp2Px( getContext( holder ), 15 );
        }else if( position == getItemCount() - 1 ) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.getItemView().getLayoutParams();
            lp.bottomMargin = Utils.dp2Px( getContext( holder ), 15 );
        }
    }

    @Override
    public void onHolderChange(@NonNull VouchersHolder h, int pos, @HolderStatus int status) {
        super.onHolderChange( h, pos, status );
        ItemData data = getItemData( pos );
        if( data == null ) return;
        //处理InUse
        if( data.getType() == Type.IN_USE ) {
            doHolderChangedOfInUse( h, data, pos, status );
        }
    }

    /**
     处理InUse
     @param h           Holder
     @param data        单条数据
     @param pos         下标
     @param status      Holder状态
     */
    private void doHolderChangedOfInUse(@NonNull VouchersHolder h, ItemData data,
                                        int pos, @HolderStatus int status) {
        CountDownTimer cdt;
        switch( status ) {
            case HolderStatus.ATTACHED:
                cdt = data.getCountDownTimer();
                if( cdt != null ) break;
                long time = data.getEndTime() - System.currentTimeMillis();
                if( data.getEndTime() <= 0 ) {
                    h.getItemView().post( () -> {
                        data.setType( Type.EXPIRED );
                        notifyItemChanged( pos );
                    } );
                    break;
                }
                cdt = new CountDownTimer( time, 1000 ){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        h.getTimeLeft().setText( getTimeLeft( millisUntilFinished ) );
//                        data.setValidTime( millisUntilFinished );
                    }
                    @Override
                    public void onFinish() {
                        h.getItemView().post( () -> {
                            data.setType( Type.EXPIRED );
                            data.setEndTime( 0 );
                            notifyItemChanged( pos );
                        } );
//                        int pos = h.getLayoutPosition();
//                        if( pos < 0 || pos >= getItemCount() ) return;
//                        removeItemData( pos );
//                        notifyItemRemoved( pos );
                    }
                };
                cdt.start();
                break;
            case HolderStatus.DETACHED:
                cdt = data.getCountDownTimer();
                if( cdt != null ) cdt.cancel();
                break;
            case HolderStatus.RECYCLED:
                break;
        }
    }

    private String getTimeLeft(long time) {
        return DateTime.toTimeProgressFormat(
                time,
                ( time >= 86400000 ? DateTimeType.DAY + ":" : "" ) +
                DateTimeType.HOUR + ":" +
                DateTimeType.MINUTE + ":" +
                DateTimeType.SECOND
        );
    }

    private String getTimeInterval(long start, long end) {
        String pattern = DateTimeType.YEAR + "/" + DateTimeType.MONTH + "/" + DateTimeType.DAY;
        return DateTime.parse( start, pattern ) + "-" + DateTime.parse( end, pattern );
    }

    public Drawable getTimeDrawable(Context context, int day, @Type int type) {
        LogUtil.e( "getTimeDrawable -> " + day + " | " + type );
        if( day < 1 || day > 7 ) return null;
        return ResUtil.getDrawable(
                context,
                "ic_item_vouchers_" +
                        ( type == Type.UNUSED || type == Type.IN_USE ? "unused" : "expired" ) +
                        "_" + day
        );
    }

    public @interface Type {
        int NONE = -1;
        int UNUSED = 0;
        int IN_USE = 1;
        int EXPIRED = 2;
        int FREE_EXPIRED = 3;
    }
    public static class ItemData implements IItemData {
        private int id;
        private int day;
        private long startTime;
        private long endTime;
        @Type
        private int type;
        private CountDownTimer mCountDownTimer;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", day=" + day +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", type=" + type +
                    ", mCountDownTimer=" + mCountDownTimer +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public CountDownTimer getCountDownTimer() {
            return mCountDownTimer;
        }

        public void setCountDownTimer(CountDownTimer cdt) {
            mCountDownTimer = cdt;
        }
    }
}
package com.gjjy.usercenterlib.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.HolderStatus;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.IntegralHolder;

import java.util.List;

/**
 * 积分列表适配器
 */
public class IntegralAdapter extends BaseRecyclerViewAdapter<IntegralAdapter.ItemData, IntegralHolder> {

    public IntegralAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public IntegralHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntegralHolder( parent, R.layout.item_integral );
    }

    @Override
    public void onBindViewHolder(@NonNull IntegralHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;

        /* 标题 */
        String title;
        long timestamp = data.getTimestamp();
        if( getTime( System.currentTimeMillis() ).equals( getTime( timestamp ) ) ) {
            //本月
            title = getResources( holder ).getString( R.string.stringThisMonth );
        }else {
            //正常月份
            title = DateTime.parse( timestamp, DateTimeType.YEAR + "/" + DateTimeType.MONTH );
        }
        holder.getTitle().setText( title );
        holder.getTitle().setVisibility( TextUtils.isEmpty( title ) ? View.INVISIBLE : View.VISIBLE );
        //收入
        doCount( holder.getIncomeCount(), data.getIncomeCount() );
        //支出
        doCount( holder.getExpendCount(), data.getExpendCount() );
//        //收入
//        holder.getIncomeCount().setText( data.getIncomeCount() );
//        int expendCount = data.getExpendCount();
//        //支出
//        holder.getExpendCount().setText( expendCount );
//        holder.getExpendCount().setVisibility(
//                expendCount == 0 ? View.GONE : View.VISIBLE
//        );
        //明细列表
        holder.getList().setLayoutManager( new LinearLayoutManager( getContext( holder ) ) );
        holder.getList().setAdapter( new IntegralDetailAdapter( data.getList() ) );
    }

    private void doCount(TextView tv, int i) {
        if( i == 0 ) {
            tv.setVisibility( View.GONE );
            return;
        }
        String count = String.valueOf( Math.abs( i ) );
        tv.setText( i > 0 ? "+" + count : "-" + count );
        tv.setTextColor(
                tv.getResources().getColor( i > 0 ? R.color.colorBuyVipMain : R.color.color66 )
        );
        tv.setVisibility( View.VISIBLE );
    }

    @Override
    public void onHolderChange(@NonNull IntegralHolder holder, int position,
                               @HolderStatus int holderStatus) {
        super.onHolderChange( holder, position, holderStatus );
        if( holderStatus != HolderStatus.ATTACHED ) return;
        if( mOnQueryMonthTotalCountListener == null ) return;
        ItemData data = getItemData( position );
        if( data == null || data.getList().size() == 0 ) return;
        if( data.getIncomeCount() != 0 || data.getExpendCount() != 0 ) return;
        mOnQueryMonthTotalCountListener.onQuery( data.getYear(), data.getMonth(), position );
    }

    public interface OnQueryMonthTotalCountListener {
        void onQuery(int year, int month, int pos);
    }
    private OnQueryMonthTotalCountListener mOnQueryMonthTotalCountListener;

    public void setOnQueryMonthTotalCountListener(OnQueryMonthTotalCountListener l) {
        this.mOnQueryMonthTotalCountListener = l;
    }

    private String getTime(long timestamp) {
        return DateTime.parse( timestamp, DateTimeType.YEAR, "/", DateTimeType.MONTH );
    }

    public static class ItemData implements IItemData {
        private int year;
        private int month;
        private long timestamp;
        private int incomeCount;
        private int expendCount;
        private List<IntegralDetailAdapter.ItemData> list;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "year=" + year +
                    ", month=" + month +
                    ", timestamp=" + timestamp +
                    ", incomeCount=" + incomeCount +
                    ", expendCount=" + expendCount +
                    ", list=" + list +
                    '}';
        }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public int getIncomeCount() { return incomeCount; }
        public void setIncomeCount(int incomeCount) { this.incomeCount = incomeCount; }

        public int getExpendCount() { return expendCount; }
        public void setExpendCount(int expendCount) {
            this.expendCount = expendCount;
        }

        public List<IntegralDetailAdapter.ItemData> getList() { return list; }
        public void setList(List<IntegralDetailAdapter.ItemData> list) { this.list = list; }
    }
}
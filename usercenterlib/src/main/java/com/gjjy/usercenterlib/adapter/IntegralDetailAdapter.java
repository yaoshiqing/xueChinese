package com.gjjy.usercenterlib.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.time.DateTime;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.IntegralDetailHolder;

import java.util.List;

/**
 * 积分明细列表适配器
 */
public class IntegralDetailAdapter extends BaseRecyclerViewAdapter<IntegralDetailAdapter.ItemData, IntegralDetailHolder> {

    public IntegralDetailAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public IntegralDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntegralDetailHolder( parent, R.layout.item_integral_detail );
    }

    @Override
    public void onBindViewHolder(@NonNull IntegralDetailHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;
        //标题
        holder.getTitle().setText( data.getTitle() );
        /* 作用 */
        String explain = data.getExplain();
        holder.getExplain().setText( explain );
        holder.getExplain().setVisibility( TextUtils.isEmpty( explain ) ? View.GONE : View.VISIBLE );
        /* 收入/支出 */
        String count = String.valueOf( Math.abs( data.getCount() ) );
        int typeColor = R.color.color66;
        if( !"0".equals( count ) ) {
            switch( data.getType() ) {
                case 1:         //收入
                    count = "+" + count;
                    typeColor = R.color.colorBuyVipMain;
                    break;
                case 2:         //支出
                    count = "-" + count;
                    break;
            }
        }
        holder.getCount().setText( count );
        holder.getCount().setTextColor( getResources( holder ).getColor( typeColor ) );
        //时间
        holder.getTime().setText( DateTime.parse( data.getTimestamp() ) );

        holder.getDiv().setVisibility( position == getItemCount() - 1 ? View.GONE : View.VISIBLE );
    }

    public static class ItemData implements IItemData {
        private int id;
        private int userId;
        private String title;
        private String explain;
        private int type;
        private int count;
        private long timestamp;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", title='" + title + '\'' +
                    ", explain='" + explain + '\'' +
                    ", type=" + type +
                    ", count=" + count +
                    ", timestamp=" + timestamp +
                    '}';
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getExplain() { return explain; }
        public void setExplain(String type) { this.explain = type; }

        public int getType() { return type; }
        public void setType(int type) { this.type = type; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
package com.gjjy.usercenterlib.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.MeOptHolder;

import java.util.List;

/**
 * 个人中心选项列表适配器
 */
public class MeOptAdapter
        extends BaseRecyclerViewAdapter<MeOptAdapter.ItemData, MeOptHolder> {

    public MeOptAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public MeOptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeOptHolder( parent, R.layout.item_user_center_opt );
    }

    @Override
    public void onBindViewHolder(@NonNull MeOptHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;

        holder.setIcon( data.getIconRes() );
        holder.getTitle().setText( data.getTitle() );

        if( position == getItemCount() - 1 ) {
            holder.hideDiv();
        }else {
            holder.showDiv();
        }
    }

    public static class ItemData implements IItemData {
        private int iconRes;
        private String title;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "iconRes=" + iconRes +
                    ", title='" + title + '\'' +
                    '}';
        }

        public int getIconRes() { return iconRes; }
        public void setIconRes(int iconRes) { this.iconRes = iconRes; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}
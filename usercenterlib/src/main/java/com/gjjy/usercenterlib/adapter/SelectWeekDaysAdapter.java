package com.gjjy.usercenterlib.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseMultiSelectAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.SelectWeekDaysHolder;

import java.util.List;

/**
 * 选择提醒日期适配器
 */
public class SelectWeekDaysAdapter
        extends BaseMultiSelectAdapter<SelectWeekDaysAdapter.ItemData, SelectWeekDaysHolder> {

    public SelectWeekDaysAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public SelectWeekDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectWeekDaysHolder( parent, R.layout.item_select_week_days);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectWeekDaysHolder h, int position, boolean isChecked) {
        ItemData data = getItemData(position);
        if (data == null) return;

        h.getContent().setText( data.getContent() );
    }

    @Override
    public boolean onInitMultiSelect(int i) {
        ItemData data = getItemData( i );
        return data != null && data.isChecked();
    }

    @Override
    public void onMultiSelectChange(RecyclerView.Adapter<SelectWeekDaysHolder> adapter,
                                    @Nullable SelectWeekDaysHolder h,
                                    int position, boolean isChecked, boolean fromUser) {
        if( h == null ) return;
        h.setIcon(
                isChecked ? R.drawable.ic_select_week_days_on : R.drawable.ic_select_week_days_off
        );
//        h.getContent().setTextColor( h.getContent().getResources().getColor(
//                isChecked ? R.color.color66 : R.color.colorA0
//        ));
    }

    public static class ItemData implements IItemData {
        @StringRes
        private int content;
        private boolean isChecked = true;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "content='" + content + '\'' +
                    ", isChecked=" + isChecked +
                    '}';
        }

        @StringRes
        public int getContent() { return content; }
        public ItemData setContent(@StringRes int content) {
            this.content = content;
            return this;
        }

        public boolean isChecked() { return isChecked; }
        public ItemData setChecked(boolean isChecked) {
            this.isChecked = isChecked;
            return this;
        }
    }
}
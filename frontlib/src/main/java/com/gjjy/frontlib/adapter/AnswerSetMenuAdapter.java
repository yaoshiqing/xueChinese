package com.gjjy.frontlib.adapter;

import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.adapter.holder.AnswerSetMenuHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.frontlib.R;

import java.util.List;

/**
 * 答题设置菜单适配器
 */
public class AnswerSetMenuAdapter
        extends BaseRecyclerViewAdapter<AnswerSetMenuAdapter.ItemData, AnswerSetMenuHolder> {

    public interface OnItemCheckedChangeListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked,
                              ItemData data, int position);
    }

    private OnItemCheckedChangeListener mOnItemCheckedChangeListener;

    public AnswerSetMenuAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public AnswerSetMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnswerSetMenuHolder( parent, R.layout.item_answer_setting );
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerSetMenuHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        holder.getName().setCompoundDrawablesWithIntrinsicBounds(
                data.getIconRes(), 0, 0, 0
        );
        holder.getName().setText( data.getName() );

        Switch swi = holder.getSwitchBtn();
        swi.setSelected( data.isEnable() );
        swi.setChecked( data.isEnable() );
        swi.setOnCheckedChangeListener((v, isChecked) -> {
            if( mOnItemCheckedChangeListener == null ) return;
            mOnItemCheckedChangeListener.onCheckedChanged( v, isChecked, data, position );
        });

        holder.addOnClickListener(v -> {
            Switch swiBtn = holder.getSwitchBtn();
            boolean isChecked = !swiBtn.isChecked();
            swiBtn.setChecked( isChecked );
            swiBtn.setSelected( isChecked );
        });

        holder.setEnableDiv( position != getItemCount() - 1 );
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener l) {
        mOnItemCheckedChangeListener = l;
    }

    public static class ItemData extends BaseEntity implements IItemData {
        private int iconRes;
        private String name;
        private boolean enable;
        private String tag;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "iconRes=" + iconRes +
                    ", name='" + name + '\'' +
                    ", enable=" + enable +
                    ", enable=" + tag +
                    '}';
        }

        public int getIconRes() { return iconRes; }
        public void setIconRes(int iconRes) { this.iconRes = iconRes; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }

        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
    }
}

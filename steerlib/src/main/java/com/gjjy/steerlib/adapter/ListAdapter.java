package com.gjjy.steerlib.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gjjy.steerlib.adapter.holder.ObjectiveHolder;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.ResUtil;
import com.gjjy.steerlib.R;

import java.util.List;

public class ListAdapter extends BaseRecyclerViewAdapter<ListAdapter.ItemData, ObjectiveHolder> {
    public @interface ItemType {
        int POWER_TYPE = 1;
        int GOAL_TYPE = 2;
    }
    @ItemType
    private final int mItemType;

    public ListAdapter(@NonNull List<ItemData> list, @ItemType int itemType) {
        super(list);
        mItemType = itemType;
    }

    @NonNull
    @Override
    public ObjectiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int type = R.layout.item_language_level;
        if( mItemType == ItemType.GOAL_TYPE ) {
            type = R.layout.item_objective;
        }
        return new ObjectiveHolder( parent, type );
    }

    private int bgIndex = 1;
    @Override
    public void onBindViewHolder(@NonNull ObjectiveHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        TextView content = holder.getContent();

        content.setText( data.getTitle() );

        if( mItemType == ItemType.GOAL_TYPE ) {
            bindObjective( content );
        }else {
            bindLanguageLevel( content, data );
        }

    }

    private void bindObjective(View bgView) {
        bgView.setBackground(
                ResUtil.getDrawable( bgView.getContext(), "ic_objective_item_bg_" + bgIndex++ )
        );
        if( bgIndex > 7 ) bgIndex = 1;
    }

    private void bindLanguageLevel(TextView bgView, ItemData data) {
        bgView.setCompoundDrawablesWithIntrinsicBounds(
                data.getIcon(),
                null,
                null,
                null
        );
        bgView.setBackgroundColor(
                ResUtil.getColor( bgView.getContext(), "colorObjectiveItem" + bgIndex++ )
        );
        if( bgIndex > 3 ) bgIndex = 1;
    }

    public static class ItemData implements IItemData {
        private Drawable icon;
        private String title;
        private int type;

        public Drawable getIcon() { return icon; }
        public void setIcon(Drawable icon) { this.icon = icon; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
    }
}

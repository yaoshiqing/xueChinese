package com.gjjy.frontlib.adapter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.gjjy.frontlib.adapter.holder.FrontMenuHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.frontlib.R;

import java.util.List;

/**
 * 首页菜单适配器
 */
public class FrontMenuAdapter
        extends BaseRecyclerViewAdapter<FrontMenuAdapter.ItemData, FrontMenuHolder> {
    private final RequestManager mGlide;

    public FrontMenuAdapter(RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public FrontMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FrontMenuHolder( parent, R.layout.item_front_menu );
    }

    @Override
    public void onBindViewHolder(@NonNull FrontMenuHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        holder.getTitle().setText( data.getTitle() );

        if( data.getIconUrl() != null ) {
            mGlide.load( data.getIconUrl() ).into( holder.getImg() );
        }else {
            holder.getImg().setImageDrawable( data.getLocalIcon() );
        }
    }

    public static class ItemData extends BaseEntity implements IItemData {
        private String title;
        private String iconUrl;
        private Drawable localIcon;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + getId() +
                    ", title='" + title + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    ", localIcon=" + localIcon +
                    '}';
        }

        public String getTitle() { return title; }
        public ItemData setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getIconUrl() { return iconUrl; }
        public ItemData setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public Drawable getLocalIcon() { return localIcon; }
        public ItemData setLocalIcon(Drawable localIcon) {
            this.localIcon = localIcon;
            return this;
        }
    }
}

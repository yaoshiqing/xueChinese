package com.gjjy.frontlib.adapter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gjjy.frontlib.adapter.holder.FastReViewHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.base.adapter.listener.OnItemClickListener;
import com.gjjy.frontlib.R;

import java.util.List;

/**
 * 快速复习列表适配器
 */
public class FastReViewAdapter
        extends BaseRecyclerViewAdapter<FastReViewAdapter.ItemData, FastReViewHolder> {
    private RequestManager mGlide;
//    private int[] mPresetColors;
//    private int mColorIndex = 0;

    public FastReViewAdapter(RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation( 0 );
//        mColorFilter = new ColorMatrixColorFilter( colorMatrix );
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public FastReViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FastReViewHolder( parent, R.layout.item_fast_review );
    }

//    private ColorMatrixColorFilter mColorFilter;
    @Override
    public void onBindViewHolder(@NonNull FastReViewHolder h, int position) {
        super.onBindViewHolder(h, position);
        ItemData data = getItemData( position );
        if( data == null ) return;
//        if( mPresetColors == null ) {
//            mPresetColors = ColorUtils.getPresetColors( holder.getContext() );
//        }
//        if( mColorIndex >= mPresetColors.length ) mColorIndex = 0;

        TextView tvContent = h.getContent();
        tvContent.setText( data.getTitle() );

        Drawable defIcon = h.getContent().getResources().getDrawable( R.drawable.ic_right_arrow );
        if( data.getIcon() != null ) {
            tvContent.setCompoundDrawablesWithIntrinsicBounds(
                    data.getIcon(), null, defIcon, null
            );
        }else {
            mGlide.load( data.getIconUrl() )
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable r,
                                                    @Nullable Transition<? super Drawable> t) {
                            tvContent.setCompoundDrawablesWithIntrinsicBounds(
                                    r,
                                    null,
                                    defIcon,
                                    null
                            );
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
            });
        }

        h.setEnableRedDot( data.getNewCount() > 0 );
        h.setRedDotCount( data.getNewCount() );
//        if( data.getIconUrl() != null ) {
////            mGlide.load( data.getIconUrl() ).into( holder.getImg() );
//        }else {
//            tvContent.setCompoundDrawablesWithIntrinsicBounds(
//                    data.getLocalIcon(),
//                    0,
//                    R.drawable.ic_right_arrow,
//                    0
//            );
//        }

////        tvContent.setBackgroundResource( R.drawable.ic_fast_review_item_bg );
//
//        Drawable leftIcon = holder.getContent().getCompoundDrawables()[ 0 ];
//        leftIcon.setColorFilter( Color.parseColor("#DCDCDC"), PorterDuff.Mode.SRC_ATOP );
//        if( data.isEnable() ) {
//            leftIcon.clearColorFilter();
////            holder.getItemView().setBackgroundColor( mPresetColors[ mColorIndex++ ] );
//        }else {
//            leftIcon.setColorFilter( mColorFilter );
////            holder.getItemView().setBackgroundResource( R.color.colorE5);
//        }
//        leftIcon = getImageViewTint( leftIcon, Color.parseColor("#DCDCDC") );
////        holder.getContent().setCompoundDrawables(
////                leftIcon,
////                null,
////                holder.getContent().getCompoundDrawables()[ 2 ],
////                null
////        );
    }

//    public Drawable getImageViewTint(Drawable drawable, @ColorInt int color) {
////        Drawable drawable = iv.getDrawable().mutate();
//        Drawable.ConstantState state = drawable.getConstantState();
//        Drawable wrapDraw = DrawableCompat
//                .wrap( state == null ? drawable : state.newDrawable() )
//                .mutate();
//
////        Drawable wrapDraw = DrawableCompat.wrap( drawable );
//        DrawableCompat.setTintList( wrapDraw, ColorStateList.valueOf( color ) );
////        DrawableCompat.setTintList( wrapDraw, ColorStateList.valueOf( color ) );
////        iv.setT.in( wrapDraw );
//        return wrapDraw;
//    }


//    @Override
//    public void onItemClick(RecyclerView.Adapter<FastReViewHolder> adapter,
//                            View v, ItemData data, int position) {
//        super.onItemClick(adapter, v, data, position);
//        Log.e("TAG", "onItemClick");
//    }
//
    @Override
    public void setOnItemClickListener(OnItemClickListener<ItemData, FastReViewHolder> l) {
        super.setOnItemClickListener((adapter, view, data, i) -> {
            if( l == null ) return;
            if( data.isEnable() ) l.onItemClick( adapter, view, data, i );
        });
    }

    public static class ItemData extends BaseEntity implements IItemData {
        private String title;
//        private String iconUrl;
        private Drawable icon;
        private String iconUrl;
        private int newCount;
        private boolean enable = true;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "title='" + title + '\'' +
                    ", icon=" + icon +
                    ", iconUrl=" + iconUrl +
                    ", newCount=" + newCount +
                    ", enable=" + enable +
                    '}';
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Drawable getIcon() { return icon; }
        public void setIcon(Drawable icon) { this.icon = icon; }

        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

        public int getNewCount() { return newCount; }
        public void setNewCount(int newCount) { this.newCount = newCount; }

        public boolean isEnable() { return enable; }
        public void setEnable(boolean enable) { this.enable = enable; }
    }
}

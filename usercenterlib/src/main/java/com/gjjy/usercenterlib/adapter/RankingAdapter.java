package com.gjjy.usercenterlib.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.RankingHolder;

import java.util.List;

/**
 * 好友排行列表适配器
 */
public class RankingAdapter extends BaseRecyclerViewAdapter<RankingAdapter.ItemData, RankingHolder> {
    private final RequestManager mGlide;
    public RankingAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public RankingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankingHolder( parent, R.layout.item_user_list_item );
    }

    @Override
    public void onBindViewHolder(@NonNull RankingHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;
        //排名
        holder.getIndex().setText( String.valueOf( data.getIndex() ) );
//        holder.getIndex().setText( String.valueOf( isTop3 ? position + 4 : position + 1 ) );
        //图片链接。空链接时为默认图标
        if( TextUtils.isEmpty( data.getImgUrl() ) ) {
            holder.getPhoto().setImageResource( R.drawable.ic_photo_user );
        }else {
            mGlide.load( data.getImgUrl() ).into( holder.getPhoto() );
        }
        //是否为会员
        holder.getVipIcon().setVisibility( data.isVip() ? View.VISIBLE : View.GONE );
        //名字
        holder.getName().setText( data.getName() );
        //经验
        holder.getXP().setText( ( data.getXp() + "XP" ) );
        //当前为用户
        holder.setUser( data.isUser() );
    }

    public static class ItemData implements IItemData {
        private long id;
        private int index;
        private String name;
        private String imgUrl;
        private int xp;
        private boolean isVip;
        private boolean isUser;

        @NonNull
        @Override
        public String toString() {
            return "TopRankingData{" +
                    "id=" + id +
                    ", index=" + index +
                    ", name='" + name + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", xp=" + xp +
                    ", isVip=" + isVip +
                    ", isUser=" + isUser +
                    '}';
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public boolean isVip() {
            return isVip;
        }

        public void setVip(boolean vip) {
            isVip = vip;
        }

        public boolean isUser() {
            return isUser;
        }

        public void setUser(boolean user) {
            isUser = user;
        }
    }
}
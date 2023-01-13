package com.gjjy.usercenterlib.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.FriendsHolder;

import java.util.List;

/**
 * 好友列表适配器
 */
public class FriendsAdapter extends BaseRecyclerViewAdapter<FriendsAdapter.ItemData, FriendsHolder> {
    private final RequestManager mGlide;
    public FriendsAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsHolder( parent, R.layout.item_user_list_item );
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;
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
    }

    public static class ItemData implements IItemData {
        private String name;
        private String imgUrl;
        private boolean isVip;

        @NonNull
        @Override
        public String toString() {
            return "TopRankingData{" +
                    "name='" + name + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", isVip=" + isVip +
                    '}';
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

        public boolean isVip() {
            return isVip;
        }

        public void setVip(boolean vip) {
            isVip = vip;
        }
    }
}
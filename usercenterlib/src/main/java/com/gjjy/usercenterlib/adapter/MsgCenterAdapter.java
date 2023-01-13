package com.gjjy.usercenterlib.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.MsgCenterHolder;
import com.bumptech.glide.RequestManager;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.List;

/**
 * 消息通知列表适配器
 */
public class MsgCenterAdapter extends BaseRecyclerViewAdapter<MsgCenterAdapter.ItemData, MsgCenterHolder> {
    private final RequestManager mGlide;
    public MsgCenterAdapter(RequestManager glide, @NonNull List<ItemData> list) {
        super(list);
        mGlide = glide;
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public MsgCenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MsgCenterHolder( parent, R.layout.item_msg_center );
    }

    @Override
    public void onBindViewHolder(@NonNull MsgCenterHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;

        int imgRes = 0;
        int titleRes = 0;
        switch ( data.getType() ) {
            case Type.SYSTEM:       //系统
                imgRes = R.drawable.ic_msg_center_system_notify_icon;
                titleRes = R.string.stringMsgCenterSystemNotify;
                break;
            case Type.VIP:          //会员
                imgRes = R.drawable.ic_msg_center_vip_notify_icon;
                titleRes = R.string.stringMsgCenterVipNotify;
                break;
            case Type.MESSAGE:      //消息
                imgRes = R.drawable.ic_msg_center_msg_notify_icon;
                titleRes = R.string.stringMsgCenterMsgNotify;
                break;
        }

        if( TextUtils.isEmpty( data.getImgUrl() ) ) {
            if( imgRes != 0 ) holder.getImg().setImageResource( imgRes );
        }else {
            mGlide.load( data.getImgUrl() ).into( holder.getImg() );
        }
        if( titleRes != 0 ) holder.getTitle().setText( titleRes );

        holder.setEnableRedDot( data.isEnableRedDot() );
    }

    public @interface Type {
        int SYSTEM = 0;
        int VIP = 1;
        int MESSAGE = 2;
    }
    public static class ItemData implements IItemData {
        @Type
        private int type;
        private boolean enableRedDot;
        private String imgUrl;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "type=" + type +
                    ", enableRedDot=" + enableRedDot +
                    ", imgUrl=" + imgUrl +
                    '}';
        }

        @Type
        public int getType() { return type; }
        public void setType(@Type int type) { this.type = type; }

        public boolean isEnableRedDot() { return enableRedDot; }
        public void setEnableRedDot(boolean enable) { this.enableRedDot = enable; }

        public String getImgUrl() { return imgUrl; }
        public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
    }
}
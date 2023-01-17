package com.gjjy.usercenterlib.adapter;

import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestManager;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.MessageTimeStyle;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.holder.MsgListHolder;
import com.gjjy.usercenterlib.adapter.holder.MsgListOfInteractiveHolder;

import java.util.List;

/**
 * 消息通知列表适配器
 */
public class MsgListAdapter extends BaseRecyclerViewAdapter<MsgListAdapter.ItemData, BaseViewHolder> {
    private final int mMsgType;
    private final RequestManager mGlide;

    public MsgListAdapter(@NonNull RequestManager glide, @NonNull List<ItemData> list, @MsgCenterAdapter.Type int type) {
        super(list);
        mGlide = glide;
        mMsgType = type;
        setEnableTouchStyle(false);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mMsgType == MsgCenterAdapter.Type.MESSAGE) {
            return new MsgListOfInteractiveHolder(parent, R.layout.item_msg_list_of_interactive);
        }
        return new MsgListHolder(parent, R.layout.item_msg_list);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData(position);
        if (data == null) return;

        // 互动消息
        if (holder instanceof MsgListOfInteractiveHolder) {
            bindMsgListOfInteractive((MsgListOfInteractiveHolder) holder, data);
            return;
        }
        // 常规消息
        bindMsgList((MsgListHolder) holder, data);
    }

    private String mUserName;

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    /**
     * 互动消息
     *
     * @param holder 互动消息Holder
     * @param data   某一条数据
     */
    private void bindMsgListOfInteractive(MsgListOfInteractiveHolder holder, ItemData data) {
        TextView tvInteractContent = holder.getInteractContent();
        int interactiveIcon = 0;
        int interactiveTypeface = Typeface.NORMAL;
        switch (data.getInteractType()) {
            case 1:         //点赞
                interactiveIcon = R.drawable.ic_msg_interactive_like_icon;
                tvInteractContent.setText(R.string.stringInteractiveLike);
                interactiveTypeface = Typeface.BOLD;
                break;
            case 2:         //回复
                tvInteractContent.setText(Html.fromHtml(getTargetContent(mUserName, data.getContent(), true)));
                break;
        }
        tvInteractContent.setCompoundDrawablesWithIntrinsicBounds(interactiveIcon, 0, 0, 0);
        tvInteractContent.setTypeface(Typeface.defaultFromStyle(interactiveTypeface));

        //我的评论
        holder.getMyContent().setText(Html.fromHtml(getTargetContent(mUserName, data.getMyContent(), false)));
        mGlide.load(data.getImgUrl()).into(holder.getMyImg());
        String photoUrl = data.getAvatarUrl();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.getPhoto().setImageResource(R.drawable.ic_photo_user);
        } else {
            mGlide.load(photoUrl).into(holder.getPhoto());
        }
        //时间
        holder.getTime().setText(
                DateTime.toMessageTime(data.getDate(), new MessageTimeStyle(
                        getResources(holder),
                        R.string.stringCommentTimeOfRecently,
                        R.string.stringCommentTimeOfMinutesAgo,
                        R.string.stringCommentTimeOfHoursAgo,
                        R.string.stringCommentTimeOfYesterday,
                        R.string.stringCommentTimeOfDayAgo
                ))
        );
        holder.getVipIcon().setVisibility(data.isVip() ? View.VISIBLE : View.GONE);
        holder.getUserName().setText(data.getNickname());
    }

    private String getTargetContent(@Nullable String name, String content, boolean isAtUser) {
        return String.format(
                "<b><font color='#333333'>%s%s</font></b>%s",
                isAtUser ? "@" : "",
                TextUtils.isEmpty(name) ? "" : (name + ": "),
                content
        );
    }

    /**
     * 常规消息
     *
     * @param holder 常规消息Holder
     * @param data   某一条消息
     */
    private void bindMsgList(MsgListHolder holder, ItemData data) {
        holder.getTitle().setText(data.getTitle());
        holder.getContent().setText(data.getContent());
        holder.getDate().setText(DateTime.parse(data.getDate()));
        holder.getRedDot().setVisibility(data.isNews() ? View.VISIBLE : View.GONE);

        int imgRes = R.drawable.ic_msg_list_other_icon;
        switch (data.getType()) {
            case Type.SYSTEM:       //系统
                imgRes = R.drawable.ic_msg_list_system_icon;
                break;
            case Type.REMIND:       //学习提醒
                imgRes = R.drawable.ic_msg_list_learning_remind_icon;
                break;
            case Type.FEEDBACK:     //反馈
                imgRes = R.drawable.ic_msg_list_feedback_icon;
                break;
        }
        holder.getImg().setImageResource(imgRes);
    }

    public @interface Type {
        int SYSTEM = 0;
        int REMIND = 1;
        int FEEDBACK = 2;
        int OTHER = 3;
    }

    public static class ItemData implements IItemData {
        private int id;
        private int msgType;
        private String title;
        private String content;
        private String url;
        @Type
        private int type;
        private long date;
        private boolean isNews;

        /* 互动消息 */
        private String imgUrl;              //文章小图
        private int talkId;                 //评论表自增ID
        private String nickname;            //回复人的昵称
        private boolean isVip;              //是否为会员
        private String avatarUrl;           //回复人的头像
        private String myContent;           //自己的评论
        private int interactType;           //互动类型。1：点赞，2：回复

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", url='" + url + '\'' +
                    ", msgType=" + msgType +
                    ", type=" + type +
                    ", date=" + date +
                    ", isNews=" + isNews +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", talkId=" + talkId +
                    ", nickname='" + nickname + '\'' +
                    ", isVip='" + isVip + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", myContent='" + myContent + '\'' +
                    ", interactType='" + interactType + '\'' +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getMsgType() {
            return msgType;
        }

        public void setMsgType(int msgType) {
            this.msgType = msgType;
        }

        @Type
        public int getType() {
            return type;
        }

        public void setType(@Type int type) {
            this.type = type;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public boolean isNews() {
            return isNews;
        }

        public void setNews(boolean news) {
            isNews = news;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getTalkId() {
            return talkId;
        }

        public void setTalkId(int talkId) {
            this.talkId = talkId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public boolean isVip() {
            return isVip;
        }

        public void setVip(boolean vip) {
            isVip = vip;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getMyContent() {
            return myContent;
        }

        public void setMyContent(String myContent) {
            this.myContent = myContent;
        }

        public int getInteractType() {
            return interactType;
        }

        public void setInteractType(int interactType) {
            this.interactType = interactType;
        }
    }
}
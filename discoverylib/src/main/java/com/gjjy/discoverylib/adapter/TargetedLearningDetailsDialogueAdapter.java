package com.gjjy.discoverylib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.widget.drag.FlowLayoutManager;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.holder.TargetedLearningDetailsDialogueHolder;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybmediax.media.MediaXC;
import com.ybear.ybmediax.media.MediaXStatusAdapter;
import com.ybear.ybutils.utils.FrameAnimation;

import java.util.Arrays;
import java.util.List;

/**
 * 专项学习 - 对话列表适配器
 */
public class TargetedLearningDetailsDialogueAdapter extends
        BaseRecyclerViewAdapter<TargetedLearningDetailsDialogueAdapter.ItemData, TargetedLearningDetailsDialogueHolder> {

    public interface OnChildContentClickListener {
        void onClick(DialogueContentAdapter adapter, View v,
                     DialogueContentAdapter.ItemData data, int position);
    }

    private final MediaXC mMediaXC;
    private final int mMediaXCTag;
    private OnChildContentClickListener mOnChildContentClickListener;
    private int mCurrentPos = -1;
    private int mLastPlayPos = -1;
    private final FrameAnimation.FrameBuilder mFrameAnimBuild;
    private FrameAnimation.FrameControl mFrameAnimCtrl;


    public TargetedLearningDetailsDialogueAdapter(Context context, @NonNull List<ItemData> list) {
        super(list);
        mMediaXC = MediaXC.get();
        mMediaXCTag = mMediaXC.createTag();
        setEnableTouchStyle( false );

        mFrameAnimBuild = FrameAnimation.create()
                .time( 400 )
                .load(
                context, "ic_play_audio_small_btn_", 0, 3
        );
    }

    public void release() {
        //if( mMediaX != null ) mMediaX.release();
        if( mMediaXC != null ) mMediaXC.stop( mMediaXCTag );
    }

    public void playAudio() {
        if( mMediaXC != null ) mMediaXC.play( mMediaXCTag );
    }

    public void pauseAudio() { mMediaXC.pause( mMediaXCTag ); }

    public void setOnAudioPlayListener(Context context, Consumer<Boolean> l) {
        if( context == null || mMediaXC == null ) return;
        mMediaXC.addOnMediaStatusListener( mMediaXCTag, new MediaXStatusAdapter() {
            @Override
            public void onPlay() {
                if( l != null ) l.accept( true );
                mFrameAnimCtrl.play( context );
            }
            @Override
            public void onPause() {
                if( l != null ) l.accept( false );
                if( mFrameAnimCtrl != null ) mFrameAnimCtrl.stopNow();
            }
            @Override
            public boolean onError(int what, int extra) {
                if( mFrameAnimCtrl != null ) mFrameAnimCtrl.stopNow();
                return super.onError( what, extra );
            }
            @Override
            public void onCompletion(int currentPlayNum, int playTotal, boolean isCompletion) {
                super.onCompletion( currentPlayNum, playTotal, isCompletion );
                if( mFrameAnimCtrl != null ) mFrameAnimCtrl.stopNow();
            }
        });
    }

    @NonNull
    @Override
    public TargetedLearningDetailsDialogueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TargetedLearningDetailsDialogueHolder( parent, R.layout.item_targeted_learning_details_dialogue );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TargetedLearningDetailsDialogueHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;
        //标题
        holder.getTitle().setText( data.getTitle() );
        //翻译
        TextView tvTranslate = holder.getTranslate();
        tvTranslate.setText( data.getTranslate() );
        tvTranslate.setVisibility( data.isShowTranslate() ? View.VISIBLE : View.GONE );

        //内容
        RecyclerView rvContent = holder.getContent();
        holder.getItemView().setVisibility( View.INVISIBLE );
        rvContent.setLayoutManager( new FlowLayoutManager() );
        rvContent.setNestedScrollingEnabled( false );
        DialogueContentAdapter adapter = new DialogueContentAdapter( data.getContent() );
        adapter.setDiscolorTexts( data.getDiscolorTexts() );
        //每个子内容点击事件
        adapter.setOnItemClickListener((adt, view, itemData, i) -> {
            mCurrentPos = position;
            if( mOnChildContentClickListener != null ) {
                mOnChildContentClickListener.onClick( adapter, view, itemData, i );
            }
        });
        rvContent.setAdapter( adapter );

        int contentLen = 1;
        for( DialogueContentAdapter.ItemData itemData : data.getContent() ) {
            contentLen += itemData.getContent().length();
        }
        ViewGroup.LayoutParams lp = rvContent.getLayoutParams();
        int minHeight = Utils.dp2Px( getContext( holder ), 26 );
        lp.height = contentLen <= 17 ? minHeight : (int)Math.ceil( contentLen / 17D ) * minHeight;
        if( holder.getItemView().getVisibility() != View.VISIBLE ) {
            holder.getItemView().setVisibility( View.VISIBLE );
        }

        //播放音频按钮
        holder.getAudioPlayBtn().setOnClickListener( v -> {
            if( mLastPlayPos >= 0 ) {
                //重置上一个按钮的播放状态
                TargetedLearningDetailsDialogueHolder lastHolder = getHolder( mLastPlayPos );
                if( lastHolder != null ) {
                    lastHolder.getAudioPlayBtn().setImageResource(
                            R.drawable.ic_play_audio_small_btn_2
                    );
                }
            }
            mFrameAnimCtrl = mFrameAnimBuild.into( holder.getAudioPlayBtn() );
            mMediaXC.play( mMediaXCTag, data.getAudioUrl() );
            mLastPlayPos = position;
        } );

        RecyclerView.LayoutParams lpItemView = (RecyclerView.LayoutParams) holder.getItemView()
                .getLayoutParams();
        lpItemView.bottomMargin = Utils.dp2Px(
                getContext( holder ), position == getItemCount() - 1 ? 50 : 0
        );
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<TargetedLearningDetailsDialogueHolder> adapter,
                            View v, ItemData data, int position) {
        super.onItemClick( adapter, v, data, position );
        mCurrentPos = position;
    }

    public void cancelTouch() {
        if( mCurrentPos == -1 ) return;
        TargetedLearningDetailsDialogueHolder h = getHolder( mCurrentPos );
        if( h == null ) return;
        RecyclerView rv = h.getContent();
        h.getContent().post(() -> {
            DialogueContentAdapter adapter = ((DialogueContentAdapter) rv.getAdapter());
            if( adapter != null ) adapter.cancelTouch();
        });

    }

    public void setOnChildContentClickListener(OnChildContentClickListener l) {
        mOnChildContentClickListener = l;
    }

    public void showTranslate(boolean isShow) {
        for( ItemData itemData : getDataList() ) {
            itemData.setShowTranslate( isShow );
        }
//        notifyDataSetChanged();
        notifyItemRangeChanged( 0, getDataList().size() );
    }

    public static class ItemData implements IItemData {
        private long id;
        private String title;
        private List<DialogueContentAdapter.ItemData> content;
        private String translate;
        private String audioUrl;
        private String[] discolorTexts = new String[ 0 ];
        private boolean isShowTranslate = true;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content=" + content +
                    ", translate='" + translate + '\'' +
                    ", audioUrl='" + audioUrl + '\'' +
                    ", discolorTexts='" + Arrays.toString( discolorTexts ) + '\'' +
                    ", isShowTranslate=" + isShowTranslate +
                    '}';
        }

        public long getId() {
            return id;
        }

        public ItemData setId(long id) {
            this.id = id;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public ItemData setTitle(String title) {
            this.title = title;
            return this;
        }

        public List<DialogueContentAdapter.ItemData> getContent() {
            return content;
        }

        public ItemData setContent(List<DialogueContentAdapter.ItemData> content) {
            this.content = content;
            return this;
        }

        public String getTranslate() {
            return translate;
        }

        public ItemData setTranslate(String translate) {
            this.translate = translate;
            return this;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public ItemData setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
            return this;
        }

        public String[] getDiscolorTexts() {
            return discolorTexts;
        }

        public void setDiscolorTexts(String[] discolorTexts) {
            this.discolorTexts = discolorTexts;
        }

        public boolean isShowTranslate() {
            return isShowTranslate;
        }

        public ItemData setShowTranslate(boolean showTranslate) {
            isShowTranslate = showTranslate;
            return this;
        }
    }
}

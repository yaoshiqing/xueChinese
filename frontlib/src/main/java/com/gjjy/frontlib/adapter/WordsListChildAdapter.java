package com.gjjy.frontlib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.holder.WordsListChildHolder;
import com.gjjy.frontlib.entity.BaseEntity;
import com.ybear.ybmediax.media.MediaXStatusAdapter;
import com.ybear.ybmediax.media.MediaXC;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybutils.utils.FrameAnimation;

import java.util.List;

/**
 * 单词表子列表适配器
 */
public class WordsListChildAdapter extends BaseRecyclerViewAdapter<WordsListChildAdapter.ItemData, WordsListChildHolder> {
    private final MediaXC mMediaXC;
    private final int mMediaXCTag;
    private final FrameAnimation.FrameBuilder mFrameAnimBuild;
    private FrameAnimation.FrameControl mFrameAnimCtrl;
    private boolean isEnablePinYin = true;
    private boolean isEnableExplain = true;
    private int mLastPlayPos = -1;

    public WordsListChildAdapter(Context context, @NonNull List<ItemData> list) {
        super(list);
        mMediaXC = MediaXC.get();
        mMediaXCTag = mMediaXC.createTag();
        setEnableTouchStyle( false );
        mFrameAnimBuild = FrameAnimation.create()
                .time( 400 )
                .load( context, "ic_play_audio_btn_", 0, 3 );

        mMediaXC.addOnMediaStatusListener( mMediaXCTag, new MediaXStatusAdapter() {
            @Override
            public void onPlay() { if( mFrameAnimCtrl != null ) mFrameAnimCtrl.play( context ); }
            @Override
            public void onPause() { if( mFrameAnimCtrl != null ) mFrameAnimCtrl.stopNow(); }
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
        } );
    }

    @NonNull
    @Override
    public WordsListChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WordsListChildHolder( parent, R.layout.item_words_list_child );
    }

    @Override
    public void onBindViewHolder(@NonNull WordsListChildHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        TextView tvPinYin = holder.getPinYin();
        TextView tvExplain = holder.getExplain();

        holder.getChinese().setText( data.getChinese() );
        tvPinYin.setText( data.getPinYin() );
        tvExplain.setText( data.getExplain() );


        holder.getPlayBtn().setOnClickListener(v -> {
            if( mLastPlayPos >= 0 ) {
                //重置上一个按钮的播放状态
                WordsListChildHolder lastHolder = getHolder( mLastPlayPos );
                if( lastHolder != null ) {
                    lastHolder.getPlayBtn().setImageResource(
                            R.drawable.ic_play_audio_btn_2
                    );
                }
            }
            mFrameAnimCtrl = mFrameAnimBuild.into( holder.getPlayBtn() );
            mMediaXC.play( mMediaXCTag, data.getWordsUrl() );
            mLastPlayPos = position;
        });

//        AnimUtil.setAlphaAnimator(
//                data.isEnablePinYin() ? 1F : 0F,
//                200,
//                animator -> {
//                    float val = (float) animator.getAnimatedValue();
//                    if( val == 0F ) tvPinYin.setVisibility( View.GONE );
//                    if( val == 1F ) tvPinYin.setVisibility( View.VISIBLE );
//                }, tvPinYin
//        );
//        AnimUtil.setAlphaAnimator(
//                data.isEnableExplain() ? 1F : 0F,
//                200,
//                animator -> {
//                    float val = (float) animator.getAnimatedValue();
//                    if( val == 0F ) tvExplain.setVisibility( View.GONE );
//                    if( val == 1F ) tvExplain.setVisibility( View.VISIBLE );
//                }, tvExplain
//        );
//        tvPinYin.setVisibility( data.isEnablePinYin() ? View.VISIBLE : View.GONE );
//        tvExplain.setVisibility( data.isEnableExplain() ? View.VISIBLE : View.GONE );
        tvPinYin.setVisibility( isEnablePinYin ? View.VISIBLE : View.GONE );
        tvExplain.setVisibility( isEnableExplain ? View.VISIBLE : View.GONE );
    }

    public void setEnablePinYin(boolean enablePinYin) {
        isEnablePinYin = enablePinYin;
    }

    public void setEnableExplain(boolean enableExplain) {
        isEnableExplain = enableExplain;
    }

    public static class ItemData extends BaseEntity implements IItemData {
        private String chinese;
        private String pinYin;
        private String explain;
        private String wordUrl;
        private boolean enablePinYin = true;
        private boolean enableExplain = true;

        @NonNull
        @Override
        public String toString() {
            return "ChildData{" +
                    "chinese='" + chinese + '\'' +
                    ", pinYin='" + pinYin + '\'' +
                    ", explain='" + explain + '\'' +
                    ", wordUrl='" + wordUrl + '\'' +
                    ", enablePinYin='" + enablePinYin + '\'' +
                    ", enableExplain='" + enableExplain + '\'' +
                    '}';
        }

        public String getChinese() { return chinese; }
        public void setChinese(String chinese) { this.chinese = chinese; }

        public String getPinYin() { return pinYin; }
        public void setPinYin(String pinYin) { this.pinYin = pinYin; }

        public String getExplain() { return explain; }
        public void setExplain(String explain) { this.explain = explain; }

        public String getWordsUrl() { return wordUrl; }
        public void setWordsUrl(String wordUrl) { this.wordUrl = wordUrl; }

        public boolean isEnablePinYin() { return enablePinYin; }
        public void setEnablePinYin(boolean enable) { enablePinYin = enable; }

        public boolean isEnableExplain() { return enableExplain; }
        public void setEnableExplain(boolean enable) { enableExplain = enable; }
    }
}

package com.gjjy.frontlib.adapter;

import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.frontlib.adapter.holder.LabelDragHolder;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.HolderStatus;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.R;
import com.ybear.ybmediax.media.MediaX;

import java.util.List;

/**
 * 标签拖拽适配器
 */
public class LabelDragAdapter
        extends BaseRecyclerViewAdapter<LabelDragAdapter.ItemData, LabelDragHolder> {
    private MediaX mMediaX;
//    private SpeechSynthesizer mTts;

//    private List<String> mMediaUrl = new ArrayList<>();

    public LabelDragAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle( false );
    }

    @Override
    public void clearItemData() {
        super.clearItemData();
//        mMediaUrl.clear();
    }

    @Override
    public boolean addItemData(List<ItemData> list) {
//        for (ItemData data : list) mMediaUrl.add( data.getAudioUrl() );
        return super.addItemData(list);
    }

    @Override
    public boolean addItemData(int position, ItemData data) {
//        mMediaUrl.add( position, data.getAudioUrl() );
        return super.addItemData(position, data);
    }

    @Override
    public ItemData removeItemData(int position) {
//        mMediaUrl.remove( position );
        return super.removeItemData(position);
    }

    @NonNull
    @Override
    public LabelDragHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelDragHolder( parent, R.layout.item_label_drag );
    }

    @Override
    public void onBindViewHolder(@NonNull LabelDragHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        if( mMediaX == null ) {
//            mMediaX = new MediaX( getContext( holder ) );
////            mMediaX.setDataSource( mMediaUrl.toArray( new String[ 0 ] ) );
//        }
//        if( mTts == null ) mTts = SpeechSynthesizer.get().init( getContext( holder ) ).build();

        ItemData data = getItemData( position );
        Resources res = getResources( holder );
        if (data == null) return;

        String pinYin = data.getPinyinString();

        String pyHtml = TextUtils.isEmpty( pinYin ) ? "" : "<font size='13'>" + pinYin + "</font><br></br>";
        String strHtml = "<font size='18 '>" + data.getDataString() + "</font>";
        holder.getContent().setText( Html.fromHtml( pyHtml + strHtml ) );

        if( data.isShow() ) {
            holder.showItemView();
        }else {
            holder.hideItemView();
        }

        View v = holder.getItemView();
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) v.getLayoutParams();
        lp.bottomMargin = Utils.dp2Px( holder.getContext(), 11 );
        if( !isOptions ) {
            lp.topMargin = lp.bottomMargin;
        }
        v.setLayoutParams( lp );

        int textColor = res.getColor( R.color.color66 );
        int bgRes = data.getTouchStyle();
        //回答正确的样式
        if( data.getTouchStyle() == 0 ) {
            textColor = res.getColor( R.color.colorMain );
            bgRes = R.drawable.ic_answer_options_def_bg;
        }
        ((TextView)v).setTextColor( textColor );
        v.setBackgroundResource( bgRes );
    }

    @Override
    public void onHolderChange(@NonNull LabelDragHolder holder, int position,
                               @HolderStatus int holderStatus) {
        super.onHolderChange( holder, position, holderStatus );
        switch( holderStatus ) {
            case HolderStatus.ATTACHED:
                mMediaX = new MediaX( holder.getContext() );
                break;
            case HolderStatus.DETACHED:
                if( mMediaX != null ) mMediaX.release();
                break;
            case HolderStatus.RECYCLED:
                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<LabelDragHolder> adapter, View v,
                            ItemData data, int position) {
        super.onItemClick(adapter, v, data, position);

//        if( mMediaX != null ) mMediaX.select( position );



//        if( TextUtils.isEmpty( data.getAudioUrl() ) ) {
//            if( mTts == null || TextUtils.isEmpty( data.getDataString() ) ) return;
//            mTts.start( data.getDataString() );
//        }else {
//            if( mMediaX != null ) mMediaX.select( position );
//        }
    }

    public void play(ItemData data) {
        if( mMediaX == null ) return;
        if( data == null || TextUtils.isEmpty( data.getAudioUrl() ) ) return;

        String url = data.getAudioUrl();
        mMediaX.setDataSource( url );
        mMediaX.play();
        LogUtil.e( "LabelDragAdapter -> onItemClick -> url:" + url );
    }

    public void stopNow() {
        if( mMediaX != null ) mMediaX.stop();
    }

    private boolean isOptions = false;
    public void setIsOptions(boolean isOptions) {
        this.isOptions = isOptions;
    }

    public static class ItemData extends OptionsEntity {
        private boolean isShow = true;
        @DrawableRes
        private int touchStyle;

        public ItemData(OptionsEntity data) {
            super( data );
            setDefTouchStyle();
        }

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "isShow=" + isShow +
                    ", touchStyle=" + touchStyle +
                    '}';
        }

        public boolean isShow() { return isShow; }
        public void setShow(boolean show) { isShow = show; }

        public int getTouchStyle() { return touchStyle; }
        public void setDefTouchStyle() { touchStyle = R.drawable.ic_answer_options_def_bg; }
        public void setErrorTouchStyle() { touchStyle = R.drawable.ic_answer_options_error_bg; }
        public void setCorrectTouchStyle() { touchStyle = 0; }
    }
}
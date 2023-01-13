package com.gjjy.discoverylib.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.discoverylib.adapter.holder.DialogueContentHolder;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.discoverylib.R;

import java.util.List;

/**
 * 专项学习 - 对话 - 内容列表适配器
 */
public class DialogueContentAdapter extends
        BaseRecyclerViewAdapter<DialogueContentAdapter.ItemData, DialogueContentHolder> {

    private String[] mDiscolorTexts;

    public DialogueContentAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle( false );
    }

    public DialogueContentAdapter setDiscolorTexts(String[] discolorTexts) {
        this.mDiscolorTexts = discolorTexts;
        return this;
    }

    @NonNull
    @Override
    public DialogueContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogueContentHolder( parent, R.layout.item_dialogue_content );
    }

    @Override
    public void onBindViewHolder(@NonNull DialogueContentHolder h, int position) {
        super.onBindViewHolder(h, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        h.getContent().setText( Html.fromHtml( data.getContent() ) );
        h.getContent().setTextColor(
                getResources( h ).getColor( data.isTouch() ? R.color.colorMain : R.color.color66 )
        );
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<DialogueContentHolder> adapter,
                            View v, ItemData data, int position) {
        boolean isClick = false;
        //只允许变绿的文本才能被点击
        if( mDiscolorTexts != null ) {

            for( String disText : mDiscolorTexts ) {
                if( disText.equals( data.getContent() ) ) {
                    isClick = true;
                    break;
                }
            }
        }
        if( !isClick ) return;

        super.onItemClick( adapter, v, data, position );
        //变绿
        int oldTouchPos = -1, newTouchPos = -1;
        for( int i = 0; i < getDataList().size(); i++ ) {
            ItemData itemData = getItemData( i );
            if( itemData.isTouch() ) {
                oldTouchPos = i;
                itemData.setTouch( false );
            }
            if( itemData.equals( data ) ) {
                newTouchPos = i;
                itemData.setTouch( true );
            }
            if( oldTouchPos != -1 && oldTouchPos == newTouchPos ) break;
        }
        notifyItemChanged( oldTouchPos );
        notifyItemChanged( newTouchPos );
    }

    public void cancelTouch() {
        int cancelTouchPos = -1;
        for( int i = 0; i < getDataList().size(); i++ ) {
            ItemData itemData = getItemData( i );
            if( itemData.isTouch() ) {
                cancelTouchPos = i;
                itemData.setTouch( false );
                break;
            }
        }
        notifyItemChanged( cancelTouchPos );
    }

    public static class ItemData implements IItemData {
        private long id;
        private String content;
        private String pinyin;
        private String translate;
        private boolean isTouch;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", pinyin='" + pinyin + '\'' +
                    ", translate='" + translate + '\'' +
                    ", isTouch=" + isTouch +
                    '}';
        }

        public long getId() {
            return id;
        }

        public ItemData setId(long id) {
            this.id = id;
            return this;
        }

        public String getContent() {
            return content;
        }

        public ItemData setContent(String content) {
            this.content = content;
            return this;
        }

        public String getPinyin() {
            return pinyin;
        }

        public ItemData setPinyin(String pinyin) {
            this.pinyin = pinyin;
            return this;
        }

        public String getTranslate() {
            return translate;
        }

        public ItemData setTranslate(String translate) {
            this.translate = translate;
            return this;
        }

        public boolean isTouch() {
            return isTouch;
        }

        public ItemData setTouch(boolean touch) {
            isTouch = touch;
            return this;
        }
    }
}

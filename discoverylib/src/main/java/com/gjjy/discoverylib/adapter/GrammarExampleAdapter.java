package com.gjjy.discoverylib.adapter;

import android.text.Html;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.holder.GrammarExampleHolder;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.List;

/**
 * 专项学习 -  - 语法列表适配器
 */
public class GrammarExampleAdapter extends
        BaseRecyclerViewAdapter<GrammarExampleAdapter.ItemData, GrammarExampleHolder> {

    public GrammarExampleAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public GrammarExampleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GrammarExampleHolder( parent, R.layout.item_grammar_example );
    }

    @Override
    public void onBindViewHolder(@NonNull GrammarExampleHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        holder.getTitle().setText( Html.fromHtml( data.getTitle() ) );
        holder.getContent().setText( Html.fromHtml( data.getContent() ) );
    }

    public static class ItemData implements IItemData {
        private long id;
        private String title;
        private String content;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }

        public long getId() { return id; }
        public ItemData setId(long id) {
            this.id = id;
            return this;
        }

        public String getTitle() { return title; }
        public ItemData setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getContent() { return content; }
        public ItemData setContent(String content) {
            this.content = content;
            return this;
        }
    }
}

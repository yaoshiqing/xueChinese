package com.gjjy.discoverylib.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.holder.TargetedLearningDetailsGrammarHolder;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 专项学习 - 语法列表适配器
 */
public class TargetedLearningDetailsGrammarAdapter extends
        BaseRecyclerViewAdapter<TargetedLearningDetailsGrammarAdapter.ItemData, TargetedLearningDetailsGrammarHolder> {

    public TargetedLearningDetailsGrammarAdapter(@NonNull List<ItemData> list) {
        super(list);
        setEnableTouchStyle( false );
    }

    @NonNull
    @Override
    public TargetedLearningDetailsGrammarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TargetedLearningDetailsGrammarHolder( parent, R.layout.item_targeted_learning_details_grammar );
    }

    @Override
    public void onBindViewHolder(@NonNull TargetedLearningDetailsGrammarHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemData data = getItemData( position );
        if( data == null ) return;

        String title = data.getTitle();
        boolean isEmptyTitle = TextUtils.isEmpty( title != null ? title.trim() : null );
        if( !isEmptyTitle ) {
            holder.getTitle().setText( Html.fromHtml( data.getTitle() ) );
        }
        holder.getTitle().setVisibility( isEmptyTitle ? View.GONE : View.VISIBLE );

        String content = data.getContent();
        boolean isEmptyContent = TextUtils.isEmpty( content != null ? content.trim() : null );
        if( !isEmptyContent ) {
            holder.getContent().setText( Html.fromHtml( data.getContent() ) );
        }
        holder.getContent().setVisibility( isEmptyContent ? View.GONE : View.VISIBLE );

        String notes = data.getNotes();
        boolean isEmptyNotes = TextUtils.isEmpty( notes != null ? notes.trim() : null );
        if( !isEmptyNotes ) {
            holder.getNotes().setText( notes );
        }
        holder.getNotesLayout().setVisibility( TextUtils.isEmpty( notes ) ? View.GONE : View.VISIBLE );

        RecyclerView rvExample = holder.getExample();
        Context context = getContext( holder );
        rvExample.setLayoutManager( new LinearLayoutManager( context ) );
        rvExample.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( context, 14 ) ) );
        rvExample.setAdapter( new GrammarExampleAdapter( data.getExample() ) );
    }

    @Override
    public void onItemClick(RecyclerView.Adapter<TargetedLearningDetailsGrammarHolder> adapter,
                            View v, ItemData data, int position) {
        super.onItemClick(adapter, v, data, position);
    }

    public static class ItemData implements IItemData {
        private long id;
        private String title;
        private String content;
        private final List<GrammarExampleAdapter.ItemData> example = new ArrayList<>();
        private String notes;

        @NonNull
        @Override
        public String toString() {
            return "ItemData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", example=" + Arrays.toString( example.toArray() ) +
                    ", notes='" + notes + '\'' +
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

        public void addExample(GrammarExampleAdapter.ItemData example) {
            this.example.add( example );
        }
        public void addExample(List<GrammarExampleAdapter.ItemData> example) {
            this.example.addAll( example );
        }
        public List<GrammarExampleAdapter.ItemData> getExample() { return example; }

        public String getNotes() { return notes; }
        public ItemData setNotes(String notes) {
            this.notes = notes;
            return this;
        }
    }
}

package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.discoverylib.R;

public class TargetedLearningDetailsGrammarHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final TextView tvContent;
    private final RecyclerView rvExample;
    private final TextView tvNotes;
    private final LinearLayout llNotesLayout;

    public TargetedLearningDetailsGrammarHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.item_targeted_learning_details_grammar_tv_title );
        tvContent = findViewById( R.id.item_targeted_learning_details_grammar_tv_content );
        rvExample = findViewById( R.id.item_targeted_learning_details_grammar_rv_example );
        tvNotes = findViewById( R.id.item_targeted_learning_details_grammar_tv_notes );
        llNotesLayout = findViewById( R.id.item_targeted_learning_details_grammar_ll_notes_layout );
    }

    public TextView getTitle() { return tvTitle; }

    public TextView getContent() { return tvContent; }

    public RecyclerView getExample() { return rvExample; }

    public TextView getNotes() { return tvNotes; }

    public LinearLayout getNotesLayout() { return llNotesLayout; }
}

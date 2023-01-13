package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.discoverylib.R;

public class GrammarExampleHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final TextView tvContent;

    public GrammarExampleHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.item_grammar_example_tv_title );
        tvContent= findViewById( R.id.item_grammar_example_tv_content );
    }

    public TextView getTitle() { return tvTitle; }

    public TextView getContent() { return tvContent; }
}

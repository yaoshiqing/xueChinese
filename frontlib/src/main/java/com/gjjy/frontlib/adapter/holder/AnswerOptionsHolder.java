package com.gjjy.frontlib.adapter.holder;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.widget.AnswerOptions;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;

public class AnswerOptionsHolder extends BaseViewHolder {
    private final AnswerOptions aoOpt;

    public AnswerOptionsHolder(@NonNull AnswerOptions itemView) {
        super(itemView);
        aoOpt = itemView;
    }

    public AnswerOptions getAnswerOpt() { return aoOpt; }
}

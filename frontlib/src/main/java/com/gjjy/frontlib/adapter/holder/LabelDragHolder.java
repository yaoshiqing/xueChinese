package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;

public class LabelDragHolder extends BaseViewHolder {
    private TextView tvContent;

    public LabelDragHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();
        tvContent = (TextView) v;
    }

    public LabelDragHolder setContent(TextView tvContent) {
        this.tvContent = tvContent;
        return this;
    }
    public TextView getContent() { return tvContent; }
}
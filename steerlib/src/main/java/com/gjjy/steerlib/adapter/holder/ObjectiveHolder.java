package com.gjjy.steerlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;

public class ObjectiveHolder extends BaseViewHolder {
    private final TextView tvContent;
    public ObjectiveHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvContent = (TextView) getItemView();
    }

    public TextView getContent() { return tvContent; }
}

package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.frontlib.R;

public class VoiceCenterHolder extends BaseViewHolder {
    private final TextView tvPinYin;
    private final TextView tvText;
    public VoiceCenterHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();
        tvPinYin = v.findViewById( R.id.item_voice_center_tv_pinyin );
        tvText = v.findViewById( R.id.item_voice_center_tv_text );
    }

    public TextView getPinYin() { return tvPinYin; }

    public TextView getText() { return tvText; }
}

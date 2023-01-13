package com.gjjy.usercenterlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;

public class SelectWeekDaysHolder extends BaseViewHolder {
    private TextView tvContent;

    public SelectWeekDaysHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvContent = (TextView) getItemView();
    }

    public TextView getContent() { return tvContent; }

    public void setIcon(@DrawableRes int resId) {
        tvContent.setCompoundDrawablesWithIntrinsicBounds( 0, resId, 0, 0 );
    }
}

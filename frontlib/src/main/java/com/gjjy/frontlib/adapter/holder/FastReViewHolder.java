package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.RedDotView;
import com.gjjy.frontlib.R;

public class FastReViewHolder extends BaseViewHolder {
    private final RedDotView rdvRedDot;
    private final TextView tvContent;

    public FastReViewHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();
        rdvRedDot = v.findViewById( R.id.item_fast_review_rdv_red_dot );
        tvContent = v.findViewById( R.id.item_fast_review_tv_item );
    }

    public void setEnableRedDot(boolean enable) {
        rdvRedDot.setVisibility( enable ? View.VISIBLE : View.GONE );
    }

    public void setRedDotCount(int count) {
        rdvRedDot.setText( String.valueOf( count ) );
    }

    public TextView getContent() { return tvContent; }
}

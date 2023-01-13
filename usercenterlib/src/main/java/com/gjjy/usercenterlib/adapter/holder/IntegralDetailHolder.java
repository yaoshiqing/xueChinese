package com.gjjy.usercenterlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.usercenterlib.R;

public class IntegralDetailHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final TextView tvExplain;
    private final TextView tvTime;
    private final TextView tvCount;
    private final View vDiv;

    public IntegralDetailHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.item_integral_detail_tv_title );
        tvExplain = findViewById( R.id.item_integral_detail_tv_explain );
        tvTime = findViewById( R.id.item_integral_detail_tv_time );
        tvCount = findViewById( R.id.item_integral_detail_tv_count );
        vDiv = findViewById( R.id.item_integral_detail_v_div );
    }

    public TextView getTitle() { return tvTitle; }

    public TextView getExplain() { return tvExplain; }

    public TextView getTime() { return tvTime; }

    public TextView getCount() { return tvCount; }

    public View getDiv() { return vDiv; }
}

package com.gjjy.usercenterlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.usercenterlib.R;

public class IntegralHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final TextView tvIncomeCount;
    private final TextView tvExpendCount;
    private final RecyclerView rvList;

    public IntegralHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.item_integral_tv_title );
        tvIncomeCount = findViewById( R.id.item_integral_tv_income_count );
        tvExpendCount = findViewById( R.id.item_integral_tv_expend_count );
        rvList = findViewById( R.id.item_integral_rv_list );
    }

    public TextView getTitle() { return tvTitle; }

    public TextView getIncomeCount() { return tvIncomeCount; }

    public TextView getExpendCount() { return tvExpendCount; }

    public RecyclerView getList() { return rvList; }
}

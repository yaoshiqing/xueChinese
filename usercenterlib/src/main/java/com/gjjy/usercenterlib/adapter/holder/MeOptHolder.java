package com.gjjy.usercenterlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.usercenterlib.R;

public class MeOptHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final View vDiv;

    public MeOptHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.user_center_options_tv_title );
        vDiv = findViewById( R.id.user_center_options_v_div );
    }

    public TextView getTitle() { return tvTitle; }

    public void setIcon(@DrawableRes int resId) {
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                resId, 0, R.drawable.ic_right_arrow, 0
        );
    }

    public void showDiv() { vDiv.setVisibility( View.VISIBLE ); }
    public void hideDiv() { vDiv.setVisibility( View.INVISIBLE ); }
}

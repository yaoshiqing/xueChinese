package com.gjjy.frontlib.adapter.holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.frontlib.R;

public class AnswerSetMenuHolder extends BaseViewHolder {
    private final TextView tvName;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private final Switch swiSwitchBtn;
    private final View vDiv;

    public AnswerSetMenuHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvName = getItemView().findViewById( R.id.item_answer_setting_tv_name );
        swiSwitchBtn = getItemView().findViewById( R.id.item_answer_setting_swi_switch_btn );
        vDiv = getItemView().findViewById( R.id.item_answer_setting_v_div );
    }

    public TextView getName() { return tvName; }

    public Switch getSwitchBtn() { return swiSwitchBtn; }

    public void setEnableDiv(boolean enable) {
        vDiv.setVisibility( enable ? View.VISIBLE : View.INVISIBLE );
    }
}

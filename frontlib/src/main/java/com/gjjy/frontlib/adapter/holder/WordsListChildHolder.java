package com.gjjy.frontlib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.frontlib.R;

public class WordsListChildHolder extends BaseViewHolder {
    private final TextView tvChinese;
    private final TextView tvPinYin;
    private final TextView tvExplain;
    private final ImageView ivPlayBtn;

    public WordsListChildHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvChinese = getItemView().findViewById( R.id.item_words_list_child_tv_chinese );
        tvPinYin = getItemView().findViewById( R.id.item_words_list_child_tv_pinyin );
        tvExplain = getItemView().findViewById( R.id.item_words_list_child_tv_explain );
        ivPlayBtn = getItemView().findViewById( R.id.item_words_list_child_iv_play_btn );
    }

    public TextView getChinese() { return tvChinese; }

    public TextView getPinYin() { return tvPinYin; }

    public TextView getExplain() { return tvExplain; }

    public ImageView getPlayBtn() { return ivPlayBtn; }
}

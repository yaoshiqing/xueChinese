package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.widget.WordsListRecyclerView;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.frontlib.R;

public class WordsListHolder extends BaseViewHolder {
    private final ImageView ivImg;
    private final TextView tvContent;
    private final WordsListRecyclerView rvChildList;
    private final View vDiv;

    public WordsListHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ivImg = getItemView().findViewById( R.id.item_words_list_tv_img );
        tvContent = getItemView().findViewById( R.id.item_words_list_tv_title );
        rvChildList = getItemView().findViewById( R.id.item_words_list_rv_child_list );
        vDiv = getItemView().findViewById( R.id.item_words_list_v_div );
    }

    public ImageView getImg() { return ivImg; }

    public TextView getTitle() { return tvContent; }

    public void switchOpenIcon() {
        tvContent.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_words_list_item_open, 0
        );
    }

    public void switchCloseIcon() {
        tvContent.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_words_list_item_close, 0
        );
    }

    public WordsListRecyclerView getChildList() { return rvChildList; }

    public View getDiv() { return vDiv; }
}

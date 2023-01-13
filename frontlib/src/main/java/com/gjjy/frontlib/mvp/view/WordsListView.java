package com.gjjy.frontlib.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.adapter.WordsListAdapter;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface WordsListView extends MvpViewable {
    void onCallAllCategoryTitle(int id);
    void onCallAllCategory(@NonNull List<FrontMenuAdapter.ItemData> list,
                           @Nullable FrontMenuAdapter.ItemData currentItem
    );
    void onCallCategoryContent(@NonNull List<WordsListAdapter.ItemData> list);
    void onCallWordsChildList(@NonNull List<WordsListChildAdapter.ItemData> list);
    void onCallLoadingDialog(boolean isShow);
}

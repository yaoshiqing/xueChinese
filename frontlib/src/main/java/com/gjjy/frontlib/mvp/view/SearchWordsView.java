package com.gjjy.frontlib.mvp.view;

import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface SearchWordsView extends MvpViewable {
    void onCallWordsChildList(List<WordsListChildAdapter.ItemData> list);
}

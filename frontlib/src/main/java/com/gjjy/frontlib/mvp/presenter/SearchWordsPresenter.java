package com.gjjy.frontlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.gjjy.frontlib.mvp.model.WordsUnitModel;
import com.gjjy.frontlib.mvp.view.SearchWordsView;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.List;

public class SearchWordsPresenter extends MvpPresenter<SearchWordsView> {
    @Model
    private UserModel mUserModel;
    @Model
    private WordsUnitModel mWordsUnitModel;

    public SearchWordsPresenter(@NonNull SearchWordsView view) {
        super(view);
        mWordsUnitModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    public void search(String kw) {
        mWordsUnitModel.searchWordsExplain(kw, list -> {
            List<WordsListChildAdapter.ItemData> childList =
                    mWordsUnitModel.toWordsListChildDataList( list, null );
            viewCall( v -> v.onCallWordsChildList( childList ) );
        });
        buriedPointSearch();
    }

    private void buriedPointSearch() {
        BuriedPointEvent.get().onReviewModuleOfSearchButtonWordsListPage( getContext() );
    }

}
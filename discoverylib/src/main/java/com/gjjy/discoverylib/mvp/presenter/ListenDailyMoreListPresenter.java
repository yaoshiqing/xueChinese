package com.gjjy.discoverylib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.ListenDailyMoreListView;
import com.gjjy.basiclib.mvp.model.UserModel;

public class ListenDailyMoreListPresenter extends MvpPresenter<ListenDailyMoreListView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;

    private int mPage;

    public ListenDailyMoreListPresenter(@NonNull ListenDailyMoreListView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mDisArtModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
        queryDataList();
    }

    public void queryDataList() {
        mPage = 0;
        nextDataList();
    }

    public void nextDataList() {
        if( mPage == -1 ) {
            viewCall( v -> v.onCallDataList( null, true ) );
            return;
        }else {
            mPage++;
        }
        mDisArtModel.reqListenDailyMoreList( mPage, data -> viewCall( v -> {
            v.onCallIsVip( mUserModel.isVip() );
            v.onCallDataList(
                    mDisArtModel.toListenDailyList( data.getData() ),
                    mPage == -1 || mPage > 1
            );
            if( mPage >= data.getLastPage() ) mPage = -1;
        }));
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }
}
package com.gjjy.discoverylib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.TargetedLearningMoreListView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class TargetedLearningMoreListPresenter extends MvpPresenter<TargetedLearningMoreListView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;

    private int mPage = 0;

    public TargetedLearningMoreListPresenter(@NonNull TargetedLearningMoreListView view) {
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
        mDisArtModel.reqTargetedLearningMoreList( mPage, data -> viewCall( v -> {
            v.onCallIsVip( mUserModel.isVip() );
            v.onCallDataList(
                    mDisArtModel.toPopularVideosList( data.getData() ),
                    mPage == -1 || mPage > 1
            );
            if( mPage >= data.getLastPage() ) mPage = -1;
        }));
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }
}
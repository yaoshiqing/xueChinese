package com.gjjy.discoverylib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.ListenDailyView;
import com.gjjy.basiclib.mvp.model.UserModel;

public class ListenDailyPresenter extends MvpPresenter<ListenDailyView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;
    private boolean isQuerying = false;

    public ListenDailyPresenter(@NonNull ListenDailyView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        post( () -> queryDataList( true ), 400 );
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }

    public void queryDataList(boolean isCache) {
        if( isQuerying ) return;
        isQuerying = true;
        mDisArtModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
        if( isCache ) viewCall( v -> v.onCallIsVip( mUserModel.isVip() ) );
        mDisArtModel.reqListenDailyList( isCache, list -> {
            viewCall( v -> {
                v.onCallDataList( list );
                v.onCallIsVip( mUserModel.isVip() );
            });
            isQuerying = false;
        });
    }

    public void buriedPointOpenItem(long id, String name) {
        BuriedPointEvent.get().onDiscoveryPageOfListenEverydayLessons(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName( getResources() ),
                id, name
        );
    }

    public void buriedPointOpenMore() {
        BuriedPointEvent.get().onDiscoveryPageOfMoreButtonListenEveryday(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );
    }
}
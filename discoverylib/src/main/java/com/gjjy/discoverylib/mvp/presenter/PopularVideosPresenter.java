package com.gjjy.discoverylib.mvp.presenter;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.DiscoveryMorePageEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.PopularVideosView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class PopularVideosPresenter extends MvpPresenter<PopularVideosView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;

    private Consumer<DiscoveryMorePageEntity> mCallDataList;

    private boolean isMoreList;
    private int mPage = 0;
    private boolean isQuerying = false;

    public PopularVideosPresenter(@NonNull PopularVideosView view) {
        super(view);
    }

    public void setIsMoreList(boolean isMoreList) {
        this.isMoreList = isMoreList;
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        mDisArtModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
        post( () -> queryDataList( true ), 500 );
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }

    public void queryDataList(boolean isCache) {
        mPage = 0;
        nextDataList( isCache );
    }

    public void nextDataList(boolean isCache) {
        if( isQuerying ) return;
        isQuerying = true;
        if( isCache ) viewCall( v -> v.onCallIsVip( mUserModel.isVip() ) );
        if( mCallDataList == null ) {
            mCallDataList = data -> {
                viewCall( v -> {
                    v.onCallDataList(
                            mDisArtModel.toPopularVideosList( data.getData() ),
                            mPage == -1 || mPage > 1
                    );
                    v.onCallIsVip( mUserModel.isVip() );
                });
                if( mPage >= data.getLastPage() ) mPage = -1;
                isQuerying = false;
            };
        }

        if( mPage == -1 ) {
            viewCall( v -> v.onCallDataList( null, true ) );
            return;
        }else {
            mPage++;
        }
        if( isMoreList ) {
            mDisArtModel.reqPopularVideosMoreList(mPage, mCallDataList );
            return;
        }
        mDisArtModel.reqPopularVideosList(isCache, mCallDataList );
    }

    public void buriedPointOpenItem(long id, String name) {
        BuriedPointEvent.get().onDiscoveryPageOfPopularVideoLessons(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName( getResources() ),
                id, name
        );
    }

    public void buriedPointOpenMore() {
        BuriedPointEvent.get().onDiscoveryPageOfMoreButtonPopularVideos(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );
    }
}

package com.gjjy.discoverylib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.MyCourseView;
import com.gjjy.basiclib.mvp.model.UserModel;

public class MyCoursePresenter extends MvpPresenter<MyCourseView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;

    private int mCollectPage;
    private int mHaveReadPage;

    public MyCoursePresenter(@NonNull MyCourseView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mDisArtModel.setUid( mUserModel.getUid(), mUserModel.getToken() );

        queryCollectDataList();
        queryHaveReadDataList();
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        long cancelId = mDisArtModel.getCancelCollectId();
        if( cancelId != -1 ) {
            viewCall(v -> {
                v.onCallCancelCollectId( cancelId );
                mDisArtModel.setCancelCollectId( -1 );
            });
        }
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }

    public void queryCollectDataList() {
        mCollectPage = 0;
        nextCollectDataList();
    }
    public void nextCollectDataList() {
        if( mCollectPage == -1 ) {
            viewCall( v -> {
                v.onCallCollectDataList( mCollectPage, null, false );
                v.onCallShowLoadingDialog( false );
            });
            return;
        }else {
            mCollectPage++;
        }

        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mDisArtModel.reqCollectionList( mCollectPage, data -> viewCall( v -> {
            v.onCallCollectDataList(
                    mCollectPage, mDisArtModel.toMyCourseList( data.getData() ), mUserModel.isVip()
            );
            if( mCollectPage >= data.getLastPage() ) mCollectPage = -1;
            v.onCallShowLoadingDialog( false );
        }));
    }

    public void queryHaveReadDataList() {
        mHaveReadPage = 0;
        nextHaveReadDataList();
    }
    public void nextHaveReadDataList() {
        if( mHaveReadPage == -1 ) {
            viewCall( v -> {
                v.onCallHaveReadDataList( mHaveReadPage, null, false );
                v.onCallShowLoadingDialog( false );
            });
            return;
        }else {
            mHaveReadPage++;
        }

        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mDisArtModel.reqHaveReadList( mHaveReadPage, data -> viewCall( v -> {
            v.onCallHaveReadDataList(
                    mHaveReadPage, mDisArtModel.toMyCourseList( data.getData() ), mUserModel.isVip()
            );
            if( mHaveReadPage >= data.getLastPage() ) mHaveReadPage = -1;
            v.onCallShowLoadingDialog( false );
        }));
    }
}

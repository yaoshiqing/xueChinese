package com.gjjy.discoverylib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.TargetedLearningView;
import com.gjjy.basiclib.mvp.model.UserModel;

public class TargetedLearningPresenter extends MvpPresenter<TargetedLearningView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;
    private boolean isQuerying = false;

    public TargetedLearningPresenter(@NonNull TargetedLearningView view) {
        super( view );
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        post( () -> queryDataList( true ), 600 );
    }

    public String getUid() { return mUserModel.getUid(); }
    public String getUserName() { return mUserModel.getUserName( getResources() ); }
    public boolean isVip() {
        return mUserModel.isVip();
    }

    public void queryDataList(boolean isCache) {
        if( isQuerying ) return;
        isQuerying = true;
        mDisArtModel.setUid( mUserModel.getUid(), mUserModel.getToken() );
        mDisArtModel.reqTargetedLearningList( isCache, list -> {
            viewCall( v -> v.onCallDataList( list ) );
            isQuerying = false;
        } );
    }

    public void buriedPointSelectedCourse(long id, String name) {
        BuriedPointEvent.get().onVideoGrammarCourseListPageOfVideoGrammarCourse(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                id,
                name
        );
    }

    public void buriedPointUnlockCourse(long id, String name, boolean result) {
        BuriedPointEvent.get().onVideoGrammarCourseListPageOfCourseUnlockPopupOfUnlockButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                id,
                name,
                result
        );
    }

    public void buriedPointOpenMore() {
        BuriedPointEvent.get().onFindingPageOfVideoGrammarCourseOfMoreButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )
        );
    }
}

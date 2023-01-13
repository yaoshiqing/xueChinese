package com.gjjy.discoverylib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.discoverylib.mvp.view.DiscoveryView;
import com.gjjy.discoverylib.utils.StartUtil;
import com.gjjy.basiclib.mvp.model.UserModel;

public class DiscoveryPresenter extends MvpPresenter<DiscoveryView> {
    @Model
    private UserModel mUserModel;

    public DiscoveryPresenter(@NonNull DiscoveryView view) {
        super(view);
    }

    public void startMyCourseActivity() {
        if( !mUserModel.startLoginCheckActivity( getActivity() ) ) {
            StartUtil.startMyCourseActivity();
            buriedPointOpenMyCourse();
        }
    }

    private void buriedPointOpenMyCourse() {
        BuriedPointEvent.get().onDiscoveryPageOfMyLessons(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() )

        );
    }
}

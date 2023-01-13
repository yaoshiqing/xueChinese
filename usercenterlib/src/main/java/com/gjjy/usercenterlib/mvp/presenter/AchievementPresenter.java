package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.usercenterlib.mvp.view.AchievementView;
import com.gjjy.basiclib.mvp.model.UserModel;

public class AchievementPresenter extends MvpPresenter<AchievementView> {
    @Model
    private UserModel mUserModel;

    public AchievementPresenter(@NonNull AchievementView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        refreshMoney();
    }

    private void refreshMoney() {
//        mUserModel.getAward(data -> {
//            AchievementView v = getView();
//            if( v != null ) post(() -> v.onCallRewardMoney( data.getLightning(), data.getHeart() ));
//        });
        viewCall(v -> v.onCallRewardMoney( mUserModel.getLightning(), mUserModel.getHeart() ));
    }

    public void checkLoginStatus() {
        viewCall(v -> v.onCallIsLoginSuccess( mUserModel.isLoginResult() ));
    }
}
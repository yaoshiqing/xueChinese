package com.gjjy.frontlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.mvp.view.ReViewView;
import com.gjjy.basiclib.mvp.model.ReqAnswerModel;
import com.gjjy.basiclib.mvp.model.UserModel;

public class ReViewPresenter extends MvpPresenter<ReViewView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqAnswerModel mReqAnswer;

    public ReViewPresenter(@NonNull ReViewView view) {
        super(view);
        mReqAnswer.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        refreshInfo();
    }

    public void refreshInfo() {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mReqAnswer.reqReviewNewCount( data -> viewCall( v -> {
            v.onCallReviewNewCount( data.getCount() );
            v.onCallShowLoadingDialog( false );
        }));
    }

}

package com.gjjy.usercenterlib.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.mvp.view.FeedbackView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;

import java.io.File;
import java.util.List;

public class FeedbackPresenter extends MvpPresenter<FeedbackView> {
    @Model
    private UserModel mUserModel;
    @Model
    private OtherModel mOtherModel;
    @Model
    private LoginModel mLoginModel;

    private String mUserEmail;

    public FeedbackPresenter(@NonNull FeedbackView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mUserEmail = mUserModel.getEmail();
        if( !TextUtils.isEmpty( mUserEmail ) ) viewCall(v -> v.onCallAutoFillInEmail( mUserEmail ));
    }

    public String getUserEmail() { return mUserEmail; }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        mOtherModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    public boolean checkEmail(String s) {
        return mLoginModel.checkEmail( s );
    }

    public void submit(String content, String email, List<File> imgFiles) {
//        boolean check = checkSubimt( email );
//        if( getContext() == null ) return;
//        Resources r = getContext().getResources();
//        FeedbackView v = getView();
//        if( v == null ) return;
//        v.onCallContactHintColor(
//                r.getColor( check ? R.color.color33 : R.color.colorDialogError)
//        );
//
//        if( !check ) return;

        mOtherModel.feedback(content, email, imgFiles, result -> {
            FeedbackView v1 = getView();
            if( v1 != null ) post(() -> v1.onCallSubmitResult( result ));
            return 0;
        });
    }

    public void copyEmail(String email) {
        Context context = getContext();
        if( context == null ) {
            viewCall( v -> v.onCallCopyEmailResult( false ) );
            return;
        }
        boolean result = SysUtil.copyTextToClipboard( getContext(), email );
        viewCall( v -> v.onCallCopyEmailResult( result ) );
    }

    public void startFacebook() {
        Context context = getContext();
        if( context == null ) return;
        Utils.startFacebookPage( context, Config.mFacebookUrl );
        BuriedPointEvent.get().onMePageOfFeedbackPageOfFBurl( context );
    }

//    private boolean checkSubimt(String email) {
//        return !TextUtils.isEmpty( email );
//    }
}

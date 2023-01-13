package com.gjjy.login.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ybear.ybutils.utils.LogUtil;

import java.lang.ref.WeakReference;

public class FacebookLogInImpl implements FacebookLogIn {
    private static final String TAG = "FacebookLogIn";
    private WeakReference<Activity> mWRActivity;
    private final FacebookBuild mBuild;
    private LoginManager mLoginManager;
    private final CallbackManager mCall;

    FacebookLogInImpl() {
        mCall = CallbackManager.Factory.create();
        mBuild = FacebookBuild.create();
    }

    @Override
    public void registerCallback(@NonNull Activity activity, FacebookCallback call) {
        mWRActivity = new WeakReference<>( activity );
        getLoginManager().registerCallback(mCall, new com.facebook.FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult result) {
                AccessToken token = result.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(token, (json, response) -> {
                    if( call != null ) call.onSuccess( new AccountResult( json ) );
                    LogUtil.e( "Facebook -> registerCallback -> " + json );
                });
                request.setParameters( getReqParam() );
                request.executeAsync();
            }
            @Override
            public void onCancel() { if( call != null ) call.onCancel(); }
            @Override
            public void onError(FacebookException error) {
                if( call != null ) call.onError( error );
            }
        });
    }
    private Bundle getReqParam() {
        Bundle b = new Bundle();
        b.putString( "fields", "id,name,gender,email,picture,first_name,last_name" );
        return b;
    }

    @Override
    public void unregisterCallback() {
        getLoginManager().unregisterCallback( mCall );
    }

    @Override
    public void setPermissions(String... permissions) { mBuild.setPermissions( permissions ); }

    @Override
    public FacebookBuild requestEmail() { return mBuild.requestEmail(); }

    @Override
    public FacebookBuild requestPhotoUrl() { return mBuild.requestPhotoUrl(); }

    public FacebookBuild requestLocation() { return mBuild.requestLocation(); }

    public FacebookBuild requestAgeRange() { return mBuild.requestAgeRange(); }

    public FacebookBuild requestBirthday() { return mBuild.requestBirthday(); }

    public FacebookBuild requestFriends() { return mBuild.requestFriends(); }

    public FacebookBuild requestGender() { return mBuild.requestGender(); }

    public FacebookBuild requestLikes() { return mBuild.requestLikes(); }

    public FacebookBuild requestLink(){ return mBuild.requestLink(); }
    @Override
    public void onClick(View v) {
        if( mWRActivity != null ) {
            Activity activity = mWRActivity.get();
            if( activity == null ) {
                Log.e(TAG, "Activity is null!");
                return;
            }
            getLoginManager().logInWithReadPermissions( activity, mBuild.get() );
        }else {
            Log.e(TAG, "You need call registerCallback()");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCall.onActivityResult( requestCode, resultCode, data );
    }

    private LoginManager getLoginManager() {
        if (mLoginManager == null) {
            mLoginManager = LoginManager.getInstance();
        }
        return mLoginManager;
    }
}

package com.gjjy.loginlib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.login.facebook.FacebookBuild;
import com.gjjy.login.facebook.FacebookCallback;
import com.gjjy.login.facebook.FacebookLogIn;
import com.gjjy.loginlib.R;

public class FacebookLoginButton extends BaseLoginButton implements FacebookLogIn {
    private final FacebookLogIn mLogin;

    public FacebookLoginButton(Context context) {
        this(context, null);
    }

    public FacebookLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FacebookLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIcon( R.drawable.ic_facebook_icon );
        setText( R.string.textFacebook );
        setTextColor( Color.WHITE );
        setBackgroundResource( R.drawable.ic_login_facebook_bg_btn );
        mLogin = FacebookLogIn.create();
        mLogin.requestPhotoUrl();
        mLogin.requestEmail();
        mLogin.requestGender();
    }

//    @Override
//    public void switchTouchStatus(boolean isTouch) {
//        super.switchTouchStatus(isTouch);
//        setIcon( isTouch ? R.drawable.ic_facebook_unselect : R.drawable.ic_facebook_select );
//    }

    @Override
    public void registerCallback(@NonNull Activity activity, FacebookCallback call) {
        mLogin.registerCallback( activity, call );
    }

    @Override
    public void unregisterCallback() { mLogin.unregisterCallback(); }

    @Override
    public void setPermissions(String... permissions) { mLogin.setPermissions( permissions ); }

    @Override
    public FacebookBuild requestEmail() { return mLogin.requestEmail(); }

    @Override
    public FacebookBuild requestPhotoUrl() { return mLogin.requestPhotoUrl(); }

    @Override
    public FacebookBuild requestLocation() { return mLogin.requestLocation(); }

    @Override
    public FacebookBuild requestAgeRange() { return mLogin.requestAgeRange(); }

    @Override
    public FacebookBuild requestBirthday() { return mLogin.requestBirthday(); }

    @Override
    public FacebookBuild requestFriends() { return mLogin.requestFriends(); }

    @Override
    public FacebookBuild requestGender() { return mLogin.requestGender(); }

    @Override
    public FacebookBuild requestLikes() { return mLogin.requestLikes(); }

    @Override
    public FacebookBuild requestLink() { return mLogin.requestLink(); }

    @Override
    public void onClick(View v) {
        super.onClick( v );
        mLogin.onClick( v );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mLogin.onActivityResult( requestCode, resultCode, data );
    }
}

package com.gjjy.loginlib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Scope;
import com.gjjy.login.google.GoogleBuild;
import com.gjjy.login.google.GoogleCallback;
import com.gjjy.login.google.GoogleLogIn;
import com.gjjy.loginlib.R;

public class GoogleLoginButton extends BaseLoginButton implements GoogleLogIn {
    private final GoogleLogIn mLogin;

    public GoogleLoginButton(Context context) {
        this(context, null);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIcon( R.drawable.ic_google_icon );
        setText( R.string.textGoogle );
        setTextColor( Color.BLACK );
        setBackgroundResource( R.drawable.ic_login_google_bg_btn );
        mLogin = GoogleLogIn.create( context );
        mLogin.requestEmail();
//        mLogin.requestGender();
    }

//    @Override
//    public void switchTouchStatus(boolean isTouch) {
//        super.switchTouchStatus(isTouch);
//        setIcon( isTouch ? R.drawable.ic_google_unselect : R.drawable.ic_google_select );
//    }

    @Override
    public void registerCallback(@NonNull Activity activity, GoogleCallback call) {
        mLogin.registerCallback( activity, call );
    }

    @Override
    public GoogleBuild requestIdToken() { return mLogin.requestIdToken(); }

    @Override
    public GoogleBuild requestEmail() { return mLogin.requestEmail(); }

//    @Override
//    public GoogleBuild requestGender() { return mLogin.requestGender(); }

    @Override
    public GoogleBuild requestId() { return mLogin.requestId(); }

    @Override
    public GoogleBuild requestProfile() { return mLogin.requestProfile(); }

    @Override
    public GoogleBuild requestServerAuthCode(String s) { return mLogin.requestServerAuthCode( s ); }

    @Override
    public GoogleBuild requestServerAuthCode(String s, boolean b) {
        return mLogin.requestServerAuthCode( s, b );
    }

    @Override
    public GoogleBuild requestScopes(Scope scope, Scope... scopes) {
        return mLogin.requestScopes( scope, scopes );
    }

//    @Override
//    public void startRequestEmail(GoogleSignInAccount gsiAccount, Consumer<List<EmailAddress>> call) {
//        mLogin.startRequestEmail( gsiAccount, call );
//    }
//
//    @Override
//    public void startRequestGenders(GoogleSignInAccount gsiAccount, Consumer<List<Gender>> call) {
//        mLogin.startRequestGenders( gsiAccount, call );
//    }

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

package com.gjjy.login.google;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.gjjy.login.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class GoogleBuild {
    @Retention(RetentionPolicy.SOURCE)
    public @interface SignIn {
        GoogleSignInOptions DEFAULT_SIGN_IN = GoogleSignInOptions.DEFAULT_SIGN_IN;
        GoogleSignInOptions DEFAULT_GAMES_SIGN_IN = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
    }

    private final GoogleSignInOptions.Builder mBuild;
    private final WeakReference<Context> mWRContext;

    private GoogleBuild(Context context, @SignIn GoogleSignInOptions signIn) {
        mWRContext = new WeakReference<>( context );
        mBuild = new GoogleSignInOptions.Builder( signIn );
    }

    /**
     * 以默认的方式登录
     * @return      Build
     */
    public static GoogleBuild create(Context context) {
        return new GoogleBuild( context, SignIn.DEFAULT_SIGN_IN );
    }

    /**
     * 以游戏的方式登录
     * @return      Build
     */
    public static GoogleBuild createGame(Context context) {
        return new GoogleBuild( context, SignIn.DEFAULT_GAMES_SIGN_IN );
    }

    public GoogleBuild requestIdToken() {
        mBuild.requestIdToken( mWRContext.get()
                .getResources()
                .getString( R.string.server_client_id )
        );
        return this;
    }

//    String getEmail() { return "https://www.googleapis.com/auth/userinfo.email"; }
    public GoogleBuild requestEmail() {
        mBuild.requestEmail();
//        mBuild.requestScopes( new Scope( getEmail() ) );
        return this;
    }

//    String getGender() { return "https://www.googleapis.com/auth/user.gender.read"; }
//    public GoogleBuild requestGender() {
//        mBuild.requestScopes( new Scope( getGender() ) );
//        return this;
//    }

    public GoogleBuild requestId() {
        mBuild.requestId();
        return this;
    }

    public GoogleBuild requestProfile() {
        mBuild.requestProfile();
        return this;
    }

    public GoogleBuild requestScopes(Scope scope, Scope... scopes) {
        mBuild.requestScopes( scope, scopes );
        return this;
    }

    public GoogleBuild requestServerAuthCode(String s) {
        mBuild.requestServerAuthCode( s );
        return this;
    }

    public GoogleBuild requestServerAuthCode(String s, boolean b) {
        mBuild.requestServerAuthCode( s, b );
        return this;
    }

    GoogleSignInClient getClient() {
        return GoogleSignIn.getClient( mWRContext.get(), mBuild.build() );
    }
}

package com.gjjy.login.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Scope;

public interface GoogleLogIn {
    static GoogleLogIn create(Context context) { return new GoogleLogInImpl( context ); }

    void registerCallback(@NonNull Activity activity, GoogleCallback call);

//    void startRequestEmail(GoogleSignInAccount gsiAccount, Consumer<List<EmailAddress>> call);
//
//    void startRequestGenders(GoogleSignInAccount gsiAccount, Consumer<List<Gender>> call);

    GoogleBuild requestIdToken();

    GoogleBuild requestEmail();

//    GoogleBuild requestGender();

    GoogleBuild requestId();

    GoogleBuild requestProfile();

    GoogleBuild requestScopes(Scope scope, Scope... scopes);

    GoogleBuild requestServerAuthCode(String s);

    GoogleBuild requestServerAuthCode(String s, boolean b);

    void onClick(View v);

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}

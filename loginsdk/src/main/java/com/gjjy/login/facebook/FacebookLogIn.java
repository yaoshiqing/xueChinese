package com.gjjy.login.facebook;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface FacebookLogIn {
    static FacebookLogIn create() { return new FacebookLogInImpl(); }

    void registerCallback(@NonNull Activity activity, FacebookCallback call);

    void unregisterCallback();

    void setPermissions(String... permissions);

    FacebookBuild  requestEmail();

    FacebookBuild requestPhotoUrl();

    FacebookBuild requestLocation();

    FacebookBuild requestAgeRange();

    FacebookBuild requestBirthday();

    FacebookBuild requestFriends();

    FacebookBuild requestGender();

    FacebookBuild requestLikes();

    FacebookBuild requestLink();

    void onClick(View v);

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}

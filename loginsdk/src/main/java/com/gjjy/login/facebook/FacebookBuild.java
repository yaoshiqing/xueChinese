package com.gjjy.login.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookBuild {
    private final List<String> mPermissions;

    private FacebookBuild() { mPermissions = new ArrayList<>(); }
    public static FacebookBuild create() { return new FacebookBuild(); }

    public FacebookBuild setPermissions(String... permissions) {
        mPermissions.addAll( Arrays.asList(permissions) );
        return this;
    }

    public FacebookBuild requestEmail() {
        mPermissions.add( "email" );
        return this;
    }

    public FacebookBuild requestPhotoUrl() {
        mPermissions.add( "user_photos" );
        return this;
    }

    public FacebookBuild requestLocation() {
        mPermissions.add( "user_location" );
        return this;
    }

    public FacebookBuild requestAgeRange() {
        mPermissions.add( "user_age_range" );
        return this;
    }

    public FacebookBuild requestBirthday() {
        mPermissions.add( "user_birthday" );
        return this;
    }

    public FacebookBuild requestFriends() {
        mPermissions.add( "user_friends" );
        return this;
    }

    public FacebookBuild requestGender() {
        mPermissions.add( "user_gender" );
        return this;
    }

    public FacebookBuild requestLikes() {
        mPermissions.add( "user_likes" );
        return this;
    }

    public FacebookBuild requestLink() {
        mPermissions.add( "user_link" );
        return this;
    }

    List<String> get() { return mPermissions; }
}

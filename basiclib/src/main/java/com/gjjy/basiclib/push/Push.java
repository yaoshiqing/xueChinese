package com.gjjy.basiclib.push;

import androidx.core.util.Consumer;

public class Push {
    private final com.gjjy.pushlib.Push mPush;

    private Push() { mPush = com.gjjy.pushlib.Push.get(); }

    public static Push get() { return Push.HANDLER.I; }
    private static final class HANDLER { private static final Push I = new Push(); }

//    public void start() { mPush.start(); }
//
//    public void stop() { mPush.stop(); }

//    public void setUid(String uid) { mPush.send( ID.UID_TAG, uid ); }
//    public void deleteUld() { mPush.deleteAlias( ID.UID_TAG ); }

    public String getRegistrationID() { return mPush.getRegistrationID(); }

    public void bindRegistrationID(Consumer<String> call) {
        mPush.bindRegistrationID( call );
    }
    public void unBindRegistrationID(Consumer<Boolean> call) { mPush.unBindRegistrationID( call ); }
}
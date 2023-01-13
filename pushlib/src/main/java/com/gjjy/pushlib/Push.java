package com.gjjy.pushlib;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.util.Consumer;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Push {
    private static final String PUSH_TOPIC = "defTopic";
    private FirebaseMessaging mFCM;
    private boolean isDebug = true;
    private String mRegistrationID;
    private int isBindState = 0;    //0：未绑定，1：已绑定，2：正在绑定

    private Push() { }

    public static Push get() { return HANDLER.I; }
    private static final class HANDLER { private static final Push I = new Push(); }

    public Push init(boolean isDebug) {
        setDebug( isDebug );
        mFCM = FirebaseMessaging.getInstance();
        // FCM 自动初始化功能
        mFCM.setAutoInitEnabled( true );
        bindRegistrationID( null );
        return this;
    }

    public void bindRegistrationID(Consumer<String> call) {
        if( isBindState == 2 ) return;
        String token = getRegistrationID();
        if( !TextUtils.isEmpty( token ) ) {
            if( call != null ) call.accept( token );
            return;
        }
        isBindState = 2;
        //注册Token
        mFCM.getToken().addOnCompleteListener( task -> {
            String r = null;
            try {
                r = task.getResult();
            }catch(Exception e) {
                e.printStackTrace();
            }
            setRegistrationID( task.isSuccessful() ? r : null );
            isBindState = task.isSuccessful() ? 1 : 0;
            if( call != null ) call.accept( r );
        });
    }

    public void unBindRegistrationID(Consumer<Boolean> call) {
        Task<Void> t = mFCM.deleteToken();
        try {
            t.addOnCompleteListener( task -> {
                boolean result = task.isSuccessful();
                if( result ) mRegistrationID = null;
                if( call != null ) call.accept( result );
            } );
        }catch(Exception e) {
            e.printStackTrace();
            if( call != null ) call.accept( false );
        }
    }

    public int getBindState() { return isBindState; }

    public Push setDebug(boolean enable) {
        isDebug = enable;
        return this;
    }

    public boolean isDebug() { return isDebug; }

//    /**
//     * 启用推送
//     */
//    public void start() {
//        mFCM.subscribeToTopic( PUSH_TOPIC ).addOnCompleteListener(task -> logD( "regToken -> " +
//                "isSuccess:" + task.isSuccessful() + " | " +
//                "isComplete:" + task.isComplete()
//        ));
//    }
//
//    /**
//     * 关闭推送
//     */
//    public void stop() {
//        mFCM.unsubscribeFromTopic( PUSH_TOPIC ).addOnCompleteListener(task -> logD( "unToken -> " +
//                "isSuccess:" + task.isSuccessful() + " | " +
//                "isComplete:" + task.isComplete()
//        ));
//    }

    public void setRegistrationID(String id) {
        mRegistrationID = id;
        logD( "registrationID -> " + getRegistrationID() );
    }

    /**
     * 获取注册ID
     */
    public String getRegistrationID() { return mRegistrationID; }

    public void send(Bundle data) {
//        mFCM.send( new RemoteMessage( data ) );
    }

    public void send(String key, String val) {
        Bundle b = new Bundle();
        b.putString( key, val );
        send( b );
    }
    public void send(int key, String val) {
        send( String.valueOf( key ), val );
    }

    private void logD(String s) { if( isDebug ) Log.d( "Push", s ); }
}
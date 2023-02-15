package com.gjjy.xuechinese.receiver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.utils.Constant;
import com.gjjy.xuechinese.ui.MainActivity;
import com.gjjy.pushlib.PushService;
import com.google.firebase.messaging.RemoteMessage;
import com.ybear.ybnetworkutil.network.NetworkConfig;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.handler.Handler;
import com.ybear.ybutils.utils.handler.HandlerManage;
import com.ybear.ybutils.utils.notification.NotificationX;

import java.util.Map;

public class XueChinesePushService extends PushService {
    private static final String TAG = "XueChinese-Push-Service";
    private final Handler mHandler = HandlerManage.create();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        log("onNewToken -> " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        if (map.size() == 0) {
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        Bundle data = new Bundle();
        for (String key : map.keySet()) {
            data.putString(key, map.get(key));
        }
        intent.putExtra(Constant.MAIN_PUSH_BUNDLE, data);

        mHandler.post(() -> NotificationX.get().showNotification(
                (int) (Math.random() * 99999) + 10000,
                NetworkConfig.get().getSmallIcon(),
                map.containsKey("title") ? map.get("title") : null,
                map.containsKey("body") ? map.get("body") : null,
                intent,
                false
        ));
        log(data.toString());
//        if( intent == null ) return;
//        String action = intent.getAction();
//        Bundle data = intent.getExtras();
//
//        log( "onReceive -> Action:" + intent.getAction() );
//        log( "onReceive -> Extras:" + data );
//
//        if( data != null ) {
//            log("onReceive -> Message ID:" + data.getString( JPushInterface.EXTRA_MSG_ID ) );
//        }
//
//        if( TextUtils.isEmpty( action ) ) return;
//        switch ( action ) {
//            case JPushInterface.ACTION_NOTIFICATION_OPENED:
//                if( data == null ) break;
//                StartUtil.startMainActivity( data.getString( JPushInterface.EXTRA_EXTRA ) );
//                break;
//        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        mHandler.post(() -> NotificationX.get().cancelAll());
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }
}

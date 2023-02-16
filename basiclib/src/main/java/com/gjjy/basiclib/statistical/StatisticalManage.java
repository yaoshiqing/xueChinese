package com.gjjy.basiclib.statistical;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.webbridge.AdjustBridge;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;

import java.util.HashMap;
import java.util.Map;

public class StatisticalManage {
    private Application mApp;
    private FirebaseAnalytics mAnalytics;

    private StatisticalManage() {
    }

    public static StatisticalManage get() {
        return HANDLER.I;
    }

    private static final class HANDLER {
        private static final StatisticalManage I = new StatisticalManage();
    }

    public StatisticalManage init(@NonNull Application app, boolean isDebug) {
        mApp = app;
        initAnalytics(isDebug);
        initAdjust(isDebug);
        initAppsFlyer(isDebug);

        app.registerActivityLifecycleCallbacks(new IActivityLifecycle() {
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                onResumeOfFragment(activity.getClass().getSimpleName());
                Adjust.onResume();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                onPauseOfFragment(activity.getClass().getSimpleName());
                Adjust.onPause();
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                AdjustBridge.unregister();
            }
        });
        return this;
    }

    private void initAnalytics(boolean isDebug) {
        mAnalytics = FirebaseAnalytics.getInstance(mApp);
        //Analytics 数据收集
        mAnalytics.setAnalyticsCollectionEnabled(!isDebug);
    }

    private void initAdjust(boolean isDebug) {
        String appToken = getKeyAndCheck("Adjust", "STATISICAL_ADJUST_APP_TOKEN");
        AdjustConfig mConfig = new AdjustConfig(
                mApp, appToken,
                isDebug ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION,
                !isDebug
        );
        //事件发送成功
        mConfig.setOnEventTrackingSucceededListener(success ->
                LogUtil.d("Adjust -> onFinishedEventTrackingSucceeded:" + success)
        );
        //事件发送失败
        mConfig.setOnEventTrackingFailedListener(failure ->
                LogUtil.e("Adjust -> onFinishedEventTrackingFailed:" + failure)
        );
        mConfig.setLogLevel(isDebug ? LogLevel.WARN : LogLevel.SUPRESS);
        Adjust.onCreate(mConfig);
    }

    private void initAppsFlyer(boolean isDebug) {
        String key = getKeyAndCheck("AppsFlyer", "STATISICAL_APPS_FLYER_KEY");

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    LogUtil.d("AppsFlyer", "onConversionDataSuccess -> " +
                            "attribute: " + attrName + " = " + conversionData.get(attrName)
                    );
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                LogUtil.e("AppsFlyer", "onConversionDataFail -> " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    LogUtil.d("AppsFlyer", "onAppOpenAttribution -> " +
                            "attribute: " + attrName + " = " + attributionData.get(attrName)
                    );
                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                LogUtil.e("AppsFlyer", "onAttributionFailure -> " + errorMessage);
            }
        };

        AppsFlyerLib afl = AppsFlyerLib.getInstance();
        afl.init(key, conversionListener, mApp);
        afl.setDebugLog(isDebug);
        afl.start(mApp);
    }

    @NonNull
    private String getKeyAndCheck(String libName, String mateName) {
        String key = AppUtil.getMetaDataForString(mApp, mateName);
        if (key == null || TextUtils.isEmpty(key)) {
            throw new NullPointerException(String.format(
                    "StatisticalManage -> Not set key for %s, The key of meta-data should be \"%s\"",
                    libName, mateName
            ));
        }
        return key;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void registerWebView(WebView webView) {
        if (webView == null) return;
        webView.getSettings().setJavaScriptEnabled(true);
        AdjustBridge.registerAndGetInstance(mApp, webView);
    }

    /**
     * Activity生命周期处于onResume时
     *
     * @param name 上下文
     */
    public void onResumeOfFragment(String name) {
        Bundle b = new Bundle();
        b.putString("on_resume", name);
        onEvent("page_action", b);
    }

    /**
     * Activity生命周期处于onPause时
     *
     * @param name 上下文
     */
    public void onPauseOfFragment(String name) {
        Bundle b = new Bundle();
        b.putString("on_pause", name);
        onEvent("page_action", b);
    }

    /**
     * 登录id
     *
     * @param id 登录id
     */
    public void signIn(String method, String id) {
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.METHOD, method);
        b.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        mAnalytics.setUserId(id);
        onEvent(FirebaseAnalytics.Event.LOGIN, b);

        AdjustEvent adjustEvent = new AdjustEvent(getKeyAndCheck(
                "Adjust", "STATISICAL_ADJUST_EVENT_LOGIN"
        ));

//        adjustEvent.addPartnerParameter( "user_id", id );
//        adjustEvent.addPartnerParameter( "method", method );
        Adjust.trackEvent(adjustEvent);
    }

    public void buyVip(String googleOrderId, String name, String price, String introductoryPrice, String currency, String type) {
        double priceOfDouble = ObjUtils.parseDouble(price);
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.TRANSACTION_ID, googleOrderId);
        b.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        b.putDouble(FirebaseAnalytics.Param.PRICE, priceOfDouble);
        b.putDouble(FirebaseAnalytics.Param.VALUE, priceOfDouble);
        b.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, type);
        b.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        b.putString("introductory_price", introductoryPrice);
        onEvent(FirebaseAnalytics.Event.PURCHASE, b);

        AdjustEvent adjustEvent = new AdjustEvent(getKeyAndCheck(
                "Adjust", "STATISICAL_ADJUST_EVENT_IN_APP_PURCHASE"
        ));
        adjustEvent.setOrderId(googleOrderId);
        adjustEvent.setRevenue(priceOfDouble, currency);
        Adjust.trackEvent(adjustEvent);
    }

    /**
     * 浏览页面
     */
    public void reviewAppPage(String activityName) {
        AdjustEvent adjustEvent = new AdjustEvent(getKeyAndCheck(
                "Adjust", "STATISICAL_ADJUST_EVENT_PAGE_VIEW"
        ));
        adjustEvent.addPartnerParameter("activity", activityName);
        Adjust.trackEvent(adjustEvent);
    }

    // adjust 注册事件
    public void registerEvent(String userid) {
        AdjustEvent adjustEvent = new AdjustEvent(getKeyAndCheck(
                "Adjust", "STATISICAL_ADJUST_EVENT_REGISTER_ACCOUNT"
        ));
        adjustEvent.addPartnerParameter("user_id", userid);
        Adjust.trackEvent(adjustEvent);
    }

    /**
     * 退出
     */
    public void onProfileSignOff(String id) {
//        Bundle b = new Bundle();
//        b.putString( "on_sign_off", id );
//        onEvent( FirebaseAnalytics.Event., b );
    }

    /**
     * 退出时保存
     */
    public void onKillProcess() {
    }

    /**
     * 上传错误异常
     *
     * @param error 错误异常
     */
    public void reportError(String error) {
    }

    private void onEvent(String eventId, Bundle p1, Map<String, Object> p2) {
        //2个参数二选一
        if (p1 == null) {
            p1 = mapToBundle(p2);
        } else if (p2 == null) {
            p2 = new HashMap<>();
            for (String key : p1.keySet()) p2.put(key, p1.get(key));
        }
        //Analytics
        mAnalytics.logEvent(eventId, p1);

        //AppsFlyer
        AppsFlyerLib.getInstance().logEvent(mApp, "AFInAppEventType." + eventId, p2);

        LogUtil.e("onEvent -> EventId:" + eventId + " | Param:" + mapToString(p2));
    }

    /**
     * 自定义事件
     *
     * @param eventId 事件Id
     * @param param   事件描述
     */
    public void onEvent(String eventId, Bundle param) {
        onEvent(eventId, param, null);
    }

    /**
     * 自定义事件
     *
     * @param eventId 事件Id
     * @param param   事件描述
     */
    public void onEvent(String eventId, Map<String, Object> param) {
        onEvent(eventId, null, param);
    }

    /**
     * 自定义事件
     *
     * @param eventId 事件Id
     * @param param   事件描述
     */
    public void onEvent(String eventId, String param) {
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.VALUE, param);
        onEvent(eventId, b, null);
    }

    private String mapToString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String key : map.keySet()) {
            sb.append(key).append(":").append(map.get(key)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private Bundle mapToBundle(Map<String, Object> map) {
        Bundle b = new Bundle();
        for (String key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof Integer) {
                b.putInt(key, ObjUtils.parseInt(val));
                continue;
            }
            if (val instanceof Float) {
                b.putFloat(key, ObjUtils.parseFloat(val));
                continue;
            }
            if (val instanceof Short) {
                b.putShort(key, ObjUtils.parseShort(val));
                continue;
            }
            if (val instanceof Long) {
                b.putLong(key, ObjUtils.parseLong(val));
                continue;
            }
            if (val instanceof Double) {
                b.putDouble(key, ObjUtils.parseDouble(val));
                continue;
            }
            if (val instanceof Byte) {
                b.putByte(key, ObjUtils.parseByte(val));
                continue;
            }
            if (val instanceof Boolean) {
                b.putBoolean(key, ObjUtils.parseBoolean(val));
                continue;
            }
            b.putString(key, ObjUtils.parseString(val));
        }
        return b;
    }
}

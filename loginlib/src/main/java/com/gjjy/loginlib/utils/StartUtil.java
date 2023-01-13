package com.gjjy.loginlib.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.utils.Constant;

public class StartUtil {
    public static void startLoginErrorActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build( "/login/loginErrorActivity" );
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(
                activity,
                p,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN_ERROR
        );
    }

    public static void startLoginActivity(Activity activity,
                                          @PageName String pageName) {
        Postcard p = ARouter.getInstance().build( "/login/loginActivity" );
        LogisticsCenter.completion( p );
        Intent intent = new Intent( activity, p.getDestination() );
        intent.putExtra( Constant.PAGE_NAME, pageName );
        intent.putExtra( Constant.LOGIN_NOT_BACK_BTN, false );

        activity.startActivityForResult(
                intent,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN_RESULT
        );
    }

    public static void startEmailLoginActivity(Activity activity) {
        startEmailLoginActivity( activity, null, null );
    }

    public static void startEmailLoginActivity(Activity activity, String acc, String pwd) {
        Postcard p = ARouter.getInstance().build( "/login/emailLoginActivity" );
        LogisticsCenter.completion( p );
        Intent intent = new Intent( activity, p.getDestination() );

        if( !TextUtils.isEmpty( acc ) && !TextUtils.isEmpty( pwd ) ) {
            intent.putExtra( Constant.EMAIL, acc );
            intent.putExtra( Constant.PASSWORD, pwd );
        }

        activity.startActivityForResult(
                intent,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_EMAIL_LOGIN_RESULT
        );
    }

    public static void startEmailSignUpActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build( "/login/emailSignUpActivity" );
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(
                activity,
                p,
                com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_EMAIL_SIGN_UP_RESULT
        );
    }

    public static void startEmailForgetPasswordActivity() {
        ARouter.getInstance().build("/login/emailForgetPasswordActivity").navigation();
    }
}

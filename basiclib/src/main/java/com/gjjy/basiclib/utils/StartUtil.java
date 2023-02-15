package com.gjjy.basiclib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.ui.activity.PictureViewerActivity;
import com.gjjy.speechsdk.PermManage;

public class StartUtil {
    public static final int REQUEST_CODE_LOGIN_ERROR = 100;
    public static final int REQUEST_CODE_LOGIN_RESULT = 101;
    public static final int REQUEST_CODE_GUIDE_RESULT = 102;
    public static final int REQUEST_CODE_LOGIN = 103;
    public static final int REQUEST_CODE_NEED_LOGIN_RESULT = 104;
    public static final int REQUEST_CODE_EMAIL_LOGIN_RESULT = 105;
    public static final int REQUEST_CODE_EMAIL_SIGN_UP_RESULT = 106;
    public static final int REQUEST_CODE_EXIT_LOGIN_RESULT = 107;
    public static final int REQUEST_CODE_EMAIL_RESET_PASSWORD_RESULT = 108;
    public static final int REQUEST_CODE_GO_TO_HOME = 109;
    public static final int REQUEST_CODE_BUY_VIP = 110;
    public static final int REQUEST_CODE_BUY_VIP_OF_FREE = 111;
    public static final int REQUEST_CODE_SKIP_TEST_LIGHTNING = 112;
    public static final int REQUEST_CODE_TEST_LOGIN = 113;
    public static final int REQUEST_CODE_LACK_LIGHTNING_OF_REFRESH_SCORE = 114;
    public static final int REQUEST_CODE_EXCHANGE_POINTS_RESULT = 115;

    private static int mDCIMPermIndex = 0;

    public static void startDCIM(Context context, int requestCode) {
        PermManage.create((FragmentActivity) context)
                .reqExternalStoragePerm((isGranted, name, shouldShowRequestPermissionRationale) -> {
                    if (isGranted) {
                        mDCIMPermIndex++;
                    }
                    if (mDCIMPermIndex >= 2) {
                        mDCIMPermIndex = 0;
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    }
                });
    }

    public static void startPictureViewerActivity(Context context, Uri uri) {
        Intent intent = new Intent(context, PictureViewerActivity.class);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void startPictureViewerActivity(Context context, String url) {
        Intent intent = new Intent(context, PictureViewerActivity.class);
        intent.putExtra(PictureViewerActivity.Extra.URL, url);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, Postcard p, int requestCode) {
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startTermsActivity() {
        ARouter.getInstance().build("/protocol/protocolActivity")
                .withInt(Constant.AGREEMENT_TYPE, 0)
                .navigation();
    }

    public static void startPolicyActivity() {
        ARouter.getInstance().build("/protocol/protocolActivity")
                .withInt(Constant.AGREEMENT_TYPE, 1)
                .navigation();
    }

    public static void startIntegralRulesActivity() {
        ARouter.getInstance().build("/protocol/protocolActivity")
                .withInt(Constant.AGREEMENT_TYPE, 2)
                .navigation();
    }

    public static void startGuideActivity(Activity activity,
                                          boolean isInitPage,
                                          boolean isShowGuideFrontPage,
                                          boolean isShowLoginPage) {
        Postcard p = ARouter.getInstance().build("/guide/guideActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.GUIDE_INIT_PAGE, isInitPage);
        intent.putExtra(Constant.GUIDE_SHOW_GUIDE_HOME_PAGE, isShowGuideFrontPage);
        intent.putExtra(Constant.GUIDE_SHOW_LOGIN_PAGE, isShowLoginPage);

        activity.startActivityForResult(intent, REQUEST_CODE_GUIDE_RESULT);
    }

    public static void startGuideActivity(Activity activity, boolean isShowGuideFrontPage, boolean isShowLoginPage) {
        startGuideActivity(activity, false, isShowGuideFrontPage, isShowLoginPage);
    }

    public static void startGuideActivity(Activity activity) {
        startGuideActivity(activity, true, true, true);
    }

    public static void startMsgDetailsActivity(Activity activity, String videoId, int id, String title) {
        Postcard p = ARouter.getInstance().build("/message/msgDetailsActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.ID_FOR_INT, id);
        intent.putExtra(Constant.ID_FOR_VIDEO, videoId);
        intent.putExtra(Constant.NAME, title);
        activity.startActivity(intent);
    }

    public static void startTargetedLearningDetailsActivity(long id, int topTalkId, String videoId, int topInteractType) {
        ARouter.getInstance()
                .build("/discovery/TargetedLearningDetailsActivity")
                .withLong(Constant.ID_FOR_INT, id)
                .withString(Constant.TYPE, "2")
                .withString(Constant.ID_FOR_VIDEO, videoId)
                .withInt(Constant.TOP_COMMENT_TALK_ID, topTalkId)
                .withInt(Constant.TOP_COMMENT_INTERACT_TYPE, topInteractType)
                .navigation();
    }

    public static void startMainActivity(Bundle data) {
        ARouter.getInstance().build("/main/mainActivity")
                .withBundle(Constant.MAIN_PUSH_BUNDLE, data)
                .navigation();
    }

    public static void startEmailResetPasswordActivity(Activity activity, String email) {
        Postcard p = ARouter.getInstance().build("/login/emailResetPasswordActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.EMAIL, email);

        activity.startActivityForResult(intent, REQUEST_CODE_EMAIL_RESET_PASSWORD_RESULT);
    }

    public static void startExitAppLoginActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build("/login/exitAppLoginActivity");
        StartUtil.startActivityForResult(activity, p, REQUEST_CODE_EXIT_LOGIN_RESULT);
    }

    public static void startLoginActivity(Activity activity, int requestCode, @PageName String pageName) {
        Postcard p = ARouter.getInstance().build("/login/loginActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.PAGE_NAME, pageName);
        StartUtil.startActivityForResult(activity, p, requestCode);
    }

    public static void startLoginActivity(Activity activity, @PageName String pageName) {
        startLoginActivity(activity, REQUEST_CODE_LOGIN, pageName);
    }

    public static void startBuyVipActivity(Activity activity, boolean isFree) {
        Postcard p = ARouter.getInstance().build("/vip/buyVipActivity");
        StartUtil.startActivityForResult(activity, p, isFree ? REQUEST_CODE_BUY_VIP_OF_FREE : REQUEST_CODE_BUY_VIP);
    }

    public static void startBuyVipActivity(Activity activity) {
        startBuyVipActivity(activity, false);
    }

    /**
     * 积分详情页面
     */
    public static void startIntegralDetailsActivity() {
        ARouter.getInstance().build("/userCenter/integralDetailsActivity").navigation();
    }

    /**
     * 积分页面
     */
    public static void startIntegralActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build("/userCenter/integralActivity");
        StartUtil.startActivityForResult(activity, p, REQUEST_CODE_EXCHANGE_POINTS_RESULT);
    }
}

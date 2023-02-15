package com.gjjy.usercenterlib;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.utils.Constant;

public class StartUtil {

    public static void startMsgCenterActivity() {
        ARouter.getInstance().build("/message/msgCenterActivity").navigation();
    }

    public static void startMsgSystemListActivity(Activity activity) {
        startMsgListActivity(activity, "1");
    }

    public static void startMsgVipListActivity(Activity activity) {
        startMsgListActivity(activity, "2");
    }

    public static void startMsgInteractiveListActivity(Activity activity) {
        startMsgListActivity(activity, "3");
    }

    private static void startMsgListActivity(Activity activity, String type) {
        Postcard p = ARouter.getInstance().build("/message/msgListActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }

    public static void startAchievementActivity(Activity activity, int xpNum) {
        Postcard p = ARouter.getInstance().build("/userCenter/achievementActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.XP_NUM, xpNum);
        activity.startActivity(intent);

        // ARouter.getInstance().build("/userCenter/achievementActivity").navigation();
    }

    public static void startSettingActivity(Activity activity) {
        Postcard p = ARouter.getInstance().build("/userCenter/setUpActivity");
        com.gjjy.basiclib.utils.StartUtil.startActivityForResult(activity, p, com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_GO_TO_HOME);
    }

    public static void startLearningReminderActivity() {
        ARouter.getInstance().build("/userCenter/learningReminderActivity").navigation();
    }

    public static void startFeedbackActivity() {
        ARouter.getInstance().build("/userCenter/feedbackActivity").navigation();
    }

    public static void startEmailUpdatePasswordActivity() {
        ARouter.getInstance().build("/login/emailUpdatePasswordActivity").navigation();
    }

    public static void startLoginActivity(Activity activity, @PageName String pageName, boolean isBackBtn) {
        Postcard p = ARouter.getInstance().build("/login/loginActivity");
        LogisticsCenter.completion(p);
        Intent intent = new Intent(activity, p.getDestination());
        intent.putExtra(Constant.PAGE_NAME, pageName);
        intent.putExtra(Constant.LOGIN_NOT_BACK_BTN, !isBackBtn);

        activity.startActivityForResult(intent, com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN);
    }

    public static void startRankingActivity() {
        ARouter.getInstance().build("/userCenter/rankingActivity").navigation();
    }

    public static void startFriendsActivity() {
        ARouter.getInstance().build("/userCenter/friendsActivity").navigation();
    }

    public static void startInviteFriendsActivity() {
        ARouter.getInstance().build("/userCenter/inviteFriendsActivity").navigation();
    }

    public static void startVouchersActivity() {
        ARouter.getInstance().build("/userCenter/vouchersActivity").navigation();
    }
}
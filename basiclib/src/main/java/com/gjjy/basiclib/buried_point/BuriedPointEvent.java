package com.gjjy.basiclib.buried_point;

import android.content.Context;

import androidx.annotation.IntRange;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.statistical.StatisticalManage;

import java.util.HashMap;
import java.util.Map;

/**
 埋点
 */
public class BuriedPointEvent {
    private final StatisticalManage mManage;
    private BuriedPointEvent() {
        mManage = StatisticalManage.get();
    }
    public static BuriedPointEvent get() { return BuriedPointEvent.HANDLER.I; }
    private static final class HANDLER {
        private static final BuriedPointEvent I = new BuriedPointEvent();
    }

    private boolean isTouchFirstMeeting = false;

    private void baseBuriedPoint(Map<String, Object> map, boolean enableClick) {
        if( enableClick ) map.put( "clickrate", "" );
        map.put( "country", Config.getLang() );
    }
    
    private String toResultValue(boolean result) { return result ? "成功" : "失败"; }

    private String toStatus(boolean status) { return status ? "打开" : "关闭"; }

    private String getUserIdentity(boolean isGuest, boolean isVip) {
        //游客
        if( isGuest ) return "游客";
        //开了会员和没开会员
        return isVip ? "会员" : "非游客|非会员";
    }

    private String toRefreshRecordResult(@IntRange(from = -2) int result) {
        switch( result ) {
            case 0:
                return "挑战成功";
            case -1:
                return "挑战失败";
            case -2:
                return "挑战持平";
            default:
                return String.format( "中断位置(%s)", result );
        }
    }

    /**
     * 初次见面按钮_引导页1
     * @param context       上下文
     */
    public void onGuidePageOfFirstMeeting(Context context) {
        if( isTouchFirstMeeting ) return;
        isTouchFirstMeeting = true;
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "FirstMeeting_guidepage1", map );
    }

    private boolean isTouchHaveAccount = false;
    /**
     * 已有账号按钮_引导页1
     * @param context       上下文
     */
    public void onGuidePageOfHaveAccount(Context context) {
        if( isTouchHaveAccount ) return;
        isTouchHaveAccount = true;
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "HaveAccount_guidepage1", map );
    }

    /**
     * 语言程度选择_语言程度页
     * @param context       上下文
     * @param levelType     类型
     */
    public void onLevelPageOfLanguageLevel(Context context, int levelType) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "LanguageLevel", String.valueOf( levelType ) );
        mManage.onEvent( "LanguageLevel_levelpage", map );
    }

    /**
     * 目的选择_学习目的页
     * @param context       上下文
     * @param levelType     类型
     */
    public void onPurposePageOfPurposeSelection(Context context, int levelType) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "purposeName", String.valueOf( levelType ) );
        mManage.onEvent( "PurposeSelection_purposePage", map );
    }

    /**
     * 登录提醒页 - 登录按钮_是否登录页
     * @param context       上下文
     * @param pageName      页面名称
     */
    public void onWhetherLoginOfLogin(Context context, @PageName String pageName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "PageName", pageName );
        mManage.onEvent( "Login_whetherLogin", map );
    }

    /**
     * 登录提醒页 - 稍后再说按钮_是否登录页
     * @param context       上下文
     * @param pageName      页面名称
     */
    public void onWhetherLoginOfLater(Context context, @PageName String pageName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "PageName", pageName );
        mManage.onEvent( "Later_whetherLogin", map );
    }

    /**
     * 登录方式按钮_登录注册
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param loginType     登录类型
     * @param page          页面名称
     */
    public void onRegisterLoginOfLoginMethod(Context context,
                                             String uid,
                                             String userName,
                                             @LoginType String loginType,
                                             @PageName String page) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "loginMethod", loginType );
        map.put( "PageName", page );
        mManage.onEvent( "LoginMethod_RegisterLogin", map );
    }

    /**
     * 登录按钮_邮箱登录页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        登录结果
     */
    public void onEmailLoginPageOfLoginButton(Context context,
                                             String uid,
                                             String userName,
                                             boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "LoginButton_EmailLoginPage", map );
    }

    /**
     * 注册入口_邮箱登录页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onEmailLoginPageOfSignUpAccess(Context context,
                                              String uid,
                                              String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "SignUpAccess_EmailLoginPage", map );
    }

    /**
     * 忘记密码按钮_邮箱登录页
     * @param context       上下文
     */
    public void onEmailLoginPageOfForgetPassword(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ForgetPassword_EmailLoginPage", map );
    }

    /**
     * 注册按钮_邮箱注册页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        登录结果
     */
    public void onEmailLoginPageOfSignUpButton(Context context,
                                               String uid,
                                               String userName,
                                               boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "SignUpButton_EmailSignUpPage", map );
    }

    /**
     * 确定按钮_忘记密码页
     * @param context       上下文
     */
    public void onForgetPasswordPageOfConfirmButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ConfirmButton_ForgetPasswordPage", map );
    }

    /**
     * 再次发送按钮_发送验证码页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        登录结果
     */
    public void onSendVerificationCodePageOfResentButton(Context context,
                                               String uid,
                                               String userName,
                                               boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "ResentButton_SendVerificationCodePage", map );
    }

    /**
     * 保存按钮_发送验证码页
     * @param context       上下文
     * @param result        登录结果
     */
    public void onSendVerificationCodePageSaveButton(Context context,
                                                     boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "SaveButton_SendVerificationCodePage", map );
    }

    /**
     * 选择分类_课程列表
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param categoryId    分类Id
     * @param categoryName  分类名称
     */
    public void onCourseListOfSelectionSort(Context context,
                                            String uid,
                                            String userName,
                                            int categoryId,
                                            String categoryName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        mManage.onEvent( "SelectionSort_courseList", map );
    }

    /**
     * 题型开关_学习详情页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param unitId        模块Id
     * @param unitName      模块名称
     * @param categoryId    分类Id
     * @param categoryName  分类名称
     * @param levelId       阶段Id
     * @param levelName     阶段名称
     */
    public void onCourseListOfModule(Context context,
                                     String uid,
                                     String userName,
                                     int unitId,
                                     String unitName,
                                     int categoryId,
                                     String categoryName,
                                     int levelId,
                                     String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "Module_courseList", map );
    }

    /**
     * 检验按钮_学习详情页
     * @param context       上下文
     */
    public void onStudyPageOfMoreSetUp(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "MoreSetting_studyPage", map );
    }

    /**
     * 小测验_阶段_课程列表
     * @param context       上下文
     * @param isOn          开关结果
     * @param switchName    开关名称
     * @param pageName      页面名称
     */
    public void onStudyPageOfQuestionTypeSwitch(Context context,
                                                boolean isOn,
                                                @QuestionType String switchName,
                                                @PageName String pageName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "status", toStatus( isOn ) );
        map.put( "statusAmount", "1" );
        map.put( "switchName", switchName );
        map.put( "PageName", pageName );
        mManage.onEvent( "QuestionTypeSwitch_studyPage", map );
    }

    /**
     * 检验按钮_学习详情页
     * @param context       上下文
     * @param categoryId    分类Id
     * @param categoryName  分类名称
     * @param levelId       阶段Id
     * @param levelName     阶段名称
     * @param unitId        模块Id
     * @param unitName      模块名称
     */
    public void onStudyPageOfCheckButton(Context context,
                                         boolean isOn,
                                         int questionTypeId,
                                         String questionTypeName,
                                         int questionId,
                                         int categoryId,
                                         String categoryName,
                                         int levelId,
                                         String levelName,
                                         int unitId,
                                         String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "status", toStatus( isOn ) );
        map.put( "questionTypeID", String.valueOf( questionTypeId ) );
        map.put( "questionTypeName", questionTypeName );
        map.put( "questionID", String.valueOf( questionId ) );

        mManage.onEvent( "CheckButton_studyPage", map );
    }

    /**
     * 开始学习按钮_课程介绍页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param levelId       阶段Id
     * @param levelName     阶段名称
     */
    public void onCourseListOfTestOutPhase(Context context,
                                           String uid,
                                           String userName,
                                           int levelId,
                                           String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "testoutID", String.valueOf( levelId ) );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "Testout_phase_courseList", map );
    }

    /**
     * 跳级考试按钮_课程介绍页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param viewCount     浏览量
     * @param viewDuration  浏览时长
     * @param unitId        模块Id
     */
    public void onModuleOfCourseDescription(Context context,
                                             String uid,
                                             String userName,
                                             int viewCount,
                                             long viewDuration,
                                             int unitId,
                                            String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "pageview", String.valueOf( viewCount ) );
        map.put( "viewingDuration", String.valueOf( viewDuration ) );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        mManage.onEvent( "CourseDescription_Module", map );
    }

    /**
     * 开始考试按钮_课程介绍页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onCourseDescriptionOfStartButton(Context context,
                                                 String uid,
                                                 String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "StartButton_courseDescription", map );
    }
    
    /**
     * 跳级考试按钮_课程介绍页
     * @param context       上下文
     * @param skipTestId    小测验Id
     * @param result        处理结果
     */
    public void onCourseDescriptionOfPassTestButton(Context context,
                                                    int skipTestId,
                                                    boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "passTestID", String.valueOf( skipTestId ) );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "PasstestButton_courseDescription", map );
    }

    /**
     * 继续按钮_小节完成页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        处理结果
     */
    public void onCourseDescriptionOfStartTestButton(Context context,
                                                     String uid,
                                                     String userName,
                                                     boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "StartTestButton_courseDescription", map );
    }

    /**
     * 复习按钮_课程列表
     * @param context       上下文
     */
    public void onCourseDescriptionOfCancelTestButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CancelTestButton_courseDescription", map );
    }

    /**
     * 继续按钮_小节完成页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param sectionId     小节Id
     * @param sectionName   小节名称
     */
    public void onSectionCompletionPageOfContinueButton(Context context,
                                                        String uid,
                                                        String userName,
                                                        int sectionId,
                                                        String sectionName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "SectionID", String.valueOf( sectionId ) );
        map.put( "SectionName", sectionName );
        mManage.onEvent( "ContinueButton_SectionCompletionPage", map );
    }

    /**
     * 复习分类_选择分类页_复习模块
     * @param context       上下文
     */
    public void onCourseListOfReviewButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ReviewButton_courseList", map );
    }

    /**
     * 快速复习_复习模块
     * @param context       上下文
     */
    public void onReviewModuleOfQuickReview(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "QuickReview_ReviewModule", map );
    }

    /**
     * 复习分类_选择分类页_复习模块
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param categoryId    分类Id
     * @param categoryName  分类名称
     */
    public void onSelectionSortOfReviewSort(Context context,
                                            String uid,
                                            String userName,
                                            int categoryId,
                                            String categoryName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "reviewSortID", String.valueOf( categoryId ) );
        map.put( "reviewSortName", categoryName );
        mManage.onEvent( "ReviewSort_selectionSort", map );
    }

    /**
     * 继续按钮_阶段复习完成页
     * @param context       上下文
     */
    public void onPhaseReviewCompletionPageOfContinueButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ContinueButton_phaseReviewCompletionPage", map );
    }

    /**
     * 错题集_复习模块
     * @param context       上下文
     */
    public void onReviewModuleOfMistakes(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "Mistakes_ReviewModule", map );
    }

    /**
     * 我的奖励按钮_个人中心页
     * @param context       上下文
     */
    public void onMePageOfAchievementButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "achievementButton_MePage", map );
    }

    /**
     * 设置按钮_个人中心页
     * @param context       上下文
     */
    public void onMePageOfSetUpButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "SettingButton_MePage", map );
    }

    /**
     * 编辑头像_设置页
     * @param context       上下文
     */
    public void onSetUpPageOfEditPicture(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "EditPicture_settingPage", map );
    }

    /**
     * 编辑昵称_设置页
     * @param context       上下文
     */
    public void onSetUpPageOfEditName(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "EditName_settingPage", map );
    }

    /**
     * 题型开关_设置页
     * @param context       上下文
     * @param isOn          开关结果
     * @param switchName    开关名称
     * @param pageName      页面名称
     */
    public void onSetUpPageOfQuestionTypeSwitch(Context context,
                                                  boolean isOn,
                                                  @QuestionType String switchName,
                                                  @PageName String pageName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "status", toStatus( isOn ) );
        map.put( "statusAmount", "1" );
        map.put( "switchName", switchName );
        map.put( "PageName", pageName );
        mManage.onEvent( "QuestionTypeSwitch_settingPage", map );
    }

    /**
     * 退出账号按钮_设置页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        处理结果
     */
    public void onSetUpPageOfLogOffButton(Context context,
                                            String uid,
                                            String userName,
                                            boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "LogOffButton_settingPage", map );
    }

    /**
     * 用户在设置页点击学习提醒时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onSetUpPageOfLearningReminder(Context context,
                                                String uid,
                                                String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "LearningReminder_settingPage", map );
    }

    public void onLearningReminderPageOfLearningReminderSwitch(Context context,
                                                               boolean switchStatus) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "switchStatus", toStatus( switchStatus ) );
        map.put( "statusAmount", "1" );
        mManage.onEvent( "LearningReminderSwitch_LearningReminderPage", map );
    }

    /**
     * 重置密码_设置页
     * @param context       上下文
     */
    public void onSetUpPageOfResetPassword(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ResetPassword_settingPage", map );
    }

    /**
     * 消息入口_个人中心页
     * @param context       上下文
     */
    public void onMePageOfNotificationAccess(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "NotificationAccess_MePage", map );
    }

    /**
     * 关闭按钮_消息中心页
     * @param context       上下文
     */
    public void onNotificationCenterPageOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_NotificationCenterPage", map );
    }

    /**
     * 开启按钮_消息中心页
     * @param context       上下文
     * @param result        处理结果
     */
    public void onNotificationCenterPageOfOpenButton(Context context, boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "OpenButton_NotificationCenterPage", map );
    }

    /**
     * 选择消息类型_消息中心页
     * @param context               上下文
     * @param notificationName      通知名称
     */
    public void onNotificationCenterPageOfChoseNotificationType(Context context,
                                                                String notificationName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "NotificationName", notificationName );
        mManage.onEvent( "ChoseNotificationType_NotificationCenterPage", map );
    }

    /**
     * 查看消息_消息详情页_消息列表页
     * @param context               上下文
     * @param notificationId        通知id
     * @param notificationTitle     通知标题
     * @param userId                用户id
     * @param userName              用户名称
     * @param duration              流程时长
     */
    public void onNotificationListPageOfNotificationDetailsPage(Context context,
                                                                int notificationId,
                                                                String notificationTitle,
                                                                String userId,
                                                                String userName,
                                                                long duration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "NotificationID", String.valueOf( notificationId ) );
        map.put( "NotificationTitle", notificationTitle );
        map.put( "userID", userId );
        map.put( "userName", userName );
        map.put( "viewingDuration", String.valueOf( duration ) );

        mManage.onEvent( "CheckNotification_NotificationDetailsPage_NotificationListPage", map );
    }

    /**
     * 登录注册按钮_游客个人中心页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onVisitorMePageOfSignupLoginButton(Context context,
                                                   String uid,
                                                   String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "SignupLoginButton_visitorMePage", map );
    }


    /* v1.1.1 */

    /**
     * 介绍模块_日常类课程列表
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param categoryId    分类Id
     * @param categoryName  分类名称
     * @param levelId       阶段Id
     * @param levelName     阶段名称
     * @param unitId        模块Id
     * @param unitName      模块名称
     */
    public void onDailyCourseListOfPrefaceModule(Context context,
                                                 String uid,
                                                 String userName,
                                                 int categoryId,
                                                 String categoryName,
                                                 int levelId,
                                                 String levelName,
                                                 int unitId,
                                                 String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );

        mManage.onEvent( "PrefaceModule_DailycourseList", map );
    }

    /**
     * 关闭按钮_汉语介绍页_介绍模块
     * @param context       上下文
     */
    public void onPrefaceModuleMandarinIntroducePageOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_MandarinIntroducePage_PrefaceModule", map );
    }

    /**
     * 关闭按钮_拼音介绍页_介绍模块
     * @param context       上下文
     */
    public void onPrefaceModulePinyinIntroducePageOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_PinyinIntroducePage_PrefaceModule", map );
    }

    /**
     * 关闭按钮_声调介绍页_介绍模块
     * @param context       上下文
     */
    public void onPrefaceModuleTonesIntroducePageOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_TonesIntroducePage_PrefaceModule", map );
    }

    /**
     * 继续按钮_模块首次完成页_介绍模块
     * @param context       上下文
     */
    public void onPrefaceModuleFirstModuleCompletePageOfContinueButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ContinueButton_FirstModuleCompletePage_PrefaceModule", map );
    }

    /**
     * 继续按钮_模块再次完成页_介绍模块
     * @param context       上下文
     */
    public void onPrefaceModuleRepetitionModuleCompletePageOfContinueButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ContinueButton_RepetitionModuleCompletePage_PrefaceModule", map );
    }

    /**
     * 继续按钮_学习详情页
     * @param context           上下文
     * @param questionTypeId    题型Id
     * @param questionTypeName  题型名称
     * @param questionId        题目Id
     * @param categoryId        分类Id
     * @param categoryName      分类名称
     * @param levelId           阶段Id
     * @param levelName         阶段名称
     * @param unitId            模块Id
     * @param unitName          模块名称
     */
    public void onStudyPageOfContinueButton(Context context,
                                            int questionTypeId,
                                            String questionTypeName,
                                            int questionId,
                                            int categoryId,
                                            String categoryName,
                                            int levelId,
                                            String levelName,
                                            int unitId,
                                            String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "questionTypeID", String.valueOf( questionTypeId ) );
        map.put( "questionTypeName", questionTypeName );
        map.put( "questionID", String.valueOf( questionId ) );

        mManage.onEvent( "ContinueButton_studyPage", map );
    }

    /**
     * 录音按钮_口语题_学习详情页
     * @param context           上下文
     * @param questionId        题目Id
     * @param categoryId        分类Id
     * @param categoryName      分类名称
     * @param levelId           阶段Id
     * @param levelName         阶段名称
     */
    public void onStudyPageSpeakingOfRecordButton(Context context,
                                                  int questionId,
                                                  int categoryId,
                                                  String categoryName,
                                                  int levelId,
                                                  String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "questionID", String.valueOf( questionId ) );
        map.put( "testoutID", String.valueOf( levelId ) );

        mManage.onEvent( "RecordButton_Speaking_studyPage", map );
    }

    /**
     * 关闭按钮_小测验
     * @param context           上下文
     * @param uid               用户id
     * @param userName          用户名称
     * @param categoryId        分类Id
     * @param categoryName      分类名称
     * @param levelId           阶段Id
     * @param levelName         阶段名称
     */
    public void onTestoutOfCloseButton(Context context,
                                       String uid,
                                       String userName,
                                       int categoryId,
                                       String categoryName,
                                       int levelId,
                                       String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "testoutID", String.valueOf( levelId ) );

        mManage.onEvent( "CloseButton_testout", map );
    }

    /**
     * 检验按钮_小测验学习详情页
     * @param context           上下文
     * @param isOn              答题结果
     * @param questionTypeId    题型Id
     * @param questionTypeName  题型名称
     * @param questionId        题目Id
     * @param categoryId        分类Id
     * @param categoryName      分类名称
     * @param levelId           阶段Id
     * @param levelName         阶段名称
     * @param unitId            模块Id
     * @param unitName          模块名称
     */
    public void onTestOutStudyPageOfCheckButton(Context context,
                                                boolean isOn,
                                                int questionTypeId,
                                                String questionTypeName,
                                                int questionId,
                                                int categoryId,
                                                String categoryName,
                                                int levelId,
                                                String levelName,
                                                int unitId,
                                                String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "testoutID", String.valueOf( levelId ) );
        map.put( "questionTypeID", String.valueOf( questionTypeId ) );
        map.put( "questionTypeName", questionTypeName );
        map.put( "questionID", String.valueOf( questionId ) );
        map.put( "status", toStatus( isOn ) );

        mManage.onEvent( "CheckButton_testoutstudyPage", map );
    }

    /**
     * 继续按钮_小测验学习详情页
     * @param context           上下文
     * @param isOn              答题结果
     * @param questionTypeId    题型Id
     * @param questionTypeName  题型名称
     * @param questionId        题目Id
     * @param categoryId        分类Id
     * @param categoryName      分类名称
     * @param levelId           阶段Id
     * @param levelName         阶段名称
     */
    public void onTestOutStudyPageOfContinueButton(Context context,
                                                   boolean isOn,
                                                   int questionTypeId,
                                                   String questionTypeName,
                                                   int questionId,
                                                   int categoryId,
                                                   String categoryName,
                                                   int levelId,
                                                   String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "testoutID", String.valueOf( levelId ) );
        map.put( "questionTypeID", String.valueOf( questionTypeId ) );
        map.put( "questionTypeName", questionTypeName );
        map.put( "questionID", String.valueOf( questionId ) );
        map.put( "status", toStatus( isOn ) );

        mManage.onEvent( "ContinueButton_testoutstudyPage", map );
    }

    /**
     * 词语表_复习模块
     * @param context       上下文
     */
    public void onReviewModuleOfWordsList(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "WordsList_ReviewModule", map );
    }

    /**
     * 搜索按钮_词语表页_复习模块
     * @param context       上下文
     */
    public void onReviewModuleOfSearchButtonWordsListPage(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "SearchButton_WordsListPage_ReviewModule", map );
    }

    /**
     * 发现按钮_底部导航
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onTabBarOfDiscoveryButton(Context context,
                                          String uid,
                                          String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "DiscoveryButton_TabBar", map );
    }

    /**
     * 我的课程按钮_发现页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onDiscoveryPageOfMyLessons(Context context,
                                          String uid,
                                          String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "MyLessons_DiscoveryPage", map );
    }

    /**
     * 更多按钮_每日聆听_发现页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onDiscoveryPageOfMoreButtonListenEveryday(Context context,
                                                          String uid,
                                                          String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "MoreButton_ListenEveryday_DiscoveryPage", map );
    }

    /**
     * 每日聆听课程_发现页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onDiscoveryPageOfListenEverydayLessons(Context context,
                                                       String uid,
                                                       String userName,
                                                       long lessonId,
                                                       String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "ListenEverydayLessons_DiscoveryPage", map );
    }

    /**
     * 更多按钮_热门视频_发现页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onDiscoveryPageOfMoreButtonPopularVideos(Context context,
                                                          String uid,
                                                          String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "MoreButton_PopularVideos_DiscoveryPage", map );
    }

    /**
     * 热门视频课程_发现页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onDiscoveryPageOfPopularVideoLessons(Context context,
                                                     String uid,
                                                     String userName,
                                                     long lessonId,
                                                     String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "PopularVideoLessons_DiscoveryPage", map );
    }

    /**
     * 每日聆听课程_每日聆听课程列表
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     */
    public void onListenEverydayLessonsListOfListenEverydayLessons(Context context,
                                                                   String uid,
                                                                   String userName,
                                                                   long lessonId,
                                                                   String lessonName,
                                                                   long duration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( duration ) );
        mManage.onEvent( "ListenEverydayLessons_ListenEverydayLessonsList", map );
    }

    /**
     * 收藏按钮_每日聆听详情页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onListenEverydayDetailPageOfCollection(Context context,
                                                       boolean isOn,
                                                       String uid,
                                                       String userName,
                                                       long lessonId,
                                                       String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "Collection_ListenEverydayDetailpage", map );
    }

    /**
     * 转发按钮_每日聆听详情页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onListenEverydayDetailPageOfTranspondButton(Context context,
                                                       boolean isOn,
                                                       String uid,
                                                       String userName,
                                                       long lessonId,
                                                       String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "TranspondButton_ListenEverydayDetailpage", map );
    }

    /**
     * 热门视频课程_热门视频课程列表
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onPopularVideoListOfPopularVideoLessons(Context context,
                                                        String uid,
                                                        String userName,
                                                        long lessonId,
                                                        String lessonName,
                                                        long duration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( duration ) );
        mManage.onEvent( "PopularVideoLessons_PopularVideoList", map );
    }

    /**
     * 收藏按钮_热门视频详情页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onPopularVideoDetailPageOfCollection(Context context,
                                                     boolean isOn,
                                                     String uid,
                                                     String userName,
                                                     long lessonId,
                                                     String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "Collection_PopularVideoDetailpage", map );
    }

    /**
     * 转发按钮_热门视频详情页
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param lessonId      课程Id
     * @param lessonName    课程名称
     */
    public void onPopularVideoDetailPageOfTranspondButton(Context context,
                                                          boolean isOn,
                                                          String uid,
                                                          String userName,
                                                          long lessonId,
                                                          String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "TranspondButton_PopularVideoDetailpage", map );
    }

    /* v1.1.2 */

    /**
     练习模式按钮_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     @param unitId              模块Id
     @param unitName            模块名称
     */
    public void onCourseDescriptionOfPracticeButton(Context context,
                                                    String uid,
                                                    String userName,
                                                    int categoryId,
                                                    String categoryName,
                                                    int levelId,
                                                    String levelName,
                                                    int unitId,
                                                    String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "PracticeButton_courseDescription", map );
    }

    /**
     速学模式按钮_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     @param unitId              模块Id
     @param unitName            模块名称
     */
    public void onCourseDescriptionOfQuickLearn(Context context,
                                                String uid,
                                                String userName,
                                                int categoryId,
                                                String categoryName,
                                                int levelId,
                                                String levelName,
                                                int unitId,
                                                String unitName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "QuickLearn_courseDescription", map );
    }

    /**
     开始按钮_速学弹窗_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onCourseDescriptionOfQuickLearnPopupOfStartButton(Context context,
                                                                  String uid,
                                                                  String userName,
                                                                  int unitId,
                                                                  String unitName,
                                                                  int categoryId,
                                                                  String categoryName,
                                                                  int levelId,
                                                                  String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "StartButton_QuickLearnPopup_courseDescription", map );
    }

    /**
     取消按钮_速学弹窗_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onCourseDescriptionOfQuickLearnPopupOfCancelButton(Context context,
                                                                   String uid,
                                                                   String userName,
                                                                   int unitId,
                                                                   String unitName,
                                                                   int categoryId,
                                                                   String categoryName,
                                                                   int levelId,
                                                                   String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "CancelButton_QuickLearnPopup_courseDescription", map );
    }

    /**
     继续按钮_获得经验页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onGainExperiencePageOfContinueButton(Context context,
                                                     String uid,
                                                     String userName,
                                                     int unitId,
                                                     String unitName,
                                                     int categoryId,
                                                     String categoryName,
                                                     int levelId,
                                                     String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "ContinueButton_GainExperiencePage", map );
    }

    /**
     关闭按钮_速学做题页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onQuickLearnExercisePageOfCloseButton(Context context,
                                                      boolean isOn,
                                                      String uid,
                                                      String userName,
                                                      int unitId,
                                                      String unitName,
                                                      int categoryId,
                                                      String categoryName,
                                                      int levelId,
                                                      String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "CloseButton_QuickLearnExercisePage", map );
    }

    /* v1.2.0 */

    /**
     体验不限时学习按钮_小测验弹窗_首页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onLearnPageOfTestOutPopupOfUnlimitedTimeButton(Context context,
                                                               String uid,
                                                               String userName,
                                                               int unitId,
                                                               String unitName,
                                                               int categoryId,
                                                               String categoryName,
                                                               int levelId,
                                                               String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "UnlimitedTimeButton_TestOutPopup_LearnPage", map );
    }

    /**
     体验无限制学习按钮_小测验弹窗_首页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onLearnPageOfTestOutPopupOfUnlimitedStudyButton(Context context,
                                                                String uid,
                                                                String userName,
                                                                int unitId,
                                                                String unitName,
                                                                int categoryId,
                                                                String categoryName,
                                                                int levelId,
                                                                String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "UnlimitedStudyButton_TestOutPopup_LearnPage", map );
    }

    /**
     开始考试按钮_跳级考试页_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onCourseDescriptionOfSkipTestPageOfStartExamButton(Context context,
                                                                   String uid,
                                                                   String userName,
                                                                   int unitId,
                                                                   String unitName,
                                                                   int categoryId,
                                                                   String categoryName,
                                                                   int levelId,
                                                                   String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "StartExamButton_SkipTestPage_courseDescription", map );
    }

    /**
     体验无限跳级按钮_跳级考试页_课程介绍页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onCourseDescriptionOfSkipTestPageOfExperienceUnlimitedSkipButton(Context context,
                                                                                 String uid,
                                                                                 String userName,
                                                                                 int unitId,
                                                                                 String unitName,
                                                                                 int categoryId,
                                                                                 String categoryName,
                                                                                 int levelId,
                                                                                 String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "ExperienceUnlimitedSkipButton_SkipTestPage_courseDescription", map );
    }

    /**
     无限跳级按钮_闪电不足弹窗_跳级考试页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onSkipTestPageOfLightningLackPopupOfUnlimitedSkipButton(Context context,
                                                                        String uid,
                                                                        String userName,
                                                                        int unitId,
                                                                        String unitName,
                                                                        int categoryId,
                                                                        String categoryName,
                                                                        int levelId,
                                                                        String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "UnlimitedSkipButton_LightningLackPopup_SkipTestPage", map );
    }

    /**
     去练习按钮_闪电不足弹窗_跳级考试页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param unitId              模块Id
     @param unitName            模块名称
     @param categoryId          分类Id
     @param categoryName        分类名称
     @param levelId             阶段Id
     @param levelName           阶段名称
     */
    public void onSkipTestPageOfLightningLackPopupOfGotoPracticeButton(Context context,
                                                                       String uid,
                                                                       String userName,
                                                                       int unitId,
                                                                       String unitName,
                                                                       int categoryId,
                                                                       String categoryName,
                                                                       int levelId,
                                                                       String levelName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "moduleID", String.valueOf( unitId ) );
        map.put( "moduleName", unitName );
        map.put( "sortID", String.valueOf( categoryId ) );
        map.put( "sortName", categoryName );
        map.put( "phaseID", String.valueOf( levelId ) );
        map.put( "phaseName", levelName );
        mManage.onEvent( "GotoPracticeButton_LightningLackPopup_SkipTestPage", map );
    }

    /**
     付费按钮_会员页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param isGuest             用户身份
     @param payTime             购买期限
     */
    public void onVipPageOfPayButton(Context context,
                                     boolean isOn,
                                     String uid,
                                     String userName,
                                     boolean isGuest,
                                     boolean isVip,
                                     String payTime) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "payTime", payTime );
        map.put( "status", toStatus( isOn ) );
        mManage.onEvent( "PayButton_VIPPage", map );
    }

    /**
     解锁按钮_课程解锁弹窗_发现页
     @param context             上下文
     @param uid                 用户id
     @param userName            用户名称
     @param categoryId          每日聆听、热门视频的id
     @param categoryName        每日聆听、热门视频
     @param lessonId            课程Id
     @param lessonName          课程名称
     */
    public void onDiscoveryPageOfCourseUnlockPopupOfUnlockButton(Context context,
                                                                 String uid,
                                                                 String userName,
                                                                 long categoryId,
                                                                 String categoryName,
                                                                 long lessonId,
                                                                 String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "categoryID", String.valueOf( categoryId ) );
        map.put( "categoryName", categoryName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "UnlockButton_CourseUnlockPopup_DiscoveryPage", map );
    }

    /* v1.2.1 */

    /**
     视频语法课程_视频语法课程列表页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onVideoGrammarCourseListPageOfVideoGrammarCourse(Context context,
                                                                 String uid,
                                                                 String userName,
                                                                 boolean isGuest,
                                                                 boolean isVip,
                                                                 long lessonId,
                                                                 String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "VideoGrammarCourse_VideoGrammarCourseListPage", map );
    }

    /**
     解锁按钮_课程解锁弹窗_视频语法课程列表页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onVideoGrammarCourseListPageOfCourseUnlockPopupOfUnlockButton(Context context,
                                                                              String uid,
                                                                              String userName,
                                                                              boolean isGuest,
                                                                              boolean isVip,
                                                                              long lessonId,
                                                                              String lessonName,
                                                                              boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "result", "解锁" + toResultValue( result ) );
        mManage.onEvent( "UnlockButton_CourseUnlockPopup_VideoGrammarCourseListPage", map );
    }

    /**
     视频播放按钮_视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onVideoGrammarCourseDetailPageOfVideoPlayButton(Context context,
                                                                String uid,
                                                                String userName,
                                                                boolean isGuest,
                                                                boolean isVip,
                                                                long lessonId,
                                                                String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "VideoPlayButton_VideoGrammarCourseDetailPage", map );
    }

    /**
     视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     @param viewDuration    浏览时长
     */
    public void onVideoGrammarCourseDetailPage(Context context,
                                               String uid,
                                               String userName,
                                               boolean isGuest,
                                               boolean isVip,
                                               long lessonId,
                                               String lessonName,
                                               long viewDuration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( viewDuration ) );
        mManage.onEvent( "VideoGrammarCourseDetailPage", map );
    }

    /**
     更多按钮_视频语法课程_发现页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     */
    public void onFindingPageOfVideoGrammarCourseOfMoreButton(Context context,
                                                              String uid,
                                                              String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        baseBuriedPoint( map, true );
        mManage.onEvent( "MoreButton_VideoGrammarCourse_FindingPage", map );
    }

    /**
     收藏按钮_语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onGrammarCourseDetailPageOfCollection(Context context,
                                                      String uid,
                                                      String userName,
                                                      long lessonId,
                                                      String lessonName,
                                                      boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", "收藏" + toResultValue( result ) );
        mManage.onEvent( "Collection_GrammarCourseDetailpage", map );
    }

    /**
     转发按钮_语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onGrammarCourseDetailPageOfTranspondButton(Context context,
                                                           String uid,
                                                           String userName,
                                                           long lessonId,
                                                           String lessonName,
                                                           boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "status", "转发" + toResultValue( result ) );
        mManage.onEvent( "TranspondButton_GrammarCourseDetailpage", map );
    }

    /**
     打开APP按钮_课程详情落地页_每日聆听
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onCourseDetailLandingPageOfListenEverydayOfOpenAPPButton(Context context,
                                                                         String uid,
                                                                         String userName,
                                                                         long lessonId,
                                                                         String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "OpenAPPButton_CourseDetailLandingPage_ListenEveryday", map );
    }

    /**
     打开APP按钮_课程详情落地页_语法课程
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onVideoGrammarCourseListPageOfCourseUnlockPopupOfUnlockButton(Context context,
                                                                              String uid,
                                                                              String userName,
                                                                              long lessonId,
                                                                              String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "OpenAPPButton_CourseDetailLandingPage_GrammarCourse", map );
    }

    /**
     开通会员按钮_个人中心页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param result          结果
     */
    public void onMePageOfBuyVipButton(Context context,
                                        String uid,
                                        String userName,
                                        boolean isGuest,
                                        boolean isVip,
                                        boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "BuyVipButton_MePage", map );
    }

    /**
     登录_排行榜_游客个人中心页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onGuestMePageOfLeaderBoardOfLogin(Context context,
                                                  String uid,
                                                  String userName,
                                                  boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "Login_LeaderBoard_GuestMePage", map );
    }

    /**
     登录_好友_游客个人中心页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onGuestMePageOfFriendsOfLogin(Context context,
                                              String uid,
                                              String userName,
                                              boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "Login_Friends_GuestMePage", map );
    }

    /**
     查看按钮_成就_个人中心页
     @param context         上下文
     */
    public void onMePageOfAchievementOfViewButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ViewButton_Achievement_MePage", map );
    }

    /**
     查看按钮_排行榜_个人中心页
     @param context         上下文
     */
    public void onMePageOfLeaderBoardOfViewButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ViewButton_LeaderBoard_MePage", map );
    }

    /**
     查看按钮_好友_个人中心页
     @param context         上下文
     */
    public void onMePageOfFriendsOfViewButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "ViewButton_Friends_MePage", map );
    }

    /**
     邀请好友按钮_个人中心页
     @param context         上下文
     */
    public void onMePageOfInviteFriends(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "InviteFriends_MePage", map );
    }

    /**
     Facebook_邀请好友页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onInviteFriendsPageOfFacebook(Context context,
                                              String uid,
                                              String userName,
                                              boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "Facebook_InviteFriendsPage", map );
    }

    /**
     Messenger_邀请好友页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onInviteFriendsPageOfMessenger(Context context,
                                              String uid,
                                              String userName,
                                              boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "Messenger_InviteFriendsPage", map );
    }

    /**
     接受邀请按钮_个人中心页
     @param context         上下文
     @param result          结果
     */
    public void onMePageOfAcceptInvitation(Context context,
                                           boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "AcceptInvitation_MePage", map );
    }

    /**
     领券中心_个人中心页
     @param context     上下文
     */
    public void onMePageOfCouponCentre(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CouponCentre_MePage", map );
    }

    /**
     立即使用按钮_领券中心_个人中心页
     @param context         上下文
     @param result          结果
     */
    public void onMePageOfCouponCentreOfUseButton(Context context, boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "UseButton_CouponCentre_MePage", map );
    }

    /**
     立即使用按钮_领券弹窗_个人中心页
     @param context         上下文
     @param result          结果
     */
    public void onMePageOfCouponPopupOfUseButton(Context context, boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "UseButton_CouponPopup_MePage", map );
    }

    /**
     关闭按钮_领券弹窗_个人中心页
     @param context         上下文
     */
    public void onMePageOfCouponPopupOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_CouponPopup_MePage", map );
    }

    /**
     继续试用按钮_试用结束弹窗_个人中心页
     @param context         上下文
     @param result          结果
     */
    public void onMePageOfEndTryPopupOfContinueTryButton(Context context, boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "result", toResultValue( result ) );
        mManage.onEvent( "ContinueTryButton_EndTryPopup_MePage", map );
    }

    /**
     关闭按钮_试用结束弹窗_个人中心页
     @param context       上下文
     */
    public void onMePageOfEndTryPopupOfCloseButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CloseButton_EndTryPopup_MePage", map );
    }

    /* v1.2.2 */

    /**
     发表按钮_发表评论页_视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onVideoGrammarCourseDetailPageOfCommentPageOfCommentButton(Context context,
                                                                           String uid,
                                                                           String userName,
                                                                           boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "解锁" + toResultValue( result ) );
        mManage.onEvent( "CommentButton_CommentPage_VideoGrammarCourseDetailPage", map );
    }

    /**
     对话页_视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     @param viewDuration    浏览时长
     */
    public void onVideoGrammarCourseDetailPageOfDialoguePage(Context context,
                                                             String uid,
                                                             String userName,
                                                             boolean isGuest,
                                                             boolean isVip,
                                                             long lessonId,
                                                             String lessonName,
                                                             long viewDuration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( viewDuration ) );
        mManage.onEvent( "DialoguePage_VideoGrammarCourseDetailPage", map );
    }

    /**
     语法页_视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     @param viewDuration    浏览时长
     */
    public void onVideoGrammarCourseDetailPageOfGrammarPage(Context context,
                                                            String uid,
                                                            String userName,
                                                            boolean isGuest,
                                                            boolean isVip,
                                                            long lessonId,
                                                            String lessonName,
                                                            long viewDuration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( viewDuration ) );
        mManage.onEvent( "GrammarPage_VideoGrammarCourseDetailPage", map );
    }

    /**
     讨论区_视频语法课程详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param isGuest         是否为游客
     @param isVip           是否为会员
     @param lessonId        课程id
     @param lessonName      课程名称
     @param viewDuration    浏览时长
     */
    public void onVideoGrammarCourseDetailPageOfForumPage(Context context,
                                                          String uid,
                                                          String userName,
                                                          boolean isGuest,
                                                          boolean isVip,
                                                          long lessonId,
                                                          String lessonName,
                                                          long viewDuration) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, false );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "userIdentity", getUserIdentity( isGuest, isVip ) );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        map.put( "viewingDuration", String.valueOf( viewDuration ) );
        mManage.onEvent( "ForumPage_VideoGrammarCourseDetailPage", map );
    }

    /**
     发表按钮_发表评论页_热门视频详情页
     @param context         上下文
     @param uid             用户id
     @param userName        用户名
     @param result          结果
     */
    public void onPopularVideoDetailpageOfCommentPageOfCommentButton(Context context,
                                                                     String uid,
                                                                     String userName,
                                                                     boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "解锁" + toResultValue( result ) );
        mManage.onEvent( "CommentButton_CommentPage_PopularVideoDetailpage", map );
    }

    /**
     选择分类_每日聆听列表页
     @param context         上下文
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onListenEverydayListPageOfSelectionSort(Context context,
                                                        long lessonId,
                                                        String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "SelectionSort_ListenEverydayListPage", map );
    }

    /**
     选择分类_热门视频列表页
     @param context         上下文
     @param lessonId        课程id
     @param lessonName      课程名称
     */
    public void onPopularVideoListPageOfSelectionSort(Context context,
                                                      long lessonId,
                                                      String lessonName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "lessonID", String.valueOf( lessonId ) );
        map.put( "lessonName", lessonName );
        mManage.onEvent( "SelectionSort_PopularVideoListPage", map );
    }

    /**
     积分按钮_个人中心页
     @param context         上下文
     */
    public void onMePageOfPointButton(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "PointButton_MePage", map );
    }

//    /**
//     去下载按钮_落地页_邀请好友
//     @param context         上下文
//     @param uid             用户id
//     @param userName        用户名
//     @param lessonId        课程id
//     @param lessonName      课程名称
//     */
//    public void onInviteFriendOfLandingPageOfDownloadButton(Context context,
//                                                            String uid,
//                                                            String userName,
//                                                            long lessonId,
//                                                            String lessonName) {
//        Map<String, Object> map = new HashMap<>();
//        baseBuriedPoint( map, true );
//        map.put( "userID", uid );
//        map.put( "userName", userName );
//        map.put( "lessonID", String.valueOf( lessonId ) );
//        map.put( "lessonName", lessonName );
//        mManage.onEvent( "DownloadButton_LandingPage_InviteFriend", map );
//    }

    /**
     FB反馈链接_反馈页_个人中心
     @param context         上下文
     */
    public void onMePageOfFeedbackPageOfFBurl(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "FBurl_FeedbackPage_MePage", map );
    }

    /* v1.2.3 */

    /**
     * 用户通过小测验后再次进入小测验点击刷新战绩按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        挑战结果。-2：持平，-1：失败，0：成功，>0：中断位置
     */
    public void onTestOfRefreshRecordPageOfRefreshRecordButton(Context context,
                                                               String uid,
                                                               String userName,
                                                               @IntRange( from = -2) int result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toRefreshRecordResult( result ) );
        mManage.onEvent( "RefreshRecordButton_RefreshRecordPage_Test", map );
    }

    /**
     * 非会员在刷新战绩页点击刷新战绩弹窗中的去开通按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        开通结果
     */
    public void onRefreshRecordPageOfToBuyButton(Context context,
                                                 String uid,
                                                 String userName,
                                                 boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "开通" + toResultValue( result ) );
        mManage.onEvent( "ToBuyButton_RefreshRecordPage", map );
    }

    /**
     * 会员在刷新战绩页点击消耗闪电弹窗中的OK按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onRefreshRecordPageOfBackButton(Context context,
                                                String uid,
                                                String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "BackButton_RefreshRecordPage", map );
    }

    /**
     * 会员在能量不足页点击去兑换按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        兑换结果
     */
    public void onEnergyShortagePageOfToExchangeButton(Context context,
                                                       String uid,
                                                       String userName,
                                                       boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "兑换" + toResultValue( result ) );
        mManage.onEvent( "ToExchangeButton_EnergyShortagePage", map );
    }

    /**
     * 会员在能量不足页点击去练习按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onEnergyShortagePageOfToPractice(Context context,
                                                 String uid,
                                                 String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "ToPractice_EnergyShortagePage", map );
    }

    /**
     * 游客在首页点击小测验后点击弹窗中的登录注册按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        兑换结果
     */
    public void onLearnSignUpPopupPageOfSignUpLoginButton(Context context,
                                                          String uid,
                                                          String userName,
                                                          boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "登录" + toResultValue( result ) );
        mManage.onEvent( "SignupLoginButton_LearnSignupPopupPage", map );
    }

    /**
     * 用户在首次通过小测验获得经验值后，在考试通过页面点击刷新战绩按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        挑战结果。-2：持平，-1：失败，0：成功，>0：中断位置
     */
    public void onPassTestPageOfRefreshRecordButton(Context context,
                                                    String uid,
                                                    String userName,
                                                    @IntRange( from = -2) int result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toRefreshRecordResult( result ) );
        mManage.onEvent( "RefreshRecordButton_PassTestPage", map );
    }

    /**
     * 用户在首次通过小测验获得经验值后，在考试通过页面点击先这样按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onPassTestPageOfLaterButton(Context context,
                                            String uid,
                                            String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "LaterButton_PassTestPage", map );
    }

    /**
     * 用户首次未通过小测验，在考试未通过页面点击再试一次按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        兑换结果
     */
    public void onFailTestPageOfTryAgainButton(Context context,
                                               String uid,
                                               String userName,
                                               boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "考试" + ( result ? "通过" : "未通过" ) );
        mManage.onEvent( "TryagainButton_FailTestPage", map );
    }

    /**
     * 用户首次未通过小测验，在考试未通过页面点击算了按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onFailTestPageOfFailTestPage(Context context,
                                             String uid,
                                             String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "FailTestPage_FailTestPage", map );
    }

    /**
     * 用户通过小测验后再次进入小测验刷新战绩，挑战结束后在挑战结果页点击再试一次按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        挑战结果。-2：持平，-1：失败，0：成功，>0：中断位置
     */
    public void onChallengeResultPageOfTryAgainButton(Context context,
                                                      String uid,
                                                      String userName,
                                                      @IntRange( from = -2) int result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", toRefreshRecordResult( result ) );
        mManage.onEvent( "TryagainButton_ChallengeResultPage", map );
    }

    /**
     * 用户在积分详情页点击积分明细入口时
     * @param context       上下文
     */
    public void onCreditsDetailPageOfCreditsDetail(Context context) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        mManage.onEvent( "CreditsDetail_CreditsDetailPage", map );
    }

    /**
     * 用户在积分详情页点击去邀请按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        兑换结果
     */
    public void onCreditsDetailPageOfToInviteButton(Context context,
                                                    String uid,
                                                    String userName,
                                                    boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "邀请" + toResultValue( result ) );
        mManage.onEvent( "ToInviteButton_CreditsDetailPage", map );
    }

    /**
     * 用户在积分详情页点击去兑换按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onCreditsDetailPageOfExchangeButton(Context context,
                                                    String uid,
                                                    String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "ExchangeButton_CreditsDetailPage", map );
    }

    /**
     * 用户在积分详情页点击兑换按钮后，弹出兑换弹窗，在兑换弹窗中点击立即兑换按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     * @param result        兑换结果
     */
    public void onCreditsDetailPageOfExchangePopupOfExchangeNow(Context context,
                                                                String uid,
                                                                String userName,
                                                                boolean result) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        map.put( "result", "兑换" + toResultValue( result ) );
        mManage.onEvent( "ExchangeNow_ExchangePopup_CreditsDetailPage", map );
    }

    /**
     * 用户在积分详情页点击返回按钮时
     * @param context       上下文
     * @param uid           用户id
     * @param userName      用户名称
     */
    public void onCreditsDetailPageOfBackButton(Context context,
                                                String uid,
                                                String userName) {
        Map<String, Object> map = new HashMap<>();
        baseBuriedPoint( map, true );
        map.put( "userID", uid );
        map.put( "userName", userName );
        mManage.onEvent( "BackButton_CreditsDetailPage", map );
    }
}
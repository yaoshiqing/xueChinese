package com.gjjy.basiclib.buried_point;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.CLASS )
public @interface PageName {
    String WHETHER_LOGIN = "WhetherLoginPage";                  //是否登录页
    String LOGIN = "LoginPage";                                 //登录页
    String COURSE_REMIND = "CourseRemindPage";                  //课程提醒页
    String SIGN_IN = "SignIn";                                  //登录页
    String GUEST_SIGN_IN = "GuestSignIn";                       //游客登录页
    String COURSE_LOGIN = "CourseLogin";                        //课程登录页
    String RE_LOGIN = "ReLogin";                                //重新登录页
    String QUESTION_DETAILS = "QuestionDetails";                //题目详情页
    String GUEST_QUESTION_DETAILS = "GuestQuestionDetails";     //游客题目详情页
    String SETTING = "Setting";                                 //设置页
    String GUEST_SETTING = "GuestSetting";                      //游客设置页
    String BUY_VIP = "BuyVip";                                  //vip购买页面
    String COMMENTS = "CommentPage";                            //评论页面
    String COURSE_DESC_LOGIN = "CourseDescriptionLogin";        //考试按钮登录页面
}
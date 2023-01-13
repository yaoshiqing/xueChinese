package com.gjjy.basiclib.utils;

import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.entity.AnswerBaseEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Constant {

    @Retention(RetentionPolicy.SOURCE)
    @interface AnswerType {
        int NONE = -1;          //空页面
        int NORMAL = 0;         //正常
        int TEST = 1;           //考试
        int SKIP_TEST = 2;      //跳级考试
        int FAST_REVIEW = 3;    //快速复习
        int ERROR_MAP = 4;      //错题集
        int INTRODUCE = 5;      //介绍页
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface SoundType {
//        int INIT_SOUND = 200;                       //初始化音频
//        int SOUND_PLAY_COMPLETION = 201;            //播放完成
        int SOUND_CORRECT = 202;                    //正确答案弹窗弹出时
        int SOUND_ERROR = 203;                      //错误答案弹窗弹出时
        int SOUND_NODE_COMPLETE = 204;              //小节完成点亮星星时
        int SOUND_GET_LIGHTNING = 205;      //获得闪电页
        int SOUND_ANSWER_END_COMPLETE = 207;        //答题结束页
        int SOUND_ENCOURAGE = 208;                  //连错鼓励页
        int SOUND_ANSWER_NODE_ENCOURAGE = 209;        //小测验鼓励页
    }

    /**
     * 类型：Bundle
     */
    String MAIN_PUSH_BUNDLE = "MAIN_PUSH_BUNDLE";

    /**
     * 页面类型
     * 类型：{@link PageName}
     */
    String PAGE_NAME = "PAGE_NAME";

    /**
     * 是否全屏
     * 类型：boolean   true：全屏
     */
    String IS_FULL_SCREEN = "IS_FULL_SCREEN";

    /**
     * 是否显示按钮
     * 类型：boolean   true：显示
     */
    String IS_SHOW_BUTTON = "IS_SHOW_BUTTON";

    /**
     * 课程类型
     * 类型：{@link AnswerType}
     */
    String ANSWER_TYPE = "ANSWER_TYPE";

    /**
     * 答题结果
     * 类型：boolean   true：完成答题
     */
    String ANSWER_RESULT = "ANSWER_RESULT";

    /**
     * AnswerBaseEntity
     * 类型：{@link AnswerBaseEntity}
     */
    String ANSWER_BASE_ENTITY = "ANSWER_BASE_ENTITY";

    /**
     * ID
     * 类型：String
     */
    String ID_FOR_STRING = "ID_FOR_STRING";

    /**
     * ID
     * 类型：int
     */
    String ID_FOR_INT = "ID";

    /**
     * VideoId
     * 类型：String
     */
    String ID_FOR_VIDEO = "VideoId";

    /**
     * 课程名称
     * 类型：String
     */
    String NAME = "NAME";

    /**
     * 类型
     * 类型：String
     */
    String TYPE = "TYPE";

    /**
     * 链接
     * 类型：String
     */
    String URL = "URL";

    /**
     * 小节ID
     * 类型：{@link SectionIds}
     */
    String SECTION_IDS = "SECTION_IDS";

    /**
     * 当前小节进度
     * 类型：int
     */
    String SECTION_NUM = "SECTION_NUM";

    /**
     * 页面数据
     * 类型：com.gjjy.frontlib.entity.PagerEntity
     */
    String PAGER_DATA = "PAGER_DATA";

    /**
     * 下一个下标
     * 类型：int
     */
    String NEXT_POSITION = "NEXT_POSITION";

    /**
     * 评级数量
     * 类型：int
     */
    String RATING_NUM = "RATING_NUM";

    /**
     * 最大评级数量
     * 类型：int
     */
    String MAX_RATING = "MAX_RATING";

    /**
     * toolbar显示状态
     * 类型：int
     *      {@link android.view.View#VISIBLE}
     *      {@link android.view.View#INVISIBLE}
     *      {@link android.view.View#GONE}
     */
    String TOOLBAR_VISIBILITY = "TOOLBAR_VISIBILITY";

    /**
     * 延迟毫秒
     * 类型：long
     */
    String DELAY_MILLIS = "DELAY_MILLIS";

    /**
     * 协议类型
     * 类型：int   0：terms，1：policy
     */
    String AGREEMENT_TYPE = "AGREEMENT_TYPE";

    /**
     * 登录页面没有返回按钮
     * 类型：boolean   是否没有返回按钮
     */
    String LOGIN_NOT_BACK_BTN = "LOGIN_NOT_BACK_BTN";

    /**
     * 登录失败返回时退出app
     * 类型：int   -1：登录失败，0：未绑定，1：已登录
     */
    String LOGIN_CALL_RESULT = "LOGIN_CALL_RESULT";

    /**
     * 是否为初始化页面
     * 初始化页面返回时会退出app
     * 类型：boolean   true：初始化页面
     */
    String GUIDE_INIT_PAGE = "GUIDE_INIT_PAGE";

    /**
     * 是否展示引导页
     * 类型：boolean   true：展示
     */
    String GUIDE_SHOW_GUIDE_HOME_PAGE = "GUIDE_SHOW_GUIDE_HOME_PAGE";

    /**
     * 是否展示登录页
     * 类型：boolean   true：展示
     */
    String GUIDE_SHOW_LOGIN_PAGE = "GUIDE_SHOW_LOGIN_PAGE";

    /**
     * 答题经验值
     * 类型：int
     */
    String ANSWER_EXP = "ANSWER_EXP";

    /**
     * 邮箱
     * 类型：String
     */
    String EMAIL = "EMAIL";

    /**
     * 密码
     * 类型：String
     */
    String PASSWORD = "PASSWORD";

    /**
     跳级考试消耗闪电
     类型：int {@link AnswerType}，空页面返回：-1
     */
    String SKIP_TEST_LIGHTNING_TYPE = "SKIP_TEST_LIGHTNING_TYPE";

    /**
     * 今天领取经验值总数
     * 类型：int
     */
    String ANSWER_TODAY_EXP = "ANSWER_TODAY_EXP";

    /**
     置顶评论类型
     类型：int 1：点赞，2：回复
     */
    String TOP_COMMENT_INTERACT_TYPE = "TOP_COMMENT_INTERACT_TYPE";

    /**
     置顶评论id
     */
    String TOP_COMMENT_TALK_ID = "TOP_COMMENT_TALK_ID";
}
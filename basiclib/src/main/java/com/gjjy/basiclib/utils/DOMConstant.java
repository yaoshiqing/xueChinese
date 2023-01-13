package com.gjjy.basiclib.utils;

public @interface DOMConstant {
    /**
     * 退出登录
     * 类型：int
     */
    int LOG_OFF = 99999;

    /**
     刷新首页
     类型：int    0：刷新（滑到底部，弹加载框），1：只刷新
     */
    int NOTIFY_FRONT_LIST = 10000;

    /**
     * 刷新探索
     * 类型：int       不传：回到顶部，1：下拉数据，2：回顶+下拉数据
     */
    int NOTIFY_DISCOVERY_LIST = 10001;

//    int NOTIFY_targeted_learning_LIST = 10002;

    int NOTIFY_USER_CENTER_LIST = 10004;

    /**
     * 题目打完后更新当前进度section_num
     * 类型：int
     */
    int NOTIFY_HOME_SECTION_NUM = 10005;

//    /**
//     * 模块状态更新unit_status
//     * 类型：int。 0未解锁、1已解锁、2已完成
//     */
//    int NOTIFY_HOME_UNIT_STATUS = 10004;

    /**
     * 网络状态
     * 类型：boolean
     */
    int NETWORK_AVAILABLE_STATUS = 10006;

    /**
     * 刷新答题数据
     */
    int REFRESH_ANSWER = 10007;

    /**
     * 展示引导页
     */
    int INIT_INIT_GUIDE = 10008;

    /**
     领取经验数据

     Data
     {@link java.util.Map}

     Key & Val
     答题类型
     {@link Constant#ANSWER_TYPE}           {@link Constant.AnswerType}
     领取经验值
     {@link Constant#ANSWER_EXP}            int
     今天领取经验值总数
     {@link Constant#ANSWER_TODAY_EXP}      int
     */
    int ANSWER_EXP_DATA = 10009;

    /**
     领取经验页面点击了下一页
     类型：boolean     true:经验页触发的，false：其他渠道触发的
     */
    int ANSWER_EXP_NEXT = 10010;

    /**
     每个答题节点保存进度结果
     类型：boolean
     */
    int ANSWER_NEXT_SAVE_PROGRESS_SUCCESS = 10011;

    /**
     滑动Y轴{@link android.widget.ScrollView} 或 {@link androidx.core.widget.NestedScrollView}
     类型：int     滑动值
     */
    int SCROLL_VIEW_Y = 10012;

    /**
     专项学习详情页 AppbarLayout expanded
     */
    int targeted_learning_DETAIL_ABL_EXPANDED = 10013;
}

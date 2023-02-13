package com.gjjy.basiclib;

import com.ybear.ybutils.utils.SysUtil;

public class Config {
    //版本号
    public static String mVersion;
    //更新渠道
    public static final String mUpdatedChannel = "android";
    //是否为后台测试服API
    public static final boolean isDebugOfURL = false;
    //邮箱
    public static final String mEmail = "xuechinese.official@gmail.com";
    //好友分享的Quote
    public static String mInviteFriendsQuote = "멤버십 드릴테니 어서 써봅시다";
    //是否解锁全部模块限制（无法保存进度）
    public static boolean isModelFullUnlock = true;
    //测试会员状态（后期需要去掉。1试用会员、2正式会员、0不是会员）
    public static int mVipStatus = 0;
    //后台api地址
    public static final String URL_API = isDebugOfURL ?
            /*测试*/ "http://8.213.133.241" :
            /*正式*/ "https://www.chineseadventure.net";

    public static final String URL_H5 = String.format( "%s/%s/", URL_API, mUpdatedChannel );

    //https://www.chineseadventure.net/api/study-core/content/index?type=message&id=121
    public static final String URL_MSG_DETAILS = URL_API + "/api/study-core/content/index?type=message&id=";

    //官网页 - FB渠道
    public static final String mFacebookUrl = "https://www.facebook.com/profile.php?id=100082885885324";
    //公告页
    public static final String mAnnouncementUrl = URL_H5 + "notice.html?" + getHtmlLangArg();
    //条款页https://play.google.com/apps/internaltest/4698621584117754273https://play.google.com/apps/internaltest/4698621584117754273
    public static final String mTermsUrl = URL_H5 + "terms.html?" + getHtmlLangArg();
    //隐私政策页
    public static final String mPolicyUrl = URL_H5 + "policyAndroid.html?" + getHtmlLangArg();
    //积分规则页
    public static final String mIntegralRulesUrl = URL_H5 + "integralRules.html?" + getHtmlLangArg();
    //邀请页
    public static final String mInvitationCodeUrl = URL_H5 + "invitation_code.html?code=%s&" + getHtmlLangArg();
    /* 介绍页 */
    private static final String mBaseIntroduceListUrl = URL_H5 + "introduce/list%s.html?" + getHtmlLangArg();
    public static final String[] mIntroduceListUrls = {
            String.format( mBaseIntroduceListUrl, 1 ),
            String.format( mBaseIntroduceListUrl, 2 ),
            String.format( mBaseIntroduceListUrl, 3 )
    };
    //答题详情页
    public static final String mAnswerDescApi = "greeting.html?" + getHtmlLangArg();
    /* 分享页 */
    private static final String mShareEndHtml = getHtmlLang() + ".html?" + getHtmlLangArg();
    //每日聆听
    public static final String mShareListenDailyUrl = URL_H5 + "listen_daily" + mShareEndHtml;
    //热门视频
    public static final String mSharePopularVideosUrl = URL_H5 + "popular_videos" + mShareEndHtml;
    //专项学习
    public static final String mShareTargetedLearningUrl = URL_H5 + "targeted_learning" + mShareEndHtml;

    //反馈链接
    public static final String mFeedbackUrl = URL_H5 + "feedback.html?user_id=%s&email=%s&" + getHtmlLangArg();

    public static String getLang() { return SysUtil.getLanguage(); }

    /**
     根据手机语言返回对应的h5语言代码
     @return    返回h5语言代码
     */
    private static String getHtmlLangArg() {
        return "language=" + ( "ko".equals( Config.getLang() ) ? "1" : "0" );
    }

    private static String getHtmlLang() {
        String lang = Config.getLang();
        return "ko".equals( lang ) ? "_" + lang : "";
    }
}
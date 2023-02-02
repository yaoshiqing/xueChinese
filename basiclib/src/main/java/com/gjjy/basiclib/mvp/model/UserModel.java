package com.gjjy.basiclib.mvp.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.api.entity.AwardEntity;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.api.entity.FriendListEntity;
import com.gjjy.basiclib.api.entity.RankingListEntity;
import com.gjjy.basiclib.api.entity.UploadImgEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.LoginType;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.gjjy.basiclib.dao.sql.UserSQL;
import com.gjjy.basiclib.entity.UserRegisterReq;
import com.gjjy.basiclib.entity.UserRegisterResp;
import com.gjjy.basiclib.push.Push;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.osslib.OSS;
import com.gjjy.osslib.OnOSSCompletedListener;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.Utils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class UserModel extends MvpModel implements DOM.OnResultListener {
    private static volatile UserDetailEntity mData;

    @Model
    private UserSQL mUserSQL;
    @Model
    private ReqUserModel mReqUser;
    @Model
    private RemindModel mRemindModel;
    private int mHeart = 0;
    private int mLightning = 0;
    private String registrationToken = "";

    public UserModel() {
    }

    @Override
    public void onCreateModel() {
        super.onCreateModel();
        mData = mUserSQL.queryUserDetail();
        mReqUser.setUid(mData.getUid(), mData.getToken());
        mRemindModel.refreshUid(mData.getUid(), mData.getToken());
    }

    public boolean startLoginCheckActivity(Activity activity, boolean isNeedLogin) {
        if (activity == null) {
            return false;
        }
        LogUtil.e("UserModel -> startLoginCheckActivity -> " +
                "isLoginResult:" + isLoginResult() + " | " +
                "isDeleteUser:" + isDeleteUser() + " | " +
                "isAddAnswer:" + isAddAnswer()
        );
        //之前是否处理过引导页面
        if ((!isLoginResult() || isDeleteUser()) && !isAddAnswer()) {
            //打开引导页面
            StartUtil.startGuideActivity(activity);
            return true;
        } else if (isNeedLogin && (!isLoginResult() || isDeleteUser())) {
            //启动登录页面
            StartUtil.startLoginActivity(activity, PageName.COURSE_LOGIN);
            return true;
        }
        return false;
    }

    public boolean startLoginCheckActivity(Activity activity) {
        return startLoginCheckActivity(activity, true);
    }

    public RemindModel getRemindModel() {
        return mRemindModel;
    }

    public long getUserId() {
        return mData.getUserId();
    }

    public String getUid() {
        return mReqUser.getUid();
    }

    public String getEmail() {
        return mData.getEmail();
    }

    public String getToken() {
        return mReqUser.getToken();
    }

    public String getUserName(Resources res) {
        if (isLoginResult()) {
            return mData.getNickname();
        }
        if (res == null) {
            return "Guest";
        } else {
            return res.getString(R.string.stringSignature);
        }
    }

    public String getAvatarUrl() {
        return mData.getAvatarUrl();
    }

    public int getVipStatus() {
        return mData.getVipStatus();
    }

    public void setVipStatus(int vipStatus) {
        mData.setVipStatus(vipStatus);
    }

    public boolean isVip() {
        return mData.getIsVip();
    }

    public void setVip(boolean isVip) {
        mData.setIsVip(isVip);
        mUserSQL.updatedUserDetail(mData);
    }

    public void setIntegral(int count) {
        mData.setIntegral(count);
        mUserSQL.updatedUserDetail(mData);
    }

    public int getIntegral() {
        return mData.getIntegral();
    }

    public String getInvitationCode() {
        return mData.getInvitationCode();
    }

    @LoginType
    public String getLoginType() {
        return mData.getLoginType();
    }

    public boolean isEmailLogin() {
        return LoginType.EMAIL.equals(getLoginType());
    }

    public boolean isExistUid() {
        return !TextUtils.isEmpty(mReqUser.getUid());
    }

    public boolean isLoginResult() {
        return isExistUid() && !TextUtils.isEmpty(mReqUser.getToken());
    }

    public boolean isDeleteUser() {
        return mData.getIsDelete();
    }

    public boolean isAddAnswer() {
        return mData.getIsAddAnswer();
    }

    public int getHeart() {
        return mHeart;
    }

    public int getLightning() {
        return mLightning;
    }

    public int getExpCount() {
        return mData.getExpCount();
    }

    public String getFriendInvitationCode() {
        return mData.getFriendInvitationCode();
    }

    /**
     * 退出登录
     */
    public void doLogOut(Activity activity) {
        if (activity == null) {
            return;
        }
        StartUtil.startExitAppLoginActivity(activity);
    }

    public void doLogOutOfOnActivityResult(Context context, int requestCode, int resultCode) {
        if (requestCode != StartUtil.REQUEST_CODE_EXIT_LOGIN_RESULT) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        mRemindModel.initData();
        DOM.getInstance().setResult(StartUtil.REQUEST_CODE_GO_TO_HOME);
        //埋点
        BuriedPointEvent.get().onSetUpPageOfLogOffButton(
                context,
                getUid(),
                getUserName(context.getResources()),
                true
        );
    }

    /**
     * 新增用户
     *
     * @param call 处理结果
     */
    public void addUser(Consumer<Boolean> call) {
        String beforeUid = mData.getUid();
        LogUtil.e("addUser -> beforeUid:" + beforeUid);
        //本地存在uid时跳过新增
        if (!TextUtils.isEmpty(beforeUid)) {
            if (call != null) {
                call.accept(true);
            }
            return;
        }
        mReqUser.reqAddUser(data -> {
            boolean result = data.isSuccess();
            //保存uid到数据库
            if (result) {
                String uid = data.getUid();
                mData.setUid(uid);
                mReqUser.setUid(uid, data.getToken());
                mUserSQL.insertUserDetail(mData);
                //绑定推送
                bindPush(r -> LogUtil.d("addUser -> bindPush -> result:" + r));
                //更新时区
                updatedTimeZone(null);
            }
            if (call != null) {
                call.accept(result);
            }
        });
    }

    /**
     * 获取用户奖励信息
     *
     * @param call 处理结果
     */
    public void getAward(Consumer<AwardEntity> call) {
        mReqUser.reqAward(data -> {
            if (data == null) {
                return;
            }
            /* 旧的 */
            data.setOldLightning(mLightning);
            data.setOldHeart(mHeart);
            /* 新的 */
            mLightning = data.getLightning();
            mHeart = data.getHeart();
            //回调
            if (call != null) {
                call.accept(data);
            }
        });
    }

    /**
     * 新增用户程度和目的
     *
     * @param levelType  语言程度：1不了解、2基本了解、3熟悉
     * @param motiveType 学习目的：1个人兴趣、2旅游、3家人好友、4学校、5工作、6技能扩展提升、7其他
     * @param call       处理结果
     */
    public void addAnswer(int levelType, int motiveType, Consumer<Boolean> call) {
        mReqUser.reqAddAnswer(levelType, motiveType, result -> {
            //保存新增结果。result在第二次插入时会失败（之前插入过了）
            mData.setIsAddAnswer(true);
            mUserSQL.updatedUserDetail(mData);
            if (call != null) {
                call.accept(result);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param call 获取结果
     */
    public void getUserDetail(Consumer<UserDetailEntity> call) {
        if (mData.getIsDelete()) {
//            mReqUser.setUid( null );
//            mData.setUid( null );
            if (call != null) {
                call.accept(mData);
            }
            return;
        }
//        if( TextUtils.isEmpty( mData.getUid() ) ) return;
//        //存在token时返回本地信息
//        if( !TextUtils.isEmpty( mData.getToken() ) ) {
//            mData = mUserSQL.queryUserDetail();
//            if( call != null ) call.accept( mData );
//            return;
//        }
        //生成邀请码
        if (TextUtils.isEmpty(mData.getInvitationCode())) {
            mReqUser.reqCreateInviteCode(data -> {
                mData.setInvitationCode(data.getInviteCode());
                reqUserDetail(call);
            });
            return;
        }
        //获取用户信息
        reqUserDetail(call);
    }

    public UserDetailEntity getUserDetail() {
        return mData;
    }

    private void reqUserDetail(Consumer<UserDetailEntity> call) {
        mReqUser.reqDetail(data -> {
            String uid = data.getUid();
            String token = data.getToken();
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                mReqUser.setUid(uid, token);
                mData.setUid(uid);

//                if( !TextUtils.isEmpty( data.getBindType() ) && !TextUtils.isEmpty( data.getBindUid() ) ) {
//                    mData.setToken( "--" );
//                }
            }
            if (token != null) mData.setToken(token);

            mData.setUserId(data.getUserId());
            mData.setNickname(data.getNickname());
            if (!TextUtils.isEmpty(data.getAvatarUrl())) {
                mData.setAvatarUrl(data.getAvatarUrl());
            }
            mData.setGender(data.getGender());
            mData.setPhone(data.getPhone());
            mData.setEmail(data.getEmail());
            mData.setLightning(mLightning = data.getLightning());
            mData.setHeart(mHeart = data.getHeart());
            mData.setIsVip(data.isVip());
            mData.setVipStatus(data.getVipStatus());
            if (Config.mVipStatus != 0) {
                mData.setIsVip(true);
                mData.setVipStatus(Config.mVipStatus);
            }
            mData.setCreateTime(data.getCreateTime());
            mData.setLastTime(data.getLastTime());
            mData.setVipTime(data.getVipTime());
            mData.setVipEndTime(data.getVipEndTime());
            mData.setInvitationCode(data.getInviteCode());
            mData.setFriendInvitationCode(data.getFriendInviteCode());
            mData.setExpCount(data.getExperience());
            mData.setIsLock(data.isLock());
            mData.setBindType(data.getBindType());
            mData.setBindUid(data.getBindUid());
            mData.setIsBindAccount(!TextUtils.isEmpty(data.getBindUid()));
            mData.setVisitCount(data.getVisitCount());
            mData.setWordCount(data.getWordCount());
            //保存到数据库
            mUserSQL.insertUserDetail(mData);
            if (call != null) {
                call.accept(mData);
            }
        });
    }

    /**
     * 编辑用户信息
     *
     * @param loginId  登录账户的id
     * @param nickname 昵称
     * @param saveUrl  头像地址
     * @param showUrl  头像地址
     * @param gender   性别：0未知，1男、2女
     * @param phone    手机号码
     * @param email    邮箱
     * @param call     处理结果
     */
    public void editUserInfo(@Nullable String loginId,
                             @Nullable String nickname,
                             @Nullable String saveUrl,
                             @Nullable String showUrl,
                             @IntRange(from = -1, to = 2) int gender,
                             @Nullable String phone,
                             @Nullable String email,
                             Consumer<Boolean> call) {
        String imgUrl = saveUrl != null && saveUrl.startsWith("http") ? null : saveUrl;
        mReqUser.reqEditUserInfo(nickname, imgUrl, gender, phone, email, result -> {
            if (result) {
                if (!TextUtils.isEmpty(loginId)) {
                    mData.setLoginId(loginId);
                }
                if (!TextUtils.isEmpty(nickname)) {
                    mData.setNickname(nickname);
                }
                if (!TextUtils.isEmpty(showUrl)) {
                    mData.setAvatarUrl(showUrl);
                }
                if (gender != -1) {
                    mData.setGender(gender);
                }
                if (!TextUtils.isEmpty(phone)) {
                    mData.setPhone(phone);
                }
                if (!TextUtils.isEmpty(email)) {
                    mData.setEmail(email);
                }
                mUserSQL.updatedUserDetail(mData);
            }
            if (call != null) {
                call.accept(result);
            }
        });
    }

    public void editUserInfo(@Nullable String loginId,
                             @Nullable String nickname,
                             @Nullable String avatarUrl,
                             @IntRange(from = -1, to = 2) int gender,
                             @Nullable String phone,
                             @Nullable String email,
                             Consumer<Boolean> call) {
        editUserInfo(loginId, nickname, avatarUrl, avatarUrl, gender, phone, email, call);

    }

    public void updatedTimeZone(Consumer<Boolean> call) {
        mReqUser.reqUpdatedTimeZone(call);
    }

    /**
     * 编辑头像
     *
     * @param context 上下文
     * @param bmp     本地图片
     * @param call    处理结果
     */
    public void editUserAvatarUrl(Context context, Bitmap bmp, Consumer<UploadImgEntity> call) {
        File f = Utils.uriToFile(context, Utils.bmpToUri(context, bmp));
        if (!f.exists()) {
            if (call != null) {
                call.accept(null);
            }
            return;
        }
        OSS.get().addOnOSSCompletedListener(new OnOSSCompletedListener() {
            @Override
            public void onSuccess(String url, String objectName, int index, int count) {
                UploadImgEntity data = new UploadImgEntity();
                data.setSave(objectName);
                data.setShow(url);
                data.setLocalFilePath(f.getAbsolutePath());
                //保存上传的头像
                editUserInfo(null, null, objectName, url, -1, null, null, result -> {
                    if (call != null) {
                        call.accept(data);
                    }
                });
            }

            @Override
            public void onFailure(String url, String objectName, int index, int count) {
                UploadImgEntity data = new UploadImgEntity();
                data.setSave(objectName);
                data.setShow(url);
                //保存上传的头像
                editUserInfo(null, objectName, url, -1, null, null, result -> {
                    if (call != null) {
                        call.accept(data);
                    }
                });
            }
        }).uploadAvatar(f);
    }

    private void bind(String uid, String registrationToken, String token, String loginType, Consumer<Integer> call) {
        Consumer<String> bindCall = msg -> {
            updateIdentity(uid, registrationToken, token, loginType);
            //绑定推送
            bindPush(r2 -> {
                if (call != null) {
                    call.accept(r2 ? 1 : 0);
                }
                LogUtil.d("login" + msg + " -> bindPush -> result:" + r2);
            });
        };
        if (TextUtils.isEmpty(getUid())) {
            bindCall.accept("(not uid)");
        } else {
            //解绑游客
            unBindPush(r1 -> bindCall.accept("(have uid)"));
        }
    }

    /**
     * 登录后获取服务器uid
     *
     * @param bindType 登录的平台类型：FACEBOOK、GOOGLE
     * @param bindUid  登录的平台uid
     * @param call     登录结果
     */
    public void loginUid(String bindType, String bindUid, Consumer<Integer> call) {
        mReqUser.reqLoginUid(bindType, bindUid, data -> {
            //这个账号本身的uid
            String uid = data.getUid();
            String token = data.getToken();
            String loginType = LoginType.NONE;
            //登录失败
            if (!data.isSuccess() || TextUtils.isEmpty(token)) {
                if (call != null) {
                    call.accept(-1);
                }
                return;
            }
            switch (bindType) {
                case "FACEBOOK":
                    loginType = LoginType.FACEBOOK;
                    break;
                case "GOOGLE":
                    loginType = LoginType.GOOGLE;
                    break;
            }

            //uid存在时表示绑定过账号，此时更新一次uid
            if (!TextUtils.isEmpty(uid)) {
                bind(uid, registrationToken, token, loginType, call);
            } else {
                //尚未绑定账号
                bindAccount(bindType, bindUid, result -> {
                    if (call != null) {
                        call.accept(2);
                    }
                });
            }
        });
    }

    /**
     * 邮箱登录
     *
     * @param email    邮箱
     * @param password 密码
     * @param call     处理结果
     */
    public void loginEmail(String email, String password, Consumer<Integer> call) {
        mReqUser.reqEmailLoginUid(email, password, data -> {
            String uid = data.getUid();
            String token = data.getToken();
            //登录失败
            if (!data.isSuccess() || TextUtils.isEmpty(token)) {
                //通过errorNumber判断具体结果
                if (call != null) {
                    call.accept(data.getErrorNumber());
                }
                return;
            }
            boolean isExist = !TextUtils.isEmpty(uid);
            //uid存在时表示绑定过账号，此时更新一次uid
            if (isExist) {
                bind(uid, registrationToken, token, LoginType.EMAIL, result -> {
                    if (call != null) {
                        call.accept(result);
                    }
                });
                return;
            }
            if (call != null) {
                call.accept(2);
            }
        });
    }

    private void updateIdentity(String uid, String registrationToken, String token, @LoginType String type) {
        mReqUser.setUid(uid, token);
        mData.setUid(uid);
        mData.setToken(token);
        mData.setLoginType(type);
        // 因为前面设置token null ,现在登录需要注册新的token，不然出现登录一直不成功的场景
        com.gjjy.pushlib.Push.get().setRegistrationID(registrationToken);
        mUserSQL.updatedUserDetail(mData);
    }

    /**
     * 绑定登陆信息
     *
     * @param bindType 登录的平台类型：FACEBOOK、GOOGLE
     * @param bindUid  登录的平台uid
     * @param call     绑定结果
     */
    public void bindAccount(String bindType, String bindUid, Consumer<Boolean> call) {
        mReqUser.reqBindAccount(bindType, bindUid, result ->
                bind(result ? 1 : 0, r -> {
                    if (call != null) {
                        call.accept(r == 1);
                    }
                })
        );
    }

    /**
     * 绑定邮箱（注册）
     *
     * @param name  昵称
     * @param email 邮箱
     * @param pwd   密码
     * @param call  绑定结果
     */
    public void bindEmail(String name, String email, String pwd, Consumer<Integer> call) {
        mReqUser.reqEmailBind(name, email, pwd, result -> bind(result, call));
    }

    private void bind(int result, Consumer<Integer> call) {
        //保存绑定结果
        if (!mData.getIsBindAccount()) {
            mData.setIsBindAccount(result == 1);
        }
        mUserSQL.updatedUserDetail(mData);
        if (call != null) {
            call.accept(result);
        }
    }

    //    private boolean isBindPush;
    public void bindPush(Consumer<Boolean> call) {
//        if( isBindPush ) return;
        Push.get().bindRegistrationID(registrationToken -> {
            if (TextUtils.isEmpty(registrationToken)) {
                return;
            }
            this.registrationToken = registrationToken;
            mReqUser.reqPushBind(registrationToken, r -> {
//                isBindPush = r;
                if (call != null) {
                    call.accept(r);
                }
            });
        });
    }

    public void unBindPush(Consumer<Boolean> call) {
        Push.get().unBindRegistrationID(result -> {
            mReqUser.reqPushUnBind(call);
            if (call != null) {
                call.accept(result);
            }
        });
    }

    /**
     * 退出登录
     *
     * @param call 处理结果
     */
    public void logOut(Consumer<Boolean> call) {
        unBindPush(r -> {
            if (call != null) {
                call.accept(r);
            }
        });
        mUserSQL.deleteUserDetail(mData);
        mData.delete();
        mReqUser.setUid(null, null);
    }

    /**
     * 获取排行榜列表
     *
     * @param call 处理结果
     */
    public void getRankingList(Consumer<RankingListEntity> call) {
        mReqUser.reqRankingList(call);
    }

    /**
     * 获取好友列表
     *
     * @param page 第几页
     * @param call 回调结果
     */
    public void getFriendsList(int page, Consumer<FriendListEntity> call) {
        mReqUser.reqFriendsList(page, call);
    }

    /**
     * 填写邀请人的邀请码
     *
     * @param code 邀请码
     * @param call 请求结果
     */
    public void editFriendInvite(String code, Consumer<Boolean> call) {
        mReqUser.reqEditFriendInvite(code, call);
    }

    /**
     * 发送手机验证码
     *
     * @param phone       手机号
     * @param countryCode 国家号
     * @param call        请求结果
     */
    public void reqSendSmsCode(String phone, String countryCode, Consumer<BaseReqEntity> call) {
        mReqUser.reqSendSmsCode(phone, countryCode, call);
    }

    // 注册新用户
    public void reqUserRegister(UserRegisterReq req, Consumer<UserRegisterResp> call) {
        mReqUser.reqUserRegister(req, call);
    }

    @Override
    public void onResult(int id, Object data) {
        if (id == DOMConstant.LOG_OFF) {
            logOut(null);
        }
    }
}
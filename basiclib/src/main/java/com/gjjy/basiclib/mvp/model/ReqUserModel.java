package com.gjjy.basiclib.mvp.model;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.api.apiFcm.PushBindApi;
import com.gjjy.basiclib.api.apiFcm.PushUnBindApi;
import com.gjjy.basiclib.api.apiOther.RankingListApi;
import com.gjjy.basiclib.api.apiUser.AddAnswerApi;
import com.gjjy.basiclib.api.apiUser.AddUserApi;
import com.gjjy.basiclib.api.apiUser.AwardApi;
import com.gjjy.basiclib.api.apiUser.BindAccountApi;
import com.gjjy.basiclib.api.apiUser.CreateInviteCodeApi;
import com.gjjy.basiclib.api.apiUser.EditFriendInviteApi;
import com.gjjy.basiclib.api.apiUser.EditUserInfoApi;
import com.gjjy.basiclib.api.apiUser.EmailBindApi;
import com.gjjy.basiclib.api.apiUser.EmailLoginUidApi;
import com.gjjy.basiclib.api.apiUser.ReqDetailApi;
import com.gjjy.basiclib.api.apiUser.ReqFriendsListApi;
import com.gjjy.basiclib.api.apiUser.ReqLoginUidApi;
import com.gjjy.basiclib.api.apiUser.ResetEmailPasswordApi;
import com.gjjy.basiclib.api.apiUser.ResetEmailPasswordByCodeApi;
import com.gjjy.basiclib.api.apiUser.SendEmailCodeApi;
import com.gjjy.basiclib.api.apiUser.SendSmsCodeApi;
import com.gjjy.basiclib.api.apiUser.UserRegisterApi;
import com.gjjy.basiclib.api.entity.AwardEntity;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.api.entity.FriendListEntity;
import com.gjjy.basiclib.api.entity.InviteCodeEntity;
import com.gjjy.basiclib.api.entity.RankingListEntity;
import com.gjjy.basiclib.api.entity.UidEntity;
import com.gjjy.basiclib.api.entity.UserDetailEntity;
import com.gjjy.basiclib.entity.UserRegisterReq;
import com.gjjy.basiclib.entity.UserRegisterResp;
import com.ybear.ybnetworkutil.request.Request;
import com.ybear.ybutils.utils.time.DateTime;

public class ReqUserModel extends BasicGlobalReqModel {
    private void onCallResultOfError(Request api, Consumer<Integer> call) {
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            BaseReqEntity data = toReqEntityOfBase(s);
            call.accept(data == null ? 0 : data.isSuccess() ? 1 : data.getErrorNumber());
        });
    }

    private void onCallUid(Request api, Consumer<UidEntity> call) {
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            UidEntity data = toReqEntity(s, UidEntity.class);
            call.accept(data == null ? new UidEntity() : data);
        });
    }

    /**
     * ????????????
     *
     * @param call ????????????
     */
    public void reqAddUser(Consumer<UidEntity> call) {
        AddUserApi api = new AddUserApi();
        onCallUid(api, call);
        reqApi(api);
    }

    /**
     * ???????????????????????????
     *
     * @param levelType  ???????????????1????????????2???????????????3??????
     * @param motiveType ???????????????1???????????????2?????????3???????????????4?????????5?????????6?????????????????????7??????
     * @param call       ????????????
     */
    public void reqAddAnswer(int levelType, int motiveType, Consumer<Boolean> call) {
        AddAnswerApi api = new AddAnswerApi();
        api.addParam("level_type", levelType);
        api.addParam("motive_type", motiveType);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ????????????????????????
     *
     * @param call ????????????
     */
    public void reqAward(Consumer<AwardEntity> call) {
        AwardApi api = new AwardApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            AwardEntity data = toReqEntity(s, AwardEntity.class);
            call.accept(data == null ? new AwardEntity() : data);
        });
        reqApi(api);
    }

    /**
     * ??????????????????
     *
     * @param nickname  ??????
     * @param avatarUrl ????????????
     * @param gender    ?????????0?????????1??????2???
     * @param phone     ????????????
     * @param email     ??????
     * @param call      ????????????
     */
    public void reqEditUserInfo(@Nullable String nickname,
                                @Nullable String avatarUrl,
                                @IntRange(from = -1, to = 2) int gender,
                                @Nullable String phone,
                                @Nullable String email,
                                @Nullable Consumer<Boolean> call) {
        EditUserInfoApi api = new EditUserInfoApi();
        if (nickname != null) api.addParam("nickname", nickname);
        if (avatarUrl != null) api.addParam("avatar_url", avatarUrl);
        if (gender != -1) api.addParam("gender", gender);
        if (phone != null) api.addParam("phone", phone);
        if (email != null) api.addParam("email", email);
        api.addParam("time_zone", DateTime.timeZoneToSecond(DateTime.currentTimeZone(
                false,
                true,
                false,
                true
        )));
        //???????????????????????????
        api.addParam("last_time", System.currentTimeMillis() / 1000L);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ??????????????????
     *
     * @param call ????????????
     */
    public void reqDetail(Consumer<UserDetailEntity> call) {
        ReqDetailApi api = new ReqDetailApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            UserDetailEntity data = toReqEntity(s, "user_info", UserDetailEntity.class);
            call.accept(data == null ? new UserDetailEntity() : data);
        });
        reqApi(api);
    }

    /**
     * ????????????
     *
     * @param call ????????????
     */
    public void reqUpdatedTimeZone(@Nullable Consumer<Boolean> call) {
        reqEditUserInfo(null, null, -1, null, null, call);
    }

    /**
     * ????????????????????????uid
     *
     * @param bindType ????????????????????????FACEBOOK???GOOGLE???APPLE
     * @param bindUid  ???????????????uid
     * @param call     ????????????
     */
    public void reqLoginUid(String bindType, String bindUid, Consumer<UidEntity> call) {
        ReqLoginUidApi api = new ReqLoginUidApi();
        api.addParam("bind_type", bindType);
        api.addParam("bind_uid", bindUid);
        onCallUid(api, call);
        reqApi(api);
    }

    /**
     * ??????????????????
     *
     * @param bindType ????????????????????????FACEBOOK???GOOGLE???APPLE
     * @param bindUid  ???????????????uid
     * @param call     ????????????
     */
    public void reqBindAccount(String bindType, String bindUid, Consumer<Boolean> call) {
        BindAccountApi api = new BindAccountApi();
        api.addParam("bind_type", bindType);
        api.addParam("bind_uid", bindUid);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ?????? - ??????
     *
     * @param name  ??????
     * @param email ??????
     * @param pwd   ??????
     * @param call  ????????????
     */
    public void reqEmailBind(String name, String email, String pwd, Consumer<Integer> call) {
        EmailBindApi api = new EmailBindApi();
        api.addParam("nickname", name);
        api.addParam("bind_uid", email);
        api.addParam("password", pwd);
        onCallResultOfError(api, call);
        reqApi(api);
    }

    /**
     * ?????? - ??????
     *
     * @param email ??????
     * @param pwd   ??????
     * @param call  ????????????
     */
    public void reqEmailLoginUid(String email, String pwd, Consumer<UidEntity> call) {
        EmailLoginUidApi api = new EmailLoginUidApi();
        api.addParam("bind_uid", email);
        api.addParam("password", pwd);
        onCallUid(api, call);
        reqApi(api);
    }

    /**
     * ?????? - ????????????
     *
     * @param oldPwd ?????????
     * @param newPwd ?????????
     * @param call   ????????????
     */
    public void reqResetEmailPassword(String oldPwd, String newPwd, Consumer<Boolean> call) {
        ResetEmailPasswordApi api = new ResetEmailPasswordApi();
        api.addParam("password", oldPwd);
        api.addParam("new_password", newPwd);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ?????? - ?????????????????????
     *
     * @param email ??????
     * @param call  ????????????
     */
    public void reqSendEmailCode(String email, Consumer<Integer> call) {
        SendEmailCodeApi api = new SendEmailCodeApi();
        api.addParam("email", email);
        onCallResultOfError(api, call);
        reqApi(api);
    }

    /**
     * ?????? - ?????????????????????
     *
     * @param emailCode ???????????????
     * @param password  ??????
     * @param call      ????????????
     */
    public void reqResetEmailPasswordByCode(String emailCode,
                                            String email,
                                            String password,
                                            Consumer<Boolean> call) {
        ResetEmailPasswordByCodeApi api = new ResetEmailPasswordByCodeApi();
        api.addParam("email_code", emailCode);
        api.addParam("email", email);
        api.addParam("password", password);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ???????????????
     *
     * @param call ????????????
     */
    public void reqCreateInviteCode(Consumer<InviteCodeEntity> call) {
        CreateInviteCodeApi api = new CreateInviteCodeApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            InviteCodeEntity data = toReqEntity(s, InviteCodeEntity.class);
            call.accept(data != null ? data : new InviteCodeEntity());
        });
        reqApi(api);
    }

    /**
     * ???????????????
     *
     * @param call ????????????
     */
    public void reqRankingList(Consumer<RankingListEntity> call) {
        RankingListApi api = new RankingListApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            RankingListEntity data = toReqEntity(s, RankingListEntity.class);
            call.accept(data != null ? data : new RankingListEntity());
        });
        reqApi(api);
    }

    /**
     * ??????????????????
     *
     * @param call ????????????
     */
    public void reqFriendsList(int page, Consumer<FriendListEntity> call) {
        ReqFriendsListApi api = new ReqFriendsListApi();
        api.addParam("page", page);
        api.addParam("pagesize", 20);
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            FriendListEntity data = toReqEntityNotBase(s, FriendListEntity.class);
            call.accept(data != null ? data : new FriendListEntity());
        });
        reqApi(api);
    }

    /**
     * ???????????????????????????
     *
     * @param code ?????????
     * @param call ????????????
     */
    public void reqEditFriendInvite(String code, Consumer<Boolean> call) {
        EditFriendInviteApi api = new EditFriendInviteApi();
        api.addParam("friend_invite_code", code);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ????????????
     *
     * @param fcmToken token
     * @param call     ????????????
     */
    public void reqPushBind(String fcmToken, Consumer<Boolean> call) {
        PushBindApi api = new PushBindApi();
        api.addParam("registration_token", fcmToken);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ????????????
     *
     * @param call ????????????
     */
    public void reqPushUnBind(Consumer<Boolean> call) {
        PushUnBindApi api = new PushUnBindApi();
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * ?????????????????????
     *
     * @param phone       ?????????
     * @param countryCode ?????????
     * @param call        ????????????
     */
    public void reqSendSmsCode(String phone, String countryCode, Consumer<BaseReqEntity> call) {
        SendSmsCodeApi api = new SendSmsCodeApi();
        api.addParam("phone", phone);
        api.addParam("countryCode", countryCode);
        api.addParam("lang", Config.getLang());
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            BaseReqEntity data = toReqEntityOfBase(s, BaseReqEntity.class);
            call.accept(data != null ? data : new BaseReqEntity());
        });
        reqApi(api);
    }

    /**
     * ??????????????????
     * @param req ??????????????????
     * @param call
     */
    public void reqUserRegister(UserRegisterReq req, Consumer<UserRegisterResp> call) {
        UserRegisterApi api = new UserRegisterApi();
        api.addParam("nickname", req.getNickname());
        api.addParam("email", req.getEmail());
        api.addParam("password", req.getPassword());
        api.addParam("phone", req.getPhone());
        api.addParam("smsCode", req.getSmsCode());
        api.addParam("userId", req.getUserId());
        api.addParam("lang", req.getLang());
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            UserRegisterResp data = toReqEntityOfBase(s, UserRegisterResp.class);
            call.accept(data != null ? data : new UserRegisterResp());
        });
        reqApi(api);
    }
}

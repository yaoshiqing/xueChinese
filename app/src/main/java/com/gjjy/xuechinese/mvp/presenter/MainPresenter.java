package com.gjjy.xuechinese.mvp.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.api.entity.CheckAnnouncementEntity;
import com.gjjy.basiclib.api.entity.CheckUpdateEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.dao.sql.SetUpSQL;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.ReqConfigModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.xuechinese.R;
import com.gjjy.xuechinese.mvp.model.PathType;
import com.gjjy.xuechinese.mvp.model.SchemeModel;
import com.gjjy.xuechinese.mvp.view.MainView;
import com.gjjy.xuechinese.ui.MainActivity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybnetworkutil.network.NetworkChangeManage;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.SysUtil;

public class MainPresenter extends MvpPresenter<MainView> {
    @Model
    private UserModel mUserModel;
    @Model
    private SetUpSQL mSetUpSQL;
    @Model
    private OtherModel mOtherModel;
    @Model
    private SchemeModel mSchemeModel;
    @Model
    private ReqConfigModel mReqConfigModel;

    private CheckUpdateEntity mUpdateCheckData;
    private CheckAnnouncementEntity mCheckAnnouncementEntity;
    // private boolean isReCheckVersion = false;

    public MainPresenter(@NonNull MainView view) {
        super(view);
    }

    @Override
    public void onLifeNewIntent(Intent intent) {
        super.onLifeNewIntent(intent);
        initIntent(intent);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mOtherModel.refreshUid(mUserModel.getUid(), mUserModel.getToken());
        mOtherModel.bindPushId(null);
        //????????????
        mReqConfigModel.reqAbleToShare();
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        //??????????????????????????????
        mUserModel.getUserDetail(null);
        //??????uid
        mOtherModel.refreshUid(mUserModel.getUid(), mUserModel.getToken());
        //????????????????????????oss
        mOtherModel.activityCheckInitOSS(activity.getApplication());
        //????????????????????????
//        if (isReCheckVersion) {
//            checkVersion();
//        }
        //?????????????????????
        doInviteFriendsCode();
        //????????????
        mUserModel.bindPush(null);
    }

    @Override
    public void onLifeDestroy() {
        mSetUpSQL.restoreSetUp();
        super.onLifeDestroy();
    }

    public void initIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Bundle data = intent.getBundleExtra(Constant.MAIN_PUSH_BUNDLE);
        if (data != null) {
            doPushExtraMsg(data);
        }

        //Scheme????????????app
        mSchemeModel.setOnIntentListener(type -> {
            int id = mSchemeModel.getId();
            String videoId = mSchemeModel.getVideoId();
            String name = mSchemeModel.getName();
            viewCall(v -> v.onCallDispatchScheme(type, id, videoId, name), 1000);
            switch (type) {
                case PathType.LISTEN_DAILY:  //????????????
                    //??????
                    BuriedPointEvent.get().onCourseDetailLandingPageOfListenEverydayOfOpenAPPButton(
                            getContext(),
                            mUserModel.getUid(), mUserModel.getUserName(getResources()),
                            id, name
                    );
                    break;
                case PathType.POPULAR_VIDEOS:        //????????????
                    com.gjjy.discoverylib.utils.StartUtil.startPopularVideosDetailsActivity(id, videoId, name);
                    break;
                case PathType.TARGETED_LEARNING:          //????????????
                    com.gjjy.discoverylib.utils.StartUtil.startTargetedLearningDetailsActivity(id, videoId);
                    //??????
                    BuriedPointEvent.get().onVideoGrammarCourseListPageOfCourseUnlockPopupOfUnlockButton(
                            getContext(),
                            mUserModel.getUid(), mUserModel.getUserName(getResources()),
                            id, name
                    );
                    break;
            }
        });
        mSchemeModel.initIntent(intent);
    }

    private void doInviteFriendsCode() {
        if (getContext() == null) {
            return;
        }
        if (!mUserModel.isLoginResult()) {
            return;
        }
        if (!TextUtils.isEmpty(mUserModel.getFriendInvitationCode())) {
            return;
        }
        SysUtil.pasteTextFromClipboards(getContext(), items -> {
            if (items == null) return;
            for (ClipData.Item item : items) {
                if (item == null) {
                    continue;
                }
                CharSequence text = item.getText();
                if (TextUtils.isEmpty(text) || text.length() < 6) {
                    continue;
                }
                //??????????????????????????????
                if (!ObjUtils.isEnglishAndNumber(text.toString(), 1, 1)) {
                    continue;
                }
                //???????????????
                mUserModel.editFriendInvite(item.getText().toString(), null);
            }
        });
    }

    public long getUserId() {
        return mUserModel.getUserId();
    }

    public String getEmail() {
        return mUserModel.getEmail();
    }

    public void startLoginCheckActivity() {
        mUserModel.startLoginCheckActivity(getActivity(), false);
    }

    public void checkFrontInitGuide() {
        if (!SpIO.isShowFrontInitGuide(getContext())) {
            return;
        }
        viewCall(MainView::onCallShowFrontInitGuide);
    }

    public void checkFindInitGuide() {
        if (!SpIO.isShowFindInitGuide(getContext())) {
            return;
        }
        viewCall(MainView::onCallShowFindInitGuide);
    }

    public void checkMeInvitationCodeInitGuide() {
        if (!SpIO.isShowMeInvitationCodeInitGuideGuide(getContext())) {
            return;
        }
        viewCall(MainView::onCallShowMeInvitationCodeInitGuide);
    }


    public void logOut() {
        post(() -> mUserModel.doLogOut(getActivity()));
    }

    public void logOutOfOnActivityResult(int requestCode, int resultCode) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.getStackManage().exit(MainActivity.class);
        mUserModel.doLogOutOfOnActivityResult(getActivity(), requestCode, resultCode);
    }

    private void doPushExtraMsg(Bundle data) {
        Context context = getContext();
        if (context == null || data == null || data.size() == 0) {
            return;
        }
//        //??????????????????
//        String title = json.getString( "title" );
        String title;
        //???????????????1001???????????????????????????
        String link = null;
        //??????????????????id????????????????????????????????????
        int id = 0;
        try {
            link = data.containsKey("link") ? data.getString("link") : null;
            id = data.containsKey("m_id") ? ObjUtils.parseInt(data.getString("m_id")) : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (link == null || link.length() == 0) {
            return;
        }
        switch (link) {
            case "1001":    //???????????????
                try {
                    int msgType = data.containsKey("msg_type") ? ObjUtils.parseInt(data.getString("msg_type")) : 0;
                    title = context.getResources().getString(msgType == 2 ? com.gjjy.usercenterlib.R.string.stringMsgCenterVipNotify : com.gjjy.usercenterlib.R.string.stringMsgCenterSystemNotify);
                    String videoId = data.containsKey("video_id") ? data.getString("video_id") : "";

                    StartUtil.startMsgDetailsActivity(
                            getActivity(),
                            videoId,
                            id,
                            title
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "1002":    //??????????????? ????????????
                try {
                    long disArtId = data.containsKey("discover_article_id") ? ObjUtils.parseLong(data.getString("discover_article_id")) : 0;
                    int talkId = data.containsKey("talk_id") ? ObjUtils.parseInt(data.getString("talk_id")) : 0;
                    int interactType = data.containsKey("interact_type") ? ObjUtils.parseInt(data.getString("interact_type")) : 0;
                    String videoId = data.containsKey("video_id") ? data.getString("video_id") : "";

                    StartUtil.startTargetedLearningDetailsActivity(disArtId, talkId, videoId, interactType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        viewCall(MainView::onCallToFront);
        LogUtil.d("doPushExtraMsg -> " + "id:" + id + " | " + "link:" + link);
    }

    public String getDoubleBackPressedExitString() {
        Activity activity = getActivity();
        return activity != null ? String.format(activity.getString(R.string.stringDoubleExit), AppUtil.getAppName(activity)) : "";
    }

    public void unNetworkService() {
        NetworkChangeManage.get().unregisterNetworkChangeAll();
        NetworkChangeManage.get().unregisterService();
    }

    public void checkAnnouncement() {
        mOtherModel.popupNotice(data -> {
            mCheckAnnouncementEntity = data;
            callAnnouncementData();
        });
    }

//    public void checkVersion() {
//        if (!mUserModel.isExistUid()) {
//            isReCheckVersion = true;
//            return;
//        }
//        isReCheckVersion = false;
//        mOtherModel.updateCheck(data -> {
//            mUpdateCheckData = data;
//            callUpdateData();
//        });
//    }

    private void callAnnouncementData() {
        if (mCheckAnnouncementEntity == null) {
            return;
        }
        //??????????????????
        if (TextUtils.isEmpty(mCheckAnnouncementEntity.getUrl())) {
            return;
        }
        //???????????????
        if (!SpIO.isShowAnnouncement(getContext(), mCheckAnnouncementEntity.getUpdateTime())) {
            return;
        }
        //??????????????????
        SpIO.saveAnnouncementStatus(getContext(), mCheckAnnouncementEntity.getUpdateTime());

        viewCall(v -> v.onCheckAnnouncement(mCheckAnnouncementEntity.getUrl()));
    }

    private void callUpdateData() {
        if (mUpdateCheckData == null) {
            return;
        }
        int status = mUpdateCheckData.getStatus();
        //????????????
        if (status == 0) {
            //????????????
            checkAnnouncement();
            return;
        }
        viewCall(v -> v.onCheckUpdate(
                mUpdateCheckData.getNewVersion(),
                mUpdateCheckData.getDescription(),
                mUpdateCheckData.getUrl(),
                status == 1)
        );
    }

    public void updateJumpEvent() {
        SysUtil.startGooglePlay(getActivity());
    }

    public void buriedPointClickFindPage() {
        BuriedPointEvent.get().onTabBarOfDiscoveryButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName(getResources())
        );
    }
}

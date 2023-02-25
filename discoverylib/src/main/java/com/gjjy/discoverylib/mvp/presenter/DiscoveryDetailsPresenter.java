package com.gjjy.discoverylib.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.api.entity.DiscoveryDetailEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonContentEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonContentGrammarEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonContentSentenceEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonContentTalkEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonRecordEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.ReqConfigModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.DialogueContentAdapter;
import com.gjjy.discoverylib.adapter.GrammarExampleAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsDialogueAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsGrammarAdapter;
import com.gjjy.discoverylib.mvp.model.DiscoverArticleModel;
import com.gjjy.discoverylib.mvp.view.FindDetailsView;
import com.gjjy.discoverylib.mvp.view.ListenDailyDetailsView;
import com.gjjy.discoverylib.mvp.view.PopularVideosDetailsView;
import com.gjjy.discoverylib.mvp.view.TargetedLearningDetailsView;
import com.gjjy.discoverylib.widget.VerticalScrollSubtitlesView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.sharesdk.FacebookShare;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.toast.ToastManage;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryDetailsPresenter extends MvpPresenter<FindDetailsView> {
    @Model
    private UserModel mUserModel;
    @Model
    private DiscoverArticleModel mDisArtModel;
    @Model
    private ReqConfigModel mReqConfigModel;

    private long mId;
    private String mVideoId;
    private String mTitle;
    private String mSummary;
    private String mImgUrl;
    private String mType;
    private int mTopCommentTalkId;
    private int mTopCommentInteractType;
    private int mDiscoverTypeId;
    private String mWebUrl;
    private long mViewDuration;

    private boolean isCollect;
    // 0：无，1：收藏，2：分享
    private int mLoginLastOpenType = 0;

    public DiscoveryDetailsPresenter(@NonNull FindDetailsView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mDisArtModel.setUid(mUserModel.getUid(), mUserModel.getToken());
        mViewDuration = System.currentTimeMillis();

        // 是否显示分享按钮
        viewCall(v -> v.onCallShowShareButton(mReqConfigModel.isEnableShare()));
    }

    @Override
    public void onLifeDestroy() {
        buriedPointDialogue();
        buriedPointGrammar();
        buriedPointComments();
        super.onLifeDestroy();
    }

    public void initIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        mId = intent.getLongExtra(Constant.ID_FOR_INT, -1);
        mVideoId = intent.getStringExtra(Constant.ID_FOR_VIDEO);
        // 默认Dialogue页面 0：Dialogue，1：Grammar，2：Comments
        mType = intent.getStringExtra(Constant.TYPE);
        mTopCommentTalkId = intent.getIntExtra(Constant.TOP_COMMENT_TALK_ID, -1);
        mTopCommentInteractType = intent.getIntExtra(Constant.TOP_COMMENT_INTERACT_TYPE, -1);

        LogUtil.e("FindDetails -> initIntent -> " +
                "mId:" + mId + " | " +
                "mVideoId:" + mVideoId + " | " +
                "mTitle:" + mTitle + " | " +
                "mType:" + mType + " | " +
                "mTopCommentTalkId:" + mTopCommentTalkId + " | " +
                "mTopCommentInteractType:" + mTopCommentInteractType
        );
    }

    @Override
    public void onTriggerActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onTriggerActivityResult(requestCode, resultCode, data);
        FacebookShare.get().onActivityResult(requestCode, resultCode, data);
        if (requestCode == StartUtil.REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            doLoginLastOpenType();
        }
        if (requestCode == StartUtil.REQUEST_CODE_BUY_VIP && resultCode != Activity.RESULT_OK) {
            finish();
        }
        LogUtil.e("FindDetails -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode + " | " +
                "data:" + data
        );
    }

    public String getType() {
        return mType;
    }

    private boolean isRequesting = false;

    public void requestData() {
        if (isRequesting) {
            return;
        }
        if (mId == -1) {
            finish();
            return;
        }
        isRequesting = true;
        viewCall(v -> v.onCallShowLoadingDialog(true));
        mDisArtModel.reqFindDetail(mId, data -> {
            viewCall(v -> {
                // 文章拉取失败
                if (data == null || data.getDiscoverArticleId() == 0) {
                    if (getContext() != null) {
                        ToastManage.get().showToast(getContext(), R.string.stringError);
                    }
                    finish();
                    return;
                }
                // 会员检查
                if (!checkIsVip(data)) {
                    return;
                }
                DiscoveryDetailJsonContentEntity content = data.getJsonContent();

                v.onCallTitle(mTitle = data.getTitle());
                mSummary = data.getSummary();
                mImgUrl = data.getBigImgUrl();

                switch (mDiscoverTypeId = data.getDiscoverTypeId()) {
                    case 1001:      // 每日聆听
                        // 语言特殊处理，把playurl 传递给videoId
                        mVideoId = content.getPlayUrl();
                        doListenDailyDetails(data.getBigImgUrl(), content.getSentence());
                        break;
                    case 1002:      // 热门视频
                        doPopularVideosDetails(data.getHtmlUrl());
                        break;
                    case 1003:      // 专项学习
                        doTargetedLearningDetails(
                                data.getDiscoverArticleId(), data.getSummary(),
                                content.getTalk(), content.getGrammar()
                        );
                        break;
                }
                v.onCallVideoId(
                        mVideoId,
                        data.getJsonRecord().getPlayProgressSecond() * 1000,
                        content.getPlaySecond() * 1000
                );
                v.onCallCollectStatus(data.isCollection(), false);
                v.onCallShowLoadingDialog(false);
                isRequesting = false;
            });
        });
    }

    private boolean checkTargetedLearningDetailsInitGuide() {
        if (!SpIO.isShowTargetedLearningDetailsInitGuide(getContext())) {
            return false;
        }
        viewCall(v -> ((TargetedLearningDetailsView) v).onCallShowTargetedLearningDetailsInitGuide());
        return true;
    }

    private boolean checkIsVip(DiscoveryDetailEntity data) {
//        mUserModel.setVip( false );
//        data.setVip( true );
        boolean isVipCourse = data.isVip();

        if (!mUserModel.isVip() && isVipCourse) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity == null) return false;
            activity.showUnlockVipDialog(
                    mUserModel.getUid(), mUserModel.getUserName(getResources()),
                    data.getDiscoverTypeId(), data.getDiscoverArticleId(),
                    data.getTitle(), data.getBigImgUrl(),
                    data.getTitle(), data.getSummary(),
                    result -> {
                        if (!result) {
                            finish();
                        }
                    }
            );
            return false;
        }
        return true;
    }

    private void doListenDailyDetails(String imgUrl, List<DiscoveryDetailJsonContentSentenceEntity> sentenceList) {
        if (sentenceList == null) {
            return;
        }
        List<VerticalScrollSubtitlesView.VssAdapter.ItemData> subtitlesList = new ArrayList<>();
        for (DiscoveryDetailJsonContentSentenceEntity sentence : sentenceList) {
            VerticalScrollSubtitlesView.VssAdapter.ItemData subtitlesData = new VerticalScrollSubtitlesView.VssAdapter.ItemData();
            subtitlesData.setPinyin(sentence.getPinyin());
            subtitlesData.setChineseChar(sentence.getZh());
            subtitlesData.setTranslate(sentence.getEn());
            subtitlesData.setDuration(sentence.getMillisecond());
            subtitlesList.add(subtitlesData);
        }
        viewCall(v -> ((ListenDailyDetailsView) v).onCallSubtitlesData(imgUrl, subtitlesList));
    }

    private void doPopularVideosDetails(String htmlUrl) {
        viewCall(v -> ((PopularVideosDetailsView) v).onCallHtmlUrl(htmlUrl));
    }

    private void doTargetedLearningDetails(long discoverArticleId,
                                           String introduction,
                                           List<DiscoveryDetailJsonContentTalkEntity> talkList,
                                           List<DiscoveryDetailJsonContentGrammarEntity> grammarList) {
        checkTargetedLearningDetailsInitGuide();
        new Thread(() -> {
            //语法
            List<TargetedLearningDetailsDialogueAdapter.ItemData> dialogueItemList = toTargetedLearningDetailsTalkList(talkList);
            //对话
            List<TargetedLearningDetailsGrammarAdapter.ItemData> grammarItemList = toTargetedLearningDetailsGrammarList(grammarList);
            viewCall(v -> {
                TargetedLearningDetailsView callV = (TargetedLearningDetailsView) v;
                callV.onCallCommentInfo(
                        discoverArticleId, mTopCommentTalkId, mTopCommentInteractType
                );
                callV.onCallIntroduction(introduction);
                callV.onCallDialogueDataList(dialogueItemList);
                callV.onCallGrammarDataList(grammarItemList);
            });
        }).start();
    }

    /**
     * 专项学习 - 转换语法列表
     *
     * @param talkList 列表
     * @return 转换结果
     */
    private List<TargetedLearningDetailsDialogueAdapter.ItemData> toTargetedLearningDetailsTalkList(List<DiscoveryDetailJsonContentTalkEntity> talkList) {
        List<TargetedLearningDetailsDialogueAdapter.ItemData> retList = new ArrayList<>();
        for (DiscoveryDetailJsonContentTalkEntity data : talkList) {
            TargetedLearningDetailsDialogueAdapter.ItemData item = new TargetedLearningDetailsDialogueAdapter.ItemData();
            // 标题
            item.setTitle(data.getRole());
            // 翻译
            item.setTranslate(data.getEnSentence());
            // 音频链接
            item.setAudioUrl(data.getAudioUrl());
            // 内容
            List<DialogueContentAdapter.ItemData> contentList = new ArrayList<>();
            for (String zh : data.getZhSentence()) {
                DialogueContentAdapter.ItemData contentItem = new DialogueContentAdapter.ItemData();
                for (DiscoveryDetailJsonContentSentenceEntity key : data.getKeyword()) {
                    String keyZh = key.getZh();
                    if (TextUtils.isEmpty(keyZh) || !keyZh.equals(zh)) {
                        continue;
                    }
                    contentItem.setPinyin(key.getPinyin());
                    contentItem.setTranslate(key.getEn());
                    break;
                }
                contentItem.setContent(zh);
                contentList.add(contentItem);
            }
            item.setContent(contentList);
            // 关键字
            String[] discolorTexts = new String[data.getKeyword().size()];
            for (int i = 0; i < discolorTexts.length; i++) {
                discolorTexts[i] = data.getKeyword().get(i).getZh();
            }
            item.setDiscolorTexts(discolorTexts);

            retList.add(item);
        }
        return retList;
    }

    /**
     * 专项学习 - 转换对话列表
     *
     * @param grammarList 列表
     * @return 转换结果
     */
    private List<TargetedLearningDetailsGrammarAdapter.ItemData> toTargetedLearningDetailsGrammarList(List<DiscoveryDetailJsonContentGrammarEntity> grammarList) {
        List<TargetedLearningDetailsGrammarAdapter.ItemData> retList = new ArrayList<>();
        for (DiscoveryDetailJsonContentGrammarEntity data : grammarList) {
            TargetedLearningDetailsGrammarAdapter.ItemData item = new TargetedLearningDetailsGrammarAdapter.ItemData();
            String[] keyword = data.getKeyword();
            item.setTitle(toDisColorText(data.getZhSentence(), keyword));
            item.setContent(toDisColorText(data.getExplain(), keyword));
            // Example
            if (data.getExample() != null) {
                for (DiscoveryDetailJsonContentSentenceEntity example : data.getExample()) {
                    GrammarExampleAdapter.ItemData exampleItem = new GrammarExampleAdapter.ItemData();
                    exampleItem.setTitle(toDisColorText(example.getZh(), keyword));
                    exampleItem.setContent(toDisColorText(example.getEn(), keyword));
                    item.addExample(exampleItem);
                }
            }
            item.setNotes(data.getRemark());
            retList.add(item);
        }
        return retList;
    }

    private String toDisColorText(String text, String... disColorText) {
        if (TextUtils.isEmpty(text) || disColorText == null || disColorText.length == 0) {
            return text;
        }
        for (String dcText : disColorText) {
            dcText = dcText.replaceAll("\\(", "（")
                    .replaceAll("\\)", "）")
                    .replaceAll("\"", "\\\"");
            text = text.replaceAll(dcText, "<font color='#F5A422'>" + dcText + "</font>");
        }
        return text;
    }

    public void saveCurrentProgress(int progress) {
        mDisArtModel.saveReadRecord(
                mId,
                new DiscoveryDetailJsonRecordEntity(progress / 1000)
        );
    }

    public boolean editCollectStatus(boolean isCollect) {
        this.isCollect = isCollect;
        if (checkIsLogin()) {
            mLoginLastOpenType = 1;
            return false;
        }
        mLoginLastOpenType = 0;
        mDisArtModel.editCollectStatus(mId, isCollect);
        return true;
    }

    public void share() {
        if (checkIsLogin()) {
            mLoginLastOpenType = 2;
            return;
        }
        mLoginLastOpenType = 0;
        String shareUrl;
        switch (mDiscoverTypeId) {
            case 1001:      //每日聆听
                shareUrl = Config.mShareListenDailyUrl;
                break;
            case 1002:      //热门视频
                shareUrl = Config.mSharePopularVideosUrl;
                break;
            case 1003:      //专项学习
                shareUrl = Config.mShareTargetedLearningUrl;
                break;
            default:
                shareUrl = null;
                break;
        }

        if (TextUtils.isEmpty(shareUrl)) {
            viewCall(v -> v.onCallShareResult(false));
            return;
        }
        //追加参数
        shareUrl += String.format(
                "?id=%s&textTitle=%s&textMain=%s&textImg=%s&vipStates=%s&code=%s",
                mId, mTitle, mSummary, mImgUrl,
                mUserModel.isVip() ? 1 : 0, mUserModel.getInvitationCode()
        );

        FacebookShare fbShare = FacebookShare.get();
        ShareLinkContent content = fbShare.createShareLink(shareUrl);

        LogUtil.e("FindDetails -> share -> shareUrl:" + shareUrl);

        fbShare.share(getActivity(), content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                callResult(true);
            }

            @Override
            public void onCancel() {
                callResult(false);
            }

            @Override
            public void onError(FacebookException error) {
                callResult(false);
            }

            private void callResult(boolean result) {
                viewCall(v -> v.onCallShareResult(result));
            }
        });
    }

    private boolean checkIsLogin() {
        return mUserModel.startLoginCheckActivity(getActivity());
    }

    private void doLoginLastOpenType() {
        LogUtil.e("doLoginLastOpenType -> " + mLoginLastOpenType);
        switch (mLoginLastOpenType) {
            case 1:     // 收藏
                editCollectStatus(isCollect);
                viewCall(v -> v.onCallCollectStatus(isCollect, true));
                break;
            case 2:     // 分享
                post(this::share, 250);
                break;
        }
        mLoginLastOpenType = 0;
    }

    public void buriedPointCollectionOfListenDaily(Context context, boolean isCollect) {
        BuriedPointEvent.get().onListenEverydayDetailPageOfCollection(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    public void buriedPointShareOfListenDaily(Context context, boolean isCollect) {
        BuriedPointEvent.get().onListenEverydayDetailPageOfTranspondButton(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    public void buriedPointDurationOfListenDaily(Context context) {
        BuriedPointEvent.get().onListenEverydayLessonsListOfListenEverydayLessons(
                context,
                mUserModel.getUid(),
                mUserModel.getUserName(getResources()),
                mId, mTitle,
                System.currentTimeMillis() - mViewDuration
        );
    }

    public void buriedPointCollectionOfPopularVideos(Context context, boolean isCollect) {
        BuriedPointEvent.get().onPopularVideoDetailPageOfCollection(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    public void buriedPointShareOfPopularVideos(Context context, boolean isCollect) {
        BuriedPointEvent.get().onPopularVideoDetailPageOfTranspondButton(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    public void buriedPointDurationOfPopularVideos() {
        BuriedPointEvent.get().onPopularVideoListOfPopularVideoLessons(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName(getResources()),
                mId, mTitle,
                System.currentTimeMillis() - mViewDuration
        );
    }

    public void buriedPointCollectionOfTargetedLearning(Context context, boolean isCollect) {
        BuriedPointEvent.get().onListenEverydayDetailPageOfCollection(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    public void buriedPointPlayVideoBtn(Context context) {
        BuriedPointEvent.get().onVideoGrammarCourseDetailPageOfVideoPlayButton(
                context,
                mUserModel.getUid(),
                mUserModel.getUserName(getResources()),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                mId,
                mTitle
        );
    }

    public void buriedPointViewDurationOfTargetedLearning(Context context) {
        BuriedPointEvent.get().onVideoGrammarCourseDetailPage(
                context,
                mUserModel.getUid(),
                mUserModel.getUserName(getResources()),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                mId,
                mTitle,
                System.currentTimeMillis() - mViewDuration
        );
    }

    public void buriedPointShareOfTargetedLearning(Context context, boolean isCollect) {
        BuriedPointEvent.get().onListenEverydayDetailPageOfTranspondButton(
                context,
                isCollect,
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                mId, mTitle
        );
    }

    private long mEnterTime = 0;
    private int mOldTargetedLearningViewDurType = -1;
    private long mViewDurOfDialogue = 0;
    private long mViewDurOfGrammar = 0;
    private long mViewDurOfComments = 0;

    /**
     * 留存时长
     *
     * @param type 0：Dialogue，1：Grammar，2：Comments
     */
    public void updateBuriedPointTargetedLearningViewDur(int type) {
        if (mEnterTime == 0) {
            mEnterTime = System.currentTimeMillis();
        }
        if (mOldTargetedLearningViewDurType == -1) {
            mOldTargetedLearningViewDurType = type;
            return;
        }
        long viewDur = System.currentTimeMillis() - mEnterTime;
        switch (mOldTargetedLearningViewDurType) {
            case 0:
                mViewDurOfDialogue += viewDur;
                break;
            case 1:
                mViewDurOfGrammar += viewDur;
                break;
            case 2:
                mViewDurOfComments += viewDur;
                break;
        }
        mOldTargetedLearningViewDurType = type;
    }

    public void buriedPointDialogue() {
        if (mUserModel == null) {
            return;
        }
        BuriedPointEvent.get().onVideoGrammarCourseDetailPageOfDialoguePage(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                !mUserModel.isLoginResult(),
                mUserModel.isVip(),
                mId, mTitle,
                mViewDurOfDialogue
        );
    }

    public void buriedPointGrammar() {
        if (mUserModel == null) {
            return;
        }
        BuriedPointEvent.get().onVideoGrammarCourseDetailPageOfGrammarPage(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                !mUserModel.isLoginResult(),
                mUserModel.isVip(),
                mId, mTitle,
                mViewDurOfGrammar
        );
    }

    public void buriedPointComments() {
        if (mUserModel == null) {
            return;
        }
        BuriedPointEvent.get().onVideoGrammarCourseDetailPageOfForumPage(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName(getResources()),
                !mUserModel.isLoginResult(),
                mUserModel.isVip(),
                mId, mTitle,
                mViewDurOfComments
        );
    }
}
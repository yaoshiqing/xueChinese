package com.gjjy.usercenterlib.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.adapter.MsgCenterAdapter;
import com.gjjy.usercenterlib.adapter.MsgListAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.usercenterlib.mvp.view.MsgListView;
import com.gjjy.basiclib.api.entity.MsgListContentEntity;
import com.gjjy.basiclib.api.entity.MsgListOfInteractiveChildEntity;
import com.gjjy.basiclib.mvp.model.ReqMessageModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class MsgListPresenter extends MvpPresenter<MsgListView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqMessageModel mReqMsgModel;

    private int mPage = 1;
    private String mMsgType = null;

    public MsgListPresenter(@NonNull MsgListView view) {
        super(view);
    }

    public void initIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        mMsgType = intent.getStringExtra(Constant.TYPE);
    }

    public String getUserName() {
        return mUserModel.getUserName(getResources());
    }

    public int getMsgCenterType() {
        switch (mMsgType) {
            case "2":
                return MsgCenterAdapter.Type.VIP;
            case "3":
                return MsgCenterAdapter.Type.MESSAGE;
            default:
                return MsgCenterAdapter.Type.SYSTEM;
        }
    }

    public void getMsgList(int page) {
        if (TextUtils.isEmpty(mMsgType)) {
            return;
        }
        boolean isPaging = page > 1;
        viewCall(v -> v.onCallShowLoadingDialog(true));
        //互动消息
        if (getMsgCenterType() == MsgCenterAdapter.Type.MESSAGE) {
            getMsgListOfInteractive(page, isPaging);
            return;
        }
        //常规消息
        mReqMsgModel.reqMsgList(mMsgType, page, 10, entity -> {
            if (entity == null) {
                mPage = page - 1;
                onCallMsgList(new ArrayList<>(), isPaging);
                return;
            }

            List<MsgListContentEntity> list = entity.getData();
            List<MsgListAdapter.ItemData> retList = new ArrayList<>();
            if (list == null) {
                onCallMsgList(retList, isPaging);
                return;
            }
            for (MsgListContentEntity data : list) {
                MsgListAdapter.ItemData item = new MsgListAdapter.ItemData();
                item.setId(data.getId());
                item.setMsgType(data.getMsgType());
                item.setTitle(data.getTitle());
                item.setContent(data.getSummary());
//                item.setUrl( Config.URL_MSG_DETAILS + data.getId() );
                item.setNews(data.getStatus() == 0);
                item.setDate(data.getCreateTime() * 1000L);
                int type;
                switch (data.getSendKey()) {
                    case "remind":       //提醒
                        type = MsgListAdapter.Type.REMIND;
                        break;
                    case "feedback":     //反馈
                        type = MsgListAdapter.Type.FEEDBACK;
                        break;
                    default:             //其他消息
                        type = MsgListAdapter.Type.OTHER;
                        break;
                }
                item.setType(type);
                retList.add(item);
            }
            onCallMsgList(retList, isPaging);
        });
    }

    /**
     * 互动消息
     *
     * @param page     第几页
     * @param isPaging 是否为第一页（分页）
     */
    private void getMsgListOfInteractive(int page, boolean isPaging) {
        mReqMsgModel.reqMsgOfInteractiveList(page, entity -> {
            if (entity == null) {
                mPage = page - 1;
                onCallMsgList(new ArrayList<>(), isPaging);
                return;
            }
            List<MsgListOfInteractiveChildEntity> list = entity.getData();
            List<MsgListAdapter.ItemData> retList = new ArrayList<>();
            if (list == null) {
                onCallMsgList(retList, isPaging);
                return;
            }
            for (MsgListOfInteractiveChildEntity data : list) {
                MsgListAdapter.ItemData item = new MsgListAdapter.ItemData();
                item.setId(data.getDiscoverArticleId());
                item.setTitle(data.getTitle());
                item.setContent(data.getInteractTalkContent());
                item.setMyContent(data.getTalkContent());
                item.setInteractType(data.getInteractType());
                item.setAvatarUrl(data.getInteractAvatarUrl());
                item.setImgUrl(data.getImgUrl());
                item.setNickname(data.getInteractNickname());
                item.setVip(data.isVip());
                item.setTalkId(data.getTalkId());
                item.setVideoId(data.getVideoId());
                item.setDate(data.getCreateTime() * 1000L);
                retList.add(item);
            }
            onCallMsgList(retList, isPaging);
        });
    }

    private void onCallMsgList(List<MsgListAdapter.ItemData> list, boolean isPaging) {
        viewCall(v -> {
            v.onCallMsgList(list, isPaging);
            v.onCallShowLoadingDialog(false);
        });
    }

    public void refreshMsgList() {
        mPage = 1;
        getMsgList(mPage);
    }


    public void nextMsgList() {
        getMsgList(++mPage);
    }

    public void unreadMsg(int id, int position) {
        mReqMsgModel.reqMsgEditStatus(id, result -> {
            if (!result) {
                return;
            }
            viewCall(v -> v.onCallUnread(position));
        });
    }

    public void unreadMsgAll() {
        mReqMsgModel.reqMsgEditStatusAll(mMsgType, result -> {
            if (!result) {
                return;
            }
            viewCall(MsgListView::onCallUnreadAll);
        });
    }
}

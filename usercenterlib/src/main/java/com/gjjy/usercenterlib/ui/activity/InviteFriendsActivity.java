package com.gjjy.usercenterlib.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.ReqConfigModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.ybear.mvp.annotations.Model;
import com.ybear.sharesdk.FacebookShare;

/**
 * 邀请好友页面
 */
@Route(path = "/userCenter/inviteFriendsActivity")
public class InviteFriendsActivity extends BaseActivity {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqConfigModel mReqConfigModel;

    private Toolbar tbToolbar;
    private TextView tvFacebookBtn;
    private TextView tvMessengerBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tbToolbar = findViewById(R.id.invite_friends_tb_toolbar);
        tvFacebookBtn = findViewById(R.id.invite_friends_tv_facebook_btn);
        tvMessengerBtn = findViewById(R.id.invite_friends_tv_messenger_btn);
    }

    private void initData() {
        setStatusBarHeight(R.id.toolbar_height_space);
        tbToolbar.setBackBtnOfImg(R.drawable.ic_white_back);

        tvFacebookBtn.setVisibility(mReqConfigModel.isEnableInviteFriendsFacebook() ? View.VISIBLE : View.GONE);
        tvMessengerBtn.setVisibility(mReqConfigModel.isEnableInviteFriendsMessenger() ? View.VISIBLE : View.GONE);
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
        tvFacebookBtn.setOnClickListener(v -> doFacebook());
        tvMessengerBtn.setOnClickListener(v -> doMessenger());
    }

    private void doFacebook() {
        FacebookShare fbShare = FacebookShare.get();
        ShareLinkContent content = fbShare.createShareLink(getShareUrl(), getResources().getString(R.string.StringInviteFriendsQuote));
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
                BuriedPointEvent.get().onInviteFriendsPageOfFacebook(
                        getContext(),
                        mUserModel.getUid(),
                        mUserModel.getUserName(getResources()),
                        result
                );

                if (result || getContext() == null) {
                    return;
                }
                post(() -> showToast(getString(R.string.stringError)));
            }
        });
    }

    private void doMessenger() {
        boolean result = false;
        MessageDialog md = new MessageDialog(this);
        try {
            ShareLinkContent slc = FacebookShare.get().createShareLink(getShareUrl());
            if (result = md.canShow(slc)) {
                md.show(slc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!result) post(() -> showToast(getString(R.string.stringError)));

        BuriedPointEvent.get().onInviteFriendsPageOfMessenger(
                this,
                mUserModel.getUid(),
                mUserModel.getUserName(getResources()),
                result
        );
    }

    private String getShareUrl() {
        return String.format(Config.mInvitationCodeUrl, mUserModel.getInvitationCode());
    }
}

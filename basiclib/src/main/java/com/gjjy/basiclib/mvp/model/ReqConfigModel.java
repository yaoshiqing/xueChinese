package com.gjjy.basiclib.mvp.model;

import com.gjjy.basiclib.api.apiConfig.IsAbleToShareApi;
import com.gjjy.basiclib.api.entity.AbleToShareEntity;

public class ReqConfigModel extends BasicGlobalReqModel {
    private boolean enableShare;
    private boolean enableBuyVipEval;
    private boolean enableInviteFriendsFacebook;
    private boolean enableInviteFriendsMessenger;
    private boolean enableMessageCenter;

    /**
     * 能否分享
     */
    public void reqAbleToShare() {
        IsAbleToShareApi api = new IsAbleToShareApi();
        api.setCallbackString((s, isResponse) -> {
            AbleToShareEntity data = toReqEntity( s, AbleToShareEntity.class );
            data = data != null ? data : new AbleToShareEntity();
            enableShare = data.isAbleToShare();
            enableBuyVipEval = !data.isAbleToHideCustomerReviews();
            enableInviteFriendsFacebook = !data.isAbleToHideFacebookShare();
            enableInviteFriendsMessenger = !data.isAbleToHideMessengerShare();
            enableMessageCenter = !data.isAbleToHideMessageCenter();
        });
        reqApi( api );
    }

    public boolean isEnableShare() { return enableShare; }

    public boolean isEnableBuyVipEval() { return enableBuyVipEval; }

    public boolean isEnableInviteFriendsFacebook() { return enableInviteFriendsFacebook; }

    public boolean isEnableInviteFriendsMessenger() { return enableInviteFriendsMessenger; }

    public boolean isEnableMessageCenter() { return enableMessageCenter; }
}

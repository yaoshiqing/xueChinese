package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

/**
 * 获取商品列表实体类
 */
public class AbleToShareEntity extends BaseReqEntity {
    private boolean ableToShare = false;                  //分享按钮的展示和隐藏
    private boolean ableToHideCustomerReviews = true;     //分享购买会员评价的展示和隐藏
    private boolean ableToHideFacebookShare = false;      //好友邀请的Facebook的展示和隐藏
    private boolean ableToHideMessengerShare = true;      //好友邀请的Messenger的展示和隐藏
    private boolean ableToHideMessageCenter = true;       //消息中心的展示和隐藏

    @NonNull
    @Override
    public String toString() {
        return "AbleToShareEntity{" +
                "ableToShare=" + ableToShare +
                ", ableToHideCustomerReviews=" + ableToHideCustomerReviews +
                ", ableToHideFacebookShare=" + ableToHideFacebookShare +
                ", ableToHideMessengerShare=" + ableToHideMessengerShare +
                ", ableToHideMessageCenter=" + ableToHideMessageCenter +
                '}';
    }

    public boolean isAbleToShare() { return ableToShare; }
    public void setAbleToShare(boolean enable) { ableToShare = enable; }

    public boolean isAbleToHideCustomerReviews() { return ableToHideCustomerReviews; }
    public void setAbleToHideCustomerReviews(boolean enable) { ableToHideCustomerReviews = enable; }

    public boolean isAbleToHideFacebookShare() { return ableToHideFacebookShare; }
    public void setAbleToHideFacebookShare(boolean enable) { ableToHideFacebookShare = enable; }

    public boolean isAbleToHideMessengerShare() { return ableToHideMessengerShare; }
    public void setAbleToHideMessengerShare(boolean enable) { ableToHideMessengerShare = enable; }

    public boolean isAbleToHideMessageCenter() { return ableToHideMessageCenter; }

    public void setAbleToHideMessageCenter(boolean enable) { ableToHideMessageCenter = enable; }
}

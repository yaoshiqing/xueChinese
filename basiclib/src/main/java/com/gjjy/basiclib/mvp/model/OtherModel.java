package com.gjjy.basiclib.mvp.model;

import android.app.Application;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.BuySubEntity;
import com.gjjy.basiclib.api.entity.CheckAnnouncementEntity;
import com.gjjy.basiclib.api.entity.CheckUpdateEntity;
import com.gjjy.basiclib.api.entity.GoogleBuySubEntity;
import com.gjjy.basiclib.api.entity.OrderEntity;
import com.gjjy.basiclib.api.entity.UploadImgEntity;
import com.gjjy.osslib.OSS;
import com.gjjy.osslib.OnOSSCompletedListener;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OtherModel extends MvpModel {
    @Model
    private ReqOtherModel mReqOther;

    public void refreshUid(String uid, String token) {
        mReqOther.setUid(uid, token);
    }

    /**
     * 初始化Oss信息
     *
     * @param app {@link Application}
     */
    private void initOssInfo(Application app) {
        OSS.get().setEnableLog(LogUtil.isDebug());
        mReqOther.reqOssInfo(data -> OSS.get().init(
                app,
                data.getAccessKeyId(),
                data.getAccessKeySecret(),
                data.getEndpoint(),
                data.getBucket(),
                data.getUrl()
        ));
        LogUtil.e("OtherModel -> initOssInfo");
    }

    public void activityCheckInitOSS(Application app) {
        if (OSS.get().isInit()) return;
        initOssInfo(app);
    }

    /**
     * 记录用户已学关键词
     *
     * @param words 关键词数组，格式：[“我的”,”你的”,”他的”]
     * @param call  处理结果
     */
    public void uploadRecordKeyword(List<String> words, Consumer<Boolean> call) {
        mReqOther.reqRecordKeyword(words, call);
    }

    private final List<String> urls = new ArrayList<>();

    public void feedback(String content, String email, List<File> imgFiles, Comparable<Boolean> call) {

        //没有图片时
        if (imgFiles.size() == 0) {
            mReqOther.reqFeedback(content, email, urls, result1 -> {
                if (call != null) {
                    call.compareTo(result1);
                }
            });
            return;
        }

        urls.clear();
        OSS.get().addOnOSSCompletedListener(new OnOSSCompletedListener() {
            @Override
            public void onSuccess(String url, String objectName, int index, int count) {
                urls.add(objectName);
                if (index == count - 1) {
                    mReqOther.reqFeedback(content, email, urls, result1 -> {
                        if (call != null) {
                            call.compareTo(result1);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String url, String objectName, int index, int count) {
                UploadImgEntity data = new UploadImgEntity();
                data.setSave(objectName);
                data.setShow(url);
            }
        }).uploadFeedback(imgFiles.toArray(new File[0]));
    }

    public void updateCheck(Consumer<CheckUpdateEntity> call) {
        mReqOther.reqUpdateCheck(call);
    }

    public void popupNotice(Consumer<CheckAnnouncementEntity> call) {
        mReqOther.reqPopupNotice(call);
    }

    public void bindPushId(Consumer<Boolean> call) {
        mReqOther.reqBindPushId(call);
    }

    public void reqCreateOrder(int goodsId, Consumer<OrderEntity> call) {
        mReqOther.reqCreateOrder(goodsId, call);
    }

    public void sendNewSub(String orderId, String purToken, Consumer<Boolean> call) {
        mReqOther.reqNewSub(orderId, purToken, call);
    }

    public void queryDiscountBuySubList(Consumer<List<BuySubEntity>> call) {
        //特惠订阅
        mReqOther.reqBuySubList(3, call);
    }

    public void queryNormalBuySubList(Consumer<List<BuySubEntity>> call) {
        //正常订阅
        mReqOther.reqBuySubList(1, call);
    }

    // google 支付 商品列表
    public void queryGoogleBuySubList(Consumer<List<GoogleBuySubEntity>> call) {
        mReqOther.reqGoogleBuySubList(call);
    }
}

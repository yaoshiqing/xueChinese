package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.alibaba.fastjson.JSONObject;
import com.gjjy.basiclib.api.apiOther.BuyListApi;
import com.gjjy.basiclib.api.apiOther.CreateOrderApi;
import com.gjjy.basiclib.api.apiOther.FeedbackApi;
import com.gjjy.basiclib.api.apiOther.GoogleBuyListApi;
import com.gjjy.basiclib.api.apiOther.NewSubApi;
import com.gjjy.basiclib.api.apiOther.RecordKeywordApi;
import com.gjjy.basiclib.api.apiOther.UploadTokenApi;
import com.gjjy.basiclib.api.apiPopupNotice.PopupNoticeApi;
import com.gjjy.basiclib.api.apiStudyDiscover.GetVodPlayAuthApi;
import com.gjjy.basiclib.api.apiVersion.UpdateCheckApi;
import com.gjjy.basiclib.api.entity.BuySubEntity;
import com.gjjy.basiclib.api.entity.CheckAnnouncementEntity;
import com.gjjy.basiclib.api.entity.CheckUpdateEntity;
import com.gjjy.basiclib.api.entity.GoogleBuySubEntity;
import com.gjjy.basiclib.api.entity.OSSInfoEntity;
import com.gjjy.basiclib.api.entity.OrderEntity;
import com.gjjy.basiclib.entity.GetVodPlayAuthEntity;

import java.util.List;

/**
 * 其他
 */
public class ReqOtherModel extends BasicGlobalReqModel {

    /**
     * 记录用户已学关键词
     *
     * @param words 关键词数组，格式：[“我的”,”你的”,”他的”]
     * @param call  请求结果
     */
    public void reqRecordKeyword(String[] words, Consumer<Boolean> call) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < words.length; i++) {
            sb.append("\"").append(words[i]).append("\"");
            if (i < words.length - 1) sb.append(",");
        }
        sb.append("]");

        RecordKeywordApi api = new RecordKeywordApi();
        api.addParam("words", sb.toString());
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * 记录用户已学关键词
     *
     * @param words 关键词数组，格式：[“我的”,”你的”,”他的”]
     * @param call  请求结果
     */
    public void reqRecordKeyword(List<String> words, Consumer<Boolean> call) {
        reqRecordKeyword(words.toArray(new String[0]), call);
    }

    /**
     * 新增用户反馈信息
     *
     * @param call 请求结果
     */
    public void reqFeedback(String s, String email, List<String> imgUrls, Consumer<Boolean> call) {
        FeedbackApi api = new FeedbackApi();
        api.addParam("content", s);
        api.addParam("email", email);
        api.addParam("img_url", JSONObject.toJSONString(imgUrls));
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * 版本更新检查
     *
     * @param call 处理结果
     */
    public void reqUpdateCheck(Consumer<CheckUpdateEntity> call) {
        UpdateCheckApi api = new UpdateCheckApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            CheckUpdateEntity data = toReqEntity(s, CheckUpdateEntity.class);
            call.accept(data == null ? new CheckUpdateEntity() : data);
        });
        reqApi(api);
    }

    /**
     * 公告检查
     *
     * @param call 处理结果
     */
    public void reqPopupNotice(Consumer<CheckAnnouncementEntity> call) {
        PopupNoticeApi api = new PopupNoticeApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            CheckAnnouncementEntity data = toReqEntity(s, CheckAnnouncementEntity.class);
            call.accept(data == null ? new CheckAnnouncementEntity() : data);
        });
        reqApi(api);
    }

    /**
     * 极光 - 推送绑定别名与标签
     *
     * @param call 处理结果
     */
    public void reqBindPushId(Consumer<Boolean> call) {
//        if( isEmptyOfUid() ) return;
//        JPushBindApi api = new JPushBindApi();
//        api.addParam( "registration_id", Push.get().getRegistrationID() );
//        callbackSuccess( api, call );
//        reqApi( api );
    }

    /**
     * 开通会员
     *
     * @param call 处理结果
     */
    public void reqCreateOrder(int goodsId, Consumer<OrderEntity> call) {
        CreateOrderApi api = new CreateOrderApi();
        api.addParam("goods_id", goodsId);
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            OrderEntity data = toReqEntity(s, OrderEntity.class);
            call.accept(data == null ? new OrderEntity() : data);
        });
        reqApi(api);
    }

    /**
     * 开通会员
     *
     * @param call 处理结果
     */
    public void reqNewSub(String orderId, String purToken, Consumer<Boolean> call) {
        NewSubApi api = new NewSubApi();
        api.addParam("order_id", orderId);
        api.addParam("purchase_token", purToken);
        callbackSuccess(api, call);
        reqApi(api);
    }

    /**
     * 获取商品列表
     *
     * @param call 处理结果
     */
    public void reqBuySubList(int payType, Consumer<List<BuySubEntity>> call) {
        BuyListApi api = new BuyListApi();
        api.addParam("pay_type", payType);
        api.setCallbackString((s, isResponse) -> {
            if (call != null) call.accept(toReqEntityOfList(s, BuySubEntity.class));
        });
        reqApi(api);
    }

    /**
     * 根据资源id获取播放授权接口
     *
     * @param call 处理结果
     */
    public void getVodPlayAuth(String mediaId, Consumer<GetVodPlayAuthEntity> call) {
        GetVodPlayAuthApi api = new GetVodPlayAuthApi();
        api.addParam("mediaId", mediaId);
        api.setCallbackString((s, isResponse) -> {
            GetVodPlayAuthEntity data = toReqEntity(s, GetVodPlayAuthEntity.class);
            if (call != null) {
                call.accept(data == null ? new GetVodPlayAuthEntity() : data);
            }
        });
        reqApi(api);
    }

    /**
     * 商品列表（google 购买）
     *
     * @param call
     */
    public void reqGoogleBuySubList(Consumer<List<GoogleBuySubEntity>> call) {
        GoogleBuyListApi api = new GoogleBuyListApi();
        api.setCallbackString((s, isResponse) -> {
            if (call != null) {
                call.accept(toReqEntityOfList(s, GoogleBuySubEntity.class));
            }
        });
        reqApi(api);
    }

    /**
     * STS-token文件上传
     * @param call 请求结果
     */
    public void reqOSSUploadToken(Consumer<OSSInfoEntity> call) {
        UploadTokenApi api = new UploadTokenApi();
        api.setCallbackString((s, isResponse) -> {
            if (call == null) return;
            OSSInfoEntity data = toReqEntity(s, OSSInfoEntity.class);
            call.accept(data == null ? new OSSInfoEntity() : data);
        });
        reqApi(api);
    }
}

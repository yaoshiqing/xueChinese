package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiMessage.MsgEditStatusAllApi;
import com.gjjy.basiclib.api.apiMessage.MsgEditStatusApi;
import com.gjjy.basiclib.api.apiMessage.MsgListApi;
import com.gjjy.basiclib.api.apiMessage.MsgTypeCountApi;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.api.entity.MsgListEntity;
import com.gjjy.basiclib.api.entity.MsgListOfInteractiveEntity;
import com.gjjy.basiclib.api.entity.MsgTypeCountEntity;
import com.ybear.mvp.annotations.Model;
import com.ybear.ybnetworkutil.request.Request;

import java.util.List;

/**
 * 用户消息
 */
public class ReqMessageModel extends BasicGlobalReqModel{
    @Model
    private UserModel mUserModel;

    @Override
    public void reqApi(Request api) {
        setUid( mUserModel.getUid(), mUserModel.getToken() );
        super.reqApi(api);
    }

    /**
     * 获取消息类型未读总数
     * @param call      处理结果
     */
    public void reqMsgTypeCount(Consumer<List<MsgTypeCountEntity>> call) {
        MsgTypeCountApi api = new MsgTypeCountApi();
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            call.accept( toReqEntityOfList( s, MsgTypeCountEntity.class ) );
        });
        reqApi( api );
    }

//    /**
//     * 获取消息详情
//     * @param id            文章id
//     * @param call          处理结果
//     */
//    public void reqMsgDetailById(int id, Consumer<MsgListContentEntity> call) {
//        MsgDetailByIdApi api = new MsgDetailByIdApi();
//        api.addParam( "jpush_id", id );
//        api.setCallbackString((s, isResponse) -> {
//            if( call == null ) return;
//            MsgListContentEntity data = toReqEntityNotBase( s, MsgListContentEntity.class );
//            call.accept( data == null ? new MsgListContentEntity() : data );
//        });
//        reqApi( api );
//    }

    /**
     * 获取消息列表
     * @param msgType       消息类型：1系统通知、2会员消息
     * @param page          第几页
     * @param pageSize      每页显示记录数
     * @param call          处理结果
     */
    public void reqMsgList(String msgType, int page, int pageSize, Consumer<MsgListEntity> call) {
        MsgListApi api = new MsgListApi();
        api.addParam( "msg_type", msgType );        //消息类型：1系统通知、2会员消息
        api.addParam( "page", page );               //第几页
        api.addParam( "pagesize", pageSize );       //每页显示记录数
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            MsgListEntity data = toReqEntityNotBase( s, MsgListEntity.class );
            call.accept( data == null ? new MsgListEntity() : data );
        });
        reqApi( api );
    }

    /**
     获取互动消息列表
     @param page          第几页
     @param call          处理结果
     */
    public void reqMsgOfInteractiveList(int page, Consumer<MsgListOfInteractiveEntity> call) {
        MsgListApi api = new MsgListApi();
        api.addParam( "msg_type", 3 );          //消息类型：1系统通知、2会员消息
        api.addParam( "page", page );               //第几页
        api.addParam( "pagesize", 20 );         //每页显示记录数
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            MsgListOfInteractiveEntity data = toReqEntityNotBase( s, MsgListOfInteractiveEntity.class );
            call.accept( data == null ? new MsgListOfInteractiveEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 更改消息为已读
     * @param id        消息表自增id
     * @param call      处理结果
     */
    public void reqMsgEditStatus(int id, Consumer<Boolean> call) {
        MsgEditStatusApi api = new MsgEditStatusApi();
        api.addParam( "id", id );        //消息表自增id
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            BaseReqEntity data = toReqEntityOfBase( s );
            call.accept( data != null && data.isSuccess() );
        });
        reqApi( api );
    }

    /**
     * 更改所有消息为已读
     * @param msgType   消息类型：1系统通知、2会员通知
     * @param call      处理结果
     */
    public void reqMsgEditStatusAll(String msgType, Consumer<Boolean> call) {
        MsgEditStatusAllApi api = new MsgEditStatusAllApi();
        api.addParam( "msg_type", msgType );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            BaseReqEntity data = toReqEntityOfBase( s );
            call.accept( data != null && data.isSuccess() );
        });
        reqApi( api );
    }
}

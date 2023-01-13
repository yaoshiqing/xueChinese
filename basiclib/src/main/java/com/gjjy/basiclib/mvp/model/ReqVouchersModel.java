package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiVouchers.BuyVipTicketApi;
import com.gjjy.basiclib.api.apiVouchers.ReqEditIsReadApi;
import com.gjjy.basiclib.api.apiVouchers.ReqVouchersListApi;
import com.gjjy.basiclib.api.apiVouchers.ReqVouchersPopupApi;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.api.entity.VouchersListEntity;
import com.gjjy.basiclib.api.entity.VouchersPopupListEntity;

/**
 * 领券中心
 */
public class ReqVouchersModel extends BasicGlobalReqModel{

    /**
     获取领券中心列表
     @param status          状态：1待使用、2使用中、3已过期
     @param page            第几页
     @param call            处理结果
     */
    public void reqVouchersList(int status, int page, Consumer<VouchersListEntity> call) {
        ReqVouchersListApi api = new ReqVouchersListApi();
        api.addParam( "status", status );
        api.addParam( "page", page );
        api.addParam( "pagesize", 10 );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            VouchersListEntity data = toReqEntityNotBase( s, VouchersListEntity.class );
            call.accept( data == null ? new VouchersListEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 获取弹窗列表
     * @param call          处理结果
     */
    public void reqVouchersPopupList(Consumer<VouchersPopupListEntity> call) {
        ReqVouchersPopupApi api = new ReqVouchersPopupApi();
        api.addParam( "page", 1 );
        api.addParam( "pagesize", 10 );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            VouchersPopupListEntity data = toReqEntityNotBase( s, VouchersPopupListEntity.class );
            call.accept( data == null ? new VouchersPopupListEntity() : data );
        });
        reqApi( api );
    }

    /**
     * 标记弹窗为已读
     * @param id            用户弹窗表自增ID
     * @param call          处理结果
     */
    public void reqReqEditIsRead(int id, Consumer<Boolean> call) {
        ReqEditIsReadApi api = new ReqEditIsReadApi();
        api.addParam( "id", id );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     立即使用券
     * @param id            领券中心表自增ID
     * @param call          处理结果。1111：你已经是正式会员。1112：你已经是试用会员
     */
    public void reqBuyVipTicket(int id, Consumer<Integer> call) {
        BuyVipTicketApi api = new BuyVipTicketApi();
        api.addParam( "id", id );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            BaseReqEntity data = toReqEntityOfBase( s );
            call.accept( data == null ? 0 : data.getErrorNumber() );
        });
        reqApi( api );
    }
}

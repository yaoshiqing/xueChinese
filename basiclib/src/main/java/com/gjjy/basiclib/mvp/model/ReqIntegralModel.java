package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiUserPoint.ReqLightningExchangeApi;
import com.gjjy.basiclib.api.entity.IntegralDataEntity;
import com.gjjy.basiclib.api.entity.MonthTotalEntity;
import com.ybear.ybutils.utils.ObjUtils;
import com.gjjy.basiclib.api.apiUserPoint.ReqListsApi;
import com.gjjy.basiclib.api.apiUserPoint.ReqMonthTotalApi;
import com.gjjy.basiclib.api.apiUserPoint.ReqTotalApi;

/**
 * 积分
 */
public class ReqIntegralModel extends BasicGlobalReqModel{
    private int mTotal = 0;

    /**
     获取指定月份积分统计
     @param year            年份
     @param month           月份
     @param call            请求结果
     */
    public void reqMonthTotal(int year, int month, Consumer<MonthTotalEntity> call) {
        ReqMonthTotalApi api = new ReqMonthTotalApi();
        api.addParam( "year", year );
        api.addParam( "month", month );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            MonthTotalEntity data = toReqEntity( s, MonthTotalEntity.class );
            call.accept( data == null ? new MonthTotalEntity() : data );
        });
        reqApi( api );
    }

    /**
     获取有效积分总数
     @param call            请求结果
     */
    public void reqTotal(boolean isCache, Consumer<Integer> call) {
        if( isCache && mTotal != 0 ) {
            if( call != null ) call.accept( mTotal );
            return;
        }
        ReqTotalApi api = new ReqTotalApi();
        api.setCallbackString((s, isResponse) -> {
            mTotal = ObjUtils.parseInt( toReqEntityOfValue( s, "total" ) );
            if( call == null ) return;
            call.accept( mTotal );
        });
        reqApi( api );
    }

    /**
     积分兑换闪电
     @param lightningCount      兑换的闪电数量
     @param call                请求结果
     */
    public void reqLightningExchange(int lightningCount, Consumer<Integer> call) {
        ReqLightningExchangeApi api = new ReqLightningExchangeApi();
        api.addParam( "num", lightningCount );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            call.accept( ObjUtils.parseInt( toReqEntityOfValue( s, "point" ) ) );
        });
        reqApi( api );
    }

    /**
     获取积分记录列表
     @param page            分页
     @param type            类型。积分类型，1获取，2消耗，默认为全部
     @param call            请求结果
     */
    public void reqIntegralList(int page, int type, Consumer<IntegralDataEntity> call) {
        ReqListsApi api = new ReqListsApi();
        api.addParam( "page", page );
        api.addParam( "pagesize", 20 );
        api.addParam( "type", type );
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            IntegralDataEntity data = toReqEntityNotBase( s, IntegralDataEntity.class );
            call.accept( data == null ? new IntegralDataEntity() : data );
        });
        reqApi( api );
    }
}

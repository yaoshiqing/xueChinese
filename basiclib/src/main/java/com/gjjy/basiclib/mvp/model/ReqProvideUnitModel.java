package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiProvideUnit.ReqProvideUnitDetailApi;
import com.gjjy.basiclib.api.apiProvideUnit.ReqProvideUnitOpenLightningApi;
import com.gjjy.basiclib.api.entity.StatusEntity;

/**
 * 介绍模块
 */
public class ReqProvideUnitModel extends BasicGlobalReqModel{

    /**
     * 获取介绍模块详情
     * @param call          请求结果
     */
    public void reqProvideUnitDetail(Consumer<StatusEntity> call) {
        ReqProvideUnitDetailApi api = new ReqProvideUnitDetailApi();
        api.setCallbackString((s, isResponse) -> {
            if( call == null ) return;
            StatusEntity entity = toReqEntity( s, StatusEntity.class );
            call.accept( entity == null ? new StatusEntity() : entity );
        });
        reqApi( api );
    }

    /**
     * 领取介绍模块闪电
     * @param call          请求结果
     */
    public void reqProvideUnitOpenLightning(Consumer<Boolean> call) {
        ReqProvideUnitOpenLightningApi api = new ReqProvideUnitOpenLightningApi();
        callbackSuccess( api, call );
        reqApi( api );
    }
}

package com.gjjy.basiclib.mvp.model;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiRemind.RemindDetailApi;
import com.gjjy.basiclib.api.apiRemind.RemindEditApi;
import com.gjjy.basiclib.api.entity.RemindDetailEntity;

/**
 * 学习提醒
 */
public class ReqRemindModel extends BasicGlobalReqModel{
    /**
     * 请求编辑学习提醒
     * @param entity    编辑数据源
     * @param call      处理结果
     */
    public void reqRemindEdit(@Nullable RemindDetailEntity entity, Consumer<Boolean> call) {
        if( entity == null ) return;
        RemindEditApi api = new RemindEditApi();
        api.addParam( "remind_switch", entity.isRemindSwitch() ? 1 : 0 );
        api.addParam( "remind_Sun", entity.isRemindSun() ? 1 : 0 );
        api.addParam( "remind_Mon", entity.isRemindMon() ? 1 : 0 );
        api.addParam( "remind_Tue", entity.isRemindTue() ? 1 : 0 );
        api.addParam( "remind_Wed", entity.isRemindWed() ? 1 : 0 );
        api.addParam( "remind_Thu", entity.isRemindThu() ? 1 : 0 );
        api.addParam( "remind_Fri", entity.isRemindFri() ? 1 : 0 );
        api.addParam( "remind_Sat", entity.isRemindSat() ? 1 : 0 );
        api.addParam( "remind_time", entity.getRemindTime() );
        callbackSuccess( api, call );
        reqApi( api );
    }

    /**
     * 请求学习提醒详细信息
     * @param call      处理结果
     */
    public void reqRemindDetail(Consumer<RemindDetailEntity> call) {
        RemindDetailApi api = new RemindDetailApi();
        api.setCallbackString((s, isResponse) -> {
            RemindDetailEntity data = toReqEntity( s, RemindDetailEntity.class );
            if( call != null ) call.accept( data == null ? new RemindDetailEntity() : data );
        });
        reqApi( api );
    }
}

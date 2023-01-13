package com.gjjy.basiclib.mvp.model;

import androidx.annotation.IntRange;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.RemindDetailEntity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.time.DateTime;

public class RemindModel extends MvpModel {
    @Model
    private ReqRemindModel mReqRemind;

    private volatile RemindDetailEntity mData;

    public void refreshUid(String uid, String token) {
        mReqRemind.setUid( uid, token );
    }

    public RemindDetailEntity getData() {
        return mData == null ? mData = new RemindDetailEntity() : mData;
    }

    public void initData() {
        mData = null;
    }

    public void getRemindDetail(boolean isCache, Consumer<RemindDetailEntity> call) {
        if( mData != null && isCache ) {
            if( call != null ) call.accept( mData );
            return;
        }
        mReqRemind.reqRemindDetail(entity -> {
            mData = entity;
            if( call != null ) call.accept( mData );
        });
    }

    /**
     * 获取时间戳
     * @param index     位移下标（0~6表示周日，周一至周六）
     * @param hour      小时
     * @param minute    分钟
     * @return          时间戳
     */
    private long getTimestamp(@IntRange(from = 0, to = 6) int index, int hour, int minute) {
        return DateTime.parse(String.format(
                "2000-01-0%s %s:%s:00",
                index + 2,
                hour <= 9 ? "0" + hour : hour,
                minute <= 9 ? "0" + minute : minute
        ));
    }

    private static int getTimeDiff() {
        //目前少半小时
        double timeZone = 0;
        try {
            timeZone = Double.parseDouble( DateTime.currentStandardTimeZone() );
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (int)( timeZone * 3600000D );
    }

    /**
     * 获取星期
     * @param index         位移下标（0~6表示周日，周一至周六）
     * @param hour          小时
     * @param minute        分钟
     * @param timeDiff      时差（以北京时区+8为准）
     * @return              周日：1，周一：2，周二：3，周三：4，周四：5，周五：6，周六：7
     */
    public int getWeek(@IntRange(from = 0, to = 6) int index, int hour, int minute, int timeDiff) {
        return DateTime.getDayOfWeek(getTimestamp( index, hour, minute ) - timeDiff );
    }

    /**
     * 获取小时
     * @param hour          小时
     * @param minute        分钟
     * @param timeDiff      时差（以北京时区+8为准）
     * @return              计算时差过后的小时
     */
    public int getHour(int hour, int minute, int timeDiff) {
        return DateTime.getHour( getTimestamp( 0, hour, minute ) - timeDiff );
    }

    /**
     * 获取分钟
     * @param hour          小时
     * @param minute        分钟
     * @param timeDiff      时差（以北京时区+8为准）
     * @return              计算时差过后的分钟
     */
    public int getMinute(int hour, int minute, int timeDiff) {
        return DateTime.getMinute( getTimestamp( 0, hour, minute ) - timeDiff );
    }

    public void editRemind(Consumer<Boolean> call) {
        //保存提醒信息
        mReqRemind.reqRemindEdit(mData, result -> {
//            //推送提醒信息
//            if( result ) {
////                //覆盖新的tag
////                setRemind( mData );
//            }
            if( call != null ) call.accept( result );
        });
    }

//    private void setRemind(RemindDetailEntity entity) {
//
//        List<Push.RemindTag> list = new ArrayList<>();
//        boolean[] weeks = entity.getRemindWeeks();
//        for (int i = 0; i < weeks.length; i++) {
//            if( !weeks[ i ] ) continue;
//
//            int nowHour = entity.getRemindHour();
//            int nowMinute = entity.getRemindMinute();
//            int diff = getTimeDiff();
//            int week = getWeek( i, nowHour, nowMinute, diff );
//
//            Push.RemindTag tag = new Push.RemindTag();
//            tag.setHour( getHour( nowHour, nowMinute, diff ) );
//            tag.setMinute( getMinute( nowHour, nowMinute, diff ) );
//            tag.setWeek( week );
//            list.add( tag );
//        }
//        Push.get().setRemindTag( list );
//    }

//    private void cleanRemind() {
//        Push.get().cleanRemindTag();
//    }
}

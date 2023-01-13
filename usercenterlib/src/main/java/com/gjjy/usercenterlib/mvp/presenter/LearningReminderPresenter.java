package com.gjjy.usercenterlib.mvp.presenter;

import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.api.entity.RemindDetailEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.RemindModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.view.LearningReminderView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.ObjUtils;

public class LearningReminderPresenter extends MvpPresenter<LearningReminderView> {
    @Model
    private UserModel mUserModel;
    private RemindModel mRemindModel;

    public LearningReminderPresenter(@NonNull LearningReminderView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        mRemindModel = mUserModel.getRemindModel();
        reqRemindDetail( true );
        mRemindModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    @Override
    public void onLifeDestroy() {
        if( mUserModel.isLoginResult() ) {
            mRemindModel.editRemind( null );
        }
        super.onLifeDestroy();
    }

    public void reqRemindDetail(boolean isCache) {
        mRemindModel.getRemindDetail(isCache, entity -> viewCall(v -> {
            String time = entity.getRemindTime();

            //启用状态
            v.onCallIsEnableRemind( entity.isRemindSwitch() );
//            v.onCallShowRemind( entity.isRemindSwitch() );
            //时间
            if( !TextUtils.isEmpty( time ) && time.contains( ":" ) ) {
                String[] sp = time.split(":");
                v.onCallTime( ObjUtils.parseInt( sp[ 0 ] ), ObjUtils.parseInt( sp[ 1 ] ) );
            }
            //星期
            v.onCallWeekStatus( entity.getRemindWeeks() );
        }));
    }

    public void setEnableRemind(boolean enable) {
        //1.2.4取消学习提醒登录的操作
//        //未登录
//        if( !mUserModel.isLoginResult() && enable ) {
//            viewCall(v -> {
//                v.onCallIsEnableRemind( false );
//                v.onCallOpenRemindError();
//            });
//            return;
//        }
        //埋点提醒打开状态
        BuriedPointEvent.get().onLearningReminderPageOfLearningReminderSwitch(
                getContext(), enable
        );
        //启用提醒通知
        viewCall( v -> v.onCallIsEnableRemind( enable ) );
//        viewCall( v -> v.onCallShowRemind( enable ) );
        mRemindModel.getData().setRemindSwitch( enable ? 1 : 0 );
    }

    public void setWeek(int index, boolean enable) {
        RemindDetailEntity entity = mRemindModel.getData();
        int status = enable ? 1 : 0;
        switch ( index ) {
            case 0:
                entity.setRemindSun( status );
                break;
            case 1:
                entity.setRemindMon( status );
                break;
            case 2:
                entity.setRemindTue( status );
                break;
            case 3:
                entity.setRemindWed( status );
                break;
            case 4:
                entity.setRemindThu( status );
                break;
            case 5:
                entity.setRemindFri( status );
                break;
            case 6:
                entity.setRemindSat( status );
                break;
        }
    }

    public void setTime(String hour, String minute) {
        mRemindModel.getData().setRemindTime( hour, minute );
    }

    public String getTime() {
        RemindDetailEntity entity = mRemindModel.getData();
        Resources res = getResources();
        return entity == null ? res == null ?
                "20:00" : res.getString( R.string.stringDefaultTime ) :
                entity.getRemindTime();
    }
}

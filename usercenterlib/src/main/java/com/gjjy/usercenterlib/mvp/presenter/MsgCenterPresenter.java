package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.adapter.MsgCenterAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.usercenterlib.mvp.view.MsgCenterView;
import com.gjjy.basiclib.api.entity.MsgTypeCountEntity;
import com.gjjy.basiclib.mvp.model.ReqMessageModel;

import java.util.ArrayList;
import java.util.List;

public class MsgCenterPresenter extends MvpPresenter<MsgCenterView> {
    @Model
    private ReqMessageModel mReqMsgModel;

    public MsgCenterPresenter(@NonNull MsgCenterView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        getMsgList();
    }

    private void getMsgList() {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mReqMsgModel.reqMsgTypeCount(list -> {
            List<MsgCenterAdapter.ItemData> retList = new ArrayList<>();
            for( MsgTypeCountEntity data : list ) {
                //不显示没有消息的列表
                if( data.getAll() == 0 ) continue;
                MsgCenterAdapter.ItemData item = new MsgCenterAdapter.ItemData();
                int msgType = 0;
                switch ( data.getMsgType() ) {
                    case 1:         //系统通知
                        msgType = MsgCenterAdapter.Type.SYSTEM;
                        break;
                    case 2:         //会员
                        msgType = MsgCenterAdapter.Type.VIP;
                        break;
                    case 3:         //消息
                        msgType = MsgCenterAdapter.Type.MESSAGE;
                        break;
                }
                item.setType( msgType );
                item.setEnableRedDot( data.getUnread() > 0 );
                item.setImgUrl( data.getPicUrl() );
                retList.add( item );
            }
            viewCall(v -> {
                v.onCallMsgList( retList );
                v.onCallShowLoadingDialog( false );
            });
        });
    }

    public void startNotifyListActivity(@MsgCenterAdapter.Type int type) {
        String typeName = "";
        switch ( type ) {
            case MsgCenterAdapter.Type.SYSTEM:      //系统消息
                typeName = "系统通知";
                StartUtil.startMsgSystemListActivity( getActivity() );
                break;
            case MsgCenterAdapter.Type.VIP:         //会员
                typeName = "会员通知";
                StartUtil.startMsgVipListActivity( getActivity() );
                break;
            case MsgCenterAdapter.Type.MESSAGE:    //消息
                typeName = "消息通知";
                StartUtil.startMsgInteractiveListActivity( getActivity() );
                break;
        }
        //埋点点击的通知类型
        BuriedPointEvent.get().onNotificationCenterPageOfChoseNotificationType(
                getContext(),
                typeName
        );
    }
}

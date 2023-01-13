package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.VouchersChildEntity;
import com.gjjy.basiclib.api.entity.VouchersListEntity;
import com.gjjy.basiclib.mvp.model.ReqVouchersModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.adapter.VouchersAdapter;
import com.gjjy.usercenterlib.mvp.view.VouchersView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class VouchersPresenter extends MvpPresenter<VouchersView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqVouchersModel mReqVouchersModel;

    private int mPageOfUnused = 0;
    private int mPageOfInUse = 0;
    private int mPageOfExpired = 0;

    public VouchersPresenter(@NonNull VouchersView view) {
        super(view);
    }

    public void queryVouchersList() {
        queryUnused( r1 -> queryInUse( r2 -> queryExpired( null ) ) );
    }

    public void queryDataList(int index) {
        switch( index ) {
            case 0:
                mPageOfUnused = 0;
                queryUnused( null );
                break;
            case 1:
                mPageOfInUse = 0;
                queryInUse( null );
                break;
            case 2:
                mPageOfExpired = 0;
                queryExpired( null );
                break;
        }
    }

    public void queryNextDataList(int index) {
        switch( index ) {
            case 0:
                mPageOfUnused++;
                queryUnused( null );
                break;
            case 1:
                mPageOfInUse++;
                queryInUse( null );
                break;
            case 2:
                mPageOfExpired++;
                queryExpired( null );
                break;
        }
    }

    public void BuyVipTicket(int id, int pos) {
        mReqVouchersModel.reqBuyVipTicket( id, code ->
                viewCall( v -> v.onCallBuyVipTicketResult( code, pos ) )
        );
    }

    private void callVouchersList(VouchersListEntity data, int index, boolean isPaging,
                                  Consumer<Boolean> call) {
        final int finalIndex = index;
        List<VouchersChildEntity> childList = data.getData();
        List<VouchersAdapter.ItemData> retList = new ArrayList<>();
        for( VouchersChildEntity child : childList ) {
            if( child.getType() != 1 ) continue;
            VouchersAdapter.ItemData item = new VouchersAdapter.ItemData();
            item.setId( child.getId() );
            item.setDay( child.getNum() );
            int type = VouchersAdapter.Type.NONE;
            long startTime = 0L;
            long endTime = 0L;
            switch( child.getNowStatus() ) {
                case 1:         //待使用
                    type = VouchersAdapter.Type.UNUSED;
                    startTime = DateTime.parse( child.getCreateTime() );
                    endTime = DateTime.parse( child.getInvalidTime() );
                    break;
                case 2:         //正在使用
                    type = VouchersAdapter.Type.IN_USE;
                    startTime = DateTime.parse( child.getStartTime() );
                    endTime = DateTime.parse( child.getEndTime() );
                    break;
                case 3:         //已过期
                case 5:         //已停用
                    type = VouchersAdapter.Type.EXPIRED;
                    break;
                case 4:         //使用结束
                    type = VouchersAdapter.Type.FREE_EXPIRED;
                    break;
            }
            item.setType( type );
            item.setStartTime( startTime );
            item.setEndTime( endTime );
            retList.add( item );
        }
        viewCall( v -> {
            v.onCallVouchersList( retList, finalIndex, isPaging );
            if( call != null ) call.accept( true );
        } );
    }

    /**
     待使用
     */
    private void queryUnused(Consumer<Boolean> call) {
        if( mPageOfUnused == -1 ) {
            callVouchersList( null, 0, true, call );
            return;
        }else {
            mPageOfUnused++;
        }
        mReqVouchersModel.reqVouchersList(1, mPageOfUnused, data -> {
            callVouchersList(
                    data, 0, mPageOfUnused == -1 || mPageOfUnused > 1, call
            );
            if( mPageOfUnused >= data.getLastPage() ) mPageOfUnused = -1;
        });
    }

    /**
     使用中
     */
    private void queryInUse(Consumer<Boolean> call) {
        if( mPageOfInUse == -1 ) {
            callVouchersList( null, 1, true, call );
            return;
        }else {
            mPageOfInUse++;
        }
        mReqVouchersModel.reqVouchersList(2, mPageOfInUse, data -> {
            callVouchersList(
                    data, 1, mPageOfInUse == -1 || mPageOfInUse > 1, call
            );
            if( mPageOfInUse >= data.getLastPage() ) mPageOfInUse = -1;
        });
    }

    /**
     已过期
     */
    private void queryExpired(Consumer<Boolean> call) {
        if( mPageOfExpired == -1 ) {
            callVouchersList( null, 2, true, call );
            return;
        }else {
            mPageOfExpired++;
        }
        mReqVouchersModel.reqVouchersList(3, mPageOfExpired, data -> {
            callVouchersList(
                    data, 2, mPageOfExpired == -1 || mPageOfExpired > 1, call
            );
            if( mPageOfExpired >= data.getLastPage() ) mPageOfExpired = -1;
        });
    }
}
package com.gjjy.usercenterlib.mvp.presenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.RankingListEntity;
import com.gjjy.basiclib.api.entity.VouchersPopupChildEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.mvp.model.ReqConfigModel;
import com.gjjy.basiclib.mvp.model.ReqIntegralModel;
import com.gjjy.basiclib.mvp.model.ReqVouchersModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.gjjy.usercenterlib.mvp.view.UserCenterView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.toast.ToastManage;

import java.util.ArrayList;
import java.util.List;

public class UserCenterPresenter extends MvpPresenter<UserCenterView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqIntegralModel mReqIntegralModel;
    @Model
    private ReqVouchersModel mReqVouchersModel;
    @Model
    private ReqConfigModel mReqConfigModel;

    private boolean isBindAccount;
    private boolean isHidden = true;
    private boolean isQuerying;
    private int mVouchersPopupIndex = 0;

    public UserCenterPresenter(@NonNull UserCenterView view) {
        super(view);
    }

    public void onVisibleChanged(boolean isVisible) {
        if( isHidden = !isVisible ) return;
        post( this::refreshDetail, 100);
        queryVouchersPopupList();
//        isHidden = true;
    }

//    @Override
//    public void onTriggerHiddenChanged(boolean hidden) {
//        super.onTriggerHiddenChanged(hidden);
//        if( isHidden = hidden ) return;
//        refreshDetail();
////        isHidden = true;
//    }

    private final List<VouchersPopupChildEntity> mVouchersPopupList = new ArrayList<>();
    @Override
    public void onLifeResume() {
        super.onLifeResume();
        if( isHidden ) return;
        refreshDetail();
    }

    public int getVipStatus() { return mUserModel.getVipStatus(); }

    private void queryVouchersPopupList() {
        if( mVouchersPopupList.size() > 0 ) return;
        mReqVouchersModel.reqVouchersPopupList(data -> {
            mVouchersPopupList.clear();
            mVouchersPopupList.addAll( data.getData() );
            if( mVouchersPopupList.size() > 0 ) nextVouchersPopup();
        });
    }

    public void nextVouchersPopup() {
        if( mVouchersPopupIndex >= mVouchersPopupList.size() ) {
            mVouchersPopupIndex = 0;
            mVouchersPopupList.clear();
            queryVouchersPopupList();
            return;
        }
        viewCall( v -> v.onCallVouchersPopup( mVouchersPopupList.get( mVouchersPopupIndex++ ) ) );
    }

    public void BuyVipTicket(int id, @NonNull Consumer<Boolean> call) {
        mReqVouchersModel.reqBuyVipTicket( id, code -> {
            editIsRead( id );
            if( getContext() == null ) return;
            boolean result = false;
            int toastTextId;
            switch( code ) {
                case 0:         //????????????
                    result = true;
                    toastTextId = R.string.stringVoucherOfBuyVipTicketOfSuccess;
                    break;
                case 1111:      //??????????????????
                    toastTextId = R.string.stringVoucherOfBuyVipTicketOfVip;
                    break;
                case 1112:      //??????????????????
                    toastTextId = R.string.stringVoucherOfBuyVipTicketOfFreeVip;
                    break;
                default:
                    toastTextId = R.string.stringVoucherOfBuyVipTicketOfFailure;
                    break;
            }

            final boolean finalResult = result;
            BuriedPointEvent.get().onMePageOfCouponPopupOfUseButton( getContext(), result );
            post( () -> {
                call.accept( finalResult );
                ToastManage.get().showToast( getContext(), toastTextId );
            } );
        } );
    }

    public void editIsRead(int id) {
        mReqVouchersModel.reqReqEditIsRead( id, null );
    }

    public void refreshDetail() {
        if( isQuerying ) return;
        isQuerying = true;
        mUserModel.getUserDetail(data -> {
            viewCall( v -> {
                isBindAccount = data.getIsBindAccount();
                v.onCallUpdateUserData( data );
            });
            isQuerying = false;
        });
        //?????????
        queryRankingList();
        //??????
        mReqIntegralModel.reqTotal( true, count -> {
            mUserModel.setIntegral( count );
            viewCall( v -> v.onCallIntegralTotalCount( count ) );
        });
        //????????????????????????
        viewCall( v -> v.onCallShowMessageCenter( mReqConfigModel.isEnableMessageCenter() ) );
    }

    public void startMsgCenterActivity() {
        //??????????????????
        BuriedPointEvent.get().onMePageOfNotificationAccess( getContext() );
        //??????????????????
        StartUtil.startMsgCenterActivity();
    }

    public void onTouchUserPhoto() {
        if( !isBindAccount ) {
            startLoginActivity();

            BuriedPointEvent.get().onVisitorMePageOfSignupLoginButton(
                    getContext(),
                    mUserModel.getUid(),
                    mUserModel.getUserName( getResources() )
            );
        }
    }

    public void startLoginActivity() {
        StartUtil.startLoginActivity(
                getActivity(),
                PageName.LOGIN,
                true
        );
    }

    public void startRankingActivity() {
        if( getContext() == null ) return;
        if( isLoginResult() ) {
            if( isEnableRanking() ) {
                StartUtil.startRankingActivity();
                buriedPointToViewRanking();
            }else {
                ToastManage.get().showToast( getContext(), R.string.stringRankingLock );
            }
        }else {
            startLoginActivity();
            buriedPointGuestRankingResult( true );
        }
    }

    public void startIntegralActivity() {
        if( isLoginResult() ) {
            com.gjjy.basiclib.utils.StartUtil.startIntegralActivity( getActivity() );
            BuriedPointEvent.get().onMePageOfPointButton( getContext() );
            return;
        }
        startLoginActivity();
    }

    public void startFriendsActivity() {
        if( isLoginResult() ) {
            StartUtil.startFriendsActivity();
            buriedPointToViewFriends();
            return;
        }
        startLoginActivity();
    }

    public void startInviteFriendsActivity() {
        if( isLoginResult() ) {
            StartUtil.startInviteFriendsActivity();
            buriedPointInviteFriends();
            return;
        }
        startLoginActivity();
    }

    public boolean isEnableRanking() { return getExpCount() >= 20; }

    public int getExpCount() { return mUserModel.getExpCount(); }

    public boolean isLoginResult() { return mUserModel.isLoginResult(); }

    public void queryRankingList() {
        if( getContext() == null ) return;
        String guestName = getContext().getResources().getString( R.string.stringSignature );
        mUserModel.getRankingList( data -> {
            int userIndex = -1;
            List<RankingAdapter.ItemData> list = new ArrayList<>();
            List<RankingListEntity.RankingEntity> rankList = data.getLists();
            if( rankList == null ) {
                viewCall( v -> v.onCallRankingList( new ArrayList<>() ) );
                return;
            }
            for( int i = 0; i < rankList.size(); i++ ) {
                RankingListEntity.RankingEntity rank = data.getLists().get( i );
                RankingAdapter.ItemData item = new RankingAdapter.ItemData();
                long userId = rank.getUserId();
                item.setId( userId );
                item.setName( TextUtils.isEmpty( rank.getNickName() ) ? guestName : rank.getNickName() );
                item.setImgUrl( rank.getAvatarUrl() );
                item.setXp( ObjUtils.parseInt( rank.getExpSum() ) );
                item.setUser( mUserModel.getUserId() == userId );
                item.setVip( rank.isVip() );
                item.setIndex( i + 1 );
                if( item.isUser() ) userIndex = i;
                list.add( item );
            }
            //?????????3??????
            if( list.size() <= 3 ) {
                viewCall( v -> v.onCallRankingList( list ) );
                return;
            }
            //???????????? ?????? ????????????
            if( userIndex <= 0 || userIndex >= list.size() ) {
                viewCall( v -> v.onCallRankingList( list.subList( 0, 3 ) ) );
                return;
            }
            //????????????
            if( userIndex == list.size() - 1 ) {
                viewCall( v -> v.onCallRankingList( list.subList( list.size() - 3, list.size() ) ) );
                return;
            }
            //???????????????
            int fUserIndex = userIndex;
            viewCall( v -> v.onCallRankingList( list.subList( fUserIndex - 1, fUserIndex + 2 ) ) );
        } );
    }

    /**
     ??????????????????
     @param code    ?????????
     */
    public void editFriendInvite(String code) {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );

        mUserModel.editFriendInvite( code, result -> viewCall( v -> {
            buriedPointInviteFriendsCodeBtn( result );
            v.onCallEditFriendInviteResult( null, result ? 1 : 0 );
            v.onCallShowLoadingDialog( false );
        } ) );
    }

    public void startVouchers() {
        StartUtil.startVouchersActivity();
        BuriedPointEvent.get().onMePageOfCouponCentre( getContext() );
    }

    public void buriedPointBuyVipResult(boolean result) {
        BuriedPointEvent.get().onMePageOfBuyVipButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                mUserModel.isLoginResult(),
                mUserModel.isVip(),
                result
        );
    }

    public void buriedPointGuestRankingResult(boolean result) {
        if( mUserModel.isLoginResult() ) return;
        BuriedPointEvent.get().onGuestMePageOfLeaderBoardOfLogin(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                result
        );
    }

    public void buriedPointToViewAchievement() {
        BuriedPointEvent.get().onMePageOfAchievementButton( getContext() );
        BuriedPointEvent.get().onMePageOfAchievementOfViewButton( getContext() );
    }

    public void buriedPointToViewRanking() {
        BuriedPointEvent.get().onMePageOfLeaderBoardOfViewButton( getContext() );
    }

    public void buriedPointToViewFriends() {
        BuriedPointEvent.get().onMePageOfFriendsOfViewButton( getContext() );
    }

    public void buriedPointInviteFriends() {
        BuriedPointEvent.get().onMePageOfInviteFriends( getContext() );
    }

    public void buriedPointInviteFriendsCodeBtn(boolean result) {
        BuriedPointEvent.get().onMePageOfAcceptInvitation( getContext(), result );
    }
}

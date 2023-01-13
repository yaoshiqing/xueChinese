package com.gjjy.usercenterlib.mvp.presenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.ObjUtils;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.view.RankingView;
import com.gjjy.basiclib.api.entity.RankingListEntity;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class RankingPresenter extends MvpPresenter<RankingView> {
    @Model
    private UserModel mUserModel;

    public RankingPresenter(@NonNull RankingView view) {
        super(view);
    }

    public void queryRankingList() {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        String guestName = getContext().getResources().getString( R.string.stringSignature );
        mUserModel.getRankingList( data -> {
            int curIndex = -1;
            int lastIndex = -1;
            List<RankingAdapter.ItemData> list = new ArrayList<>();
            List<RankingListEntity.RankingEntity> rankList = data.getLists();
            if( rankList == null ) {
                viewCall( v -> v.onCallRankingList( new ArrayList<>(), 0, 0 ) );
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
                if( item.isUser() ) curIndex = i;
                list.add( item );
            }
            int finalCurIndex = curIndex;
            viewCall( v -> {
                v.onCallRankingList( list, finalCurIndex, lastIndex );
                v.onCallShowLoadingDialog( false );
            } );
        } );
//        new Thread(() -> {
//            List<RankingAdapter.ItemData> list = new ArrayList<>();
////            int curIndex = (int) ( Math.random() * 30 );
////            int lastIndex = (int) ( Math.random() * 30 );
//            int curIndex = 5;  //当前位置
//            int lastIndex = 8;  //上次位置
//            for( int i = 0; i < 30; i++ ) {
//                RankingAdapter.ItemData data = new RankingAdapter.ItemData();
//                data.setName( "MiWi " + ( i + 1 ) + "@" + (int)( Math.random() * 99999 ) + 10000 );
//                if( i % 3 == 0 ) {
//                    data.setImgUrl( "https://pics6.baidu.com/feed/a08b87d6277f9e2f726b0e3db2882622b999f3ec.jpeg?token=8da340017c308f0d70971953991c9ef9" );
//                }
//                data.setXp( (int)( Math.random() * 900 ) + 100 );
//                data.setVip( i % 5 == 0 );
//                //当前用户
//                data.setUser( i == curIndex );
//                list.add( data );
//            }
//            viewCall( v -> v.onCallRankingList( list, curIndex, lastIndex ) );
//        } ).start();
    }
}
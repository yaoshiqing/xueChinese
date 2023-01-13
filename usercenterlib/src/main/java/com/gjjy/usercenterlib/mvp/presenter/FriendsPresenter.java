package com.gjjy.usercenterlib.mvp.presenter;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.adapter.FriendsAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.usercenterlib.mvp.view.FriendsView;
import com.gjjy.basiclib.api.entity.FriendEntity;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class FriendsPresenter extends MvpPresenter<FriendsView> {
    @Model
    private UserModel mUserModel;
    private int mPage;

    public FriendsPresenter(@NonNull FriendsView view) {
        super(view);
    }

    public void queryDataList() {
        mPage = 0;
        nextDataList();
    }

    public void nextDataList() {
        if( mPage == -1 ) {
            viewCall( v -> v.onCallFriendsList( null, true ) );
            return;
        }else {
            mPage++;
        }
        mUserModel.getFriendsList(mPage, data -> {
            List<FriendsAdapter.ItemData> list = new ArrayList<>();
            for( FriendEntity friend : data.getData() ) {
                FriendsAdapter.ItemData item = new FriendsAdapter.ItemData();
                item.setName( friend.getNickName() );
                item.setImgUrl( friend.getAvatarUrl() );
                item.setVip( friend.isVip() );
                list.add( item );
            }
            viewCall( v -> v.onCallFriendsList( list, mPage == -1 || mPage > 1 ) );
            if( mPage >= data.getLastPage() ) mPage = -1;
        });
    }
}
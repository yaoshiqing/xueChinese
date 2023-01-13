package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.adapter.FastReViewAdapter;
import com.gjjy.frontlib.mvp.view.FastReViewView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.api.entity.ReviewStatusEntity;
import com.gjjy.basiclib.mvp.model.ReqAnswerModel;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class FastReViewPresenter extends MvpPresenter<FastReViewView> {
    @Model
    private UserModel mUserModel;
    @Model
    private ReqAnswerModel mReqAnswer;
    public FastReViewPresenter(@NonNull FastReViewView view) {
        super(view);
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        mReqAnswer.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        initDataList();
    }

    public void buriedPoint(int categoryId, String categoryName) {
        BuriedPointEvent.get().onSelectionSortOfReviewSort(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                categoryId,
                categoryName
        );
    }

    private void initDataList() {
        FastReViewView v = getView();
        if( v != null ) v.onCallShowLoadingDialog( true );
        mReqAnswer.reqReviewStatus(list -> {
            List<FastReViewAdapter.ItemData> itemList = new ArrayList<>();
            for( ReviewStatusEntity data : list ) {
                FastReViewAdapter.ItemData item = new FastReViewAdapter.ItemData();
                item.setId( data.getCategoryId() );
                item.setTitle( data.getTitle() );
                item.setNewCount( data.getNewCount() );
                item.setEnable( data.getAllCount() > 0 );
                item.setIconUrl( item.isEnable() ? data.getImgUrl() : data.getLockImgUrl() );

                itemList.add(item);
            }
            viewCall( v1 -> v1.onCallDataList( itemList ) );
        });
    }
}
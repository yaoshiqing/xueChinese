package com.gjjy.frontlib.mvp.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.CategoryAllEntity;
import com.gjjy.frontlib.adapter.FrontListAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.gjjy.frontlib.R;
import com.gjjy.basiclib.api.entity.CategoryContentEntity;
import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.basiclib.mvp.model.ReqFrontInfoModel;
import com.gjjy.basiclib.mvp.model.ReqProvideUnitModel;

import java.util.ArrayList;
import java.util.List;

public class FrontInfoModel extends MvpModel {
    @Model
    private ReqFrontInfoModel mReqFrontInfo;
    @Model
    private ReqProvideUnitModel mReqProvideUnitModel;

    private final List<CategoryAllEntity> mAllCategoryList = new ArrayList<>();
    private final SparseArray<List<CategoryContentEntity>> mContentList = new SparseArray<>();
    private boolean isIntroduceComplete = false;

    public void setUid(String uid, String token) {
        mReqFrontInfo.setUid( uid, token );
        mReqProvideUnitModel.setUid( uid, token );
    }

    private void reqIntroduce(boolean isCache) {
        if( isCache ) return;
        mReqProvideUnitModel.reqProvideUnitDetail(
                entity -> isIntroduceComplete = entity.getStatus() == 2
        );
    }

    public boolean getAllCategory(boolean isCache, Consumer<List<CategoryAllEntity>> call) {
        //缓存
        if( isCache && mAllCategoryList.size() > 0 ) {
            if( call != null ) call.accept( mAllCategoryList );
            return true;
        }
        //网络
        mReqFrontInfo.reqAllCategory(list -> {
            mAllCategoryList.clear();
            mAllCategoryList.addAll( list );
            if( call != null ) call.accept( list );
        });
        return false;
    }

    public boolean getCategoryContent(int id, boolean isCache,
                                   Consumer<List<CategoryContentEntity>> call) {
        List<CategoryContentEntity> val;
        //缓存
        if( isCache &&
                mContentList.indexOfKey( id ) != -1 &&
                ( val = mContentList.get( id ) ) != null ) {
            if( call != null ) call.accept( val );
            return true;
        }
        mContentList.remove( id );
        //介绍页完成情况
        reqIntroduce( isCache );
        //网络
        mReqFrontInfo.reqCategoryContent(id, list -> {
            if( list.size() > 0 ) mContentList.put( id, list );
            if( call != null ) call.accept( list );
        });
        return false;
    }

    public FrontListAdapter.ChildItem getIntroduceItemData(Context context) {
        if( context == null ) return null;
        Resources res = context.getResources();
        FrontListAdapter.ChildItem itemData = new FrontListAdapter.ChildItem();
        itemData.setId( -1 );
        itemData.setTitle(
                res.getString( R.string.stringIntroduceTitle )
        );
        itemData.setImgResId( R.drawable.ic_front_daily_fixed );
        ArrayList<SectionIds> list = new ArrayList<>();
        list.add( new SectionIds( 0, "" ) );
        itemData.setSectionIds( list );
        itemData.setSectionNum( isIntroduceComplete ? 1 : 0 );
        itemData.setUnitStatus( isIntroduceComplete ? 2 : 1 );
        itemData.setComplete( isIntroduceComplete );
        return itemData;
    }
}

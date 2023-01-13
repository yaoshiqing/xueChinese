package com.gjjy.frontlib.mvp.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.api.entity.CategoryAllEntity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.adapter.WordsListAdapter;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.gjjy.frontlib.mvp.model.FrontInfoModel;
import com.gjjy.frontlib.mvp.model.WordsUnitModel;
import com.gjjy.frontlib.mvp.view.WordsListView;
import com.gjjy.basiclib.api.entity.CategoryContentEntity;
import com.gjjy.basiclib.api.entity.CategoryContentModelEntity;
import com.gjjy.basiclib.mvp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class WordsListPresenter extends MvpPresenter<WordsListView> {
    @Model
    private UserModel mUserModel;
    @Model
    private FrontInfoModel mFrontInfo;
    @Model
    private WordsUnitModel mWordsUnitModel;

    private int mCurrentCategoryId;
    private boolean mEnablePinYin = true;
    private boolean mEnableExplain = true;

    public WordsListPresenter(@NonNull WordsListView view) {
        super(view);
        mFrontInfo.setUid( mUserModel.getUid(), mUserModel.getToken() );
        mWordsUnitModel.setUid( mUserModel.getUid(), mUserModel.getToken() );

        //刷新分类
        boolean isCache = mFrontInfo.getAllCategory( true, this::callFrontMenuDataList );
        if( !isCache ) viewCall( v -> v.onCallLoadingDialog( true ) );
    }

    public void setEnablePinYin(boolean enable) { mEnablePinYin = enable; }
    public void setEnableExplain(boolean enable) { mEnableExplain = enable; }

    public int getCurrentCategoryId() { return mCurrentCategoryId; }

    public boolean eqSelectCategoryId(int id) {
        return id == mCurrentCategoryId;
    }

    public void setSelectCategoryId(int id) {
        if( id == -1 ) {
            viewCall(v -> {
                v.onCallLoadingDialog( false );
                v.onCallCategoryContent( new ArrayList<>() );
            });
            return;
        }
        mCurrentCategoryId = id;
        //加载分类内容
        boolean isCache = mFrontInfo.getCategoryContent( id, true, this::callItemDataList );
        if( !isCache ) viewCall( v -> v.onCallLoadingDialog( true ) );
    }

    public void getUnitWordsList(int unitId) {
        viewCall(v -> v.onCallLoadingDialog( true ));
        mWordsUnitModel.getWordsExplainAll(unitId, true, list -> {
            List<WordsListChildAdapter.ItemData> childList =
                    mWordsUnitModel.toWordsListChildDataList(list, itemData -> {
                                itemData.setEnablePinYin( mEnablePinYin );
                                itemData.setEnableExplain( mEnableExplain );
                    });
            viewCall( v -> {
                v.onCallWordsChildList( childList );
                v.onCallLoadingDialog( false );
            } );
        });

        buriedPointOpenWordsList();
    }

    private void buriedPointOpenWordsList() {
        BuriedPointEvent.get().onReviewModuleOfWordsList( getContext() );
    }

    private void callFrontMenuDataList(List<CategoryAllEntity> list) {
        if( list == null || list.size() == 0 ) {
            viewCall(v -> v.onCallLoadingDialog( false ));
            return;
        }
        List<FrontMenuAdapter.ItemData> callList = new ArrayList<>();
        FrontMenuAdapter.ItemData currentItem = null;
        for( CategoryAllEntity data : list ) {
            FrontMenuAdapter.ItemData item = new FrontMenuAdapter.ItemData();
            item.setId( data.getCategoryId() );
            item.setTitle( data.getTitle() );
            item.setIconUrl( data.getImgUrl() );
            callList.add( item );
            //当前选中列表的数据源
            if( currentItem == null && item.getId() == getCurrentCategoryId() ) currentItem = item;
        }
        if( callList.size() > 0 && currentItem == null ) currentItem = callList.get( 0 );
        if( currentItem != null ) {
            //获取列表
            setSelectCategoryId(currentItem.getId() );
        }

        FrontMenuAdapter.ItemData finalCurrentItem = currentItem;
        viewCall(v -> {
            v.onCallAllCategory( callList, finalCurrentItem );
//            v.onCallLoadingDialog( false );
        });
    }

    @UiThread
    private void callItemDataList(List<CategoryContentEntity> list) {
        if( list == null || list.size() == 0 ) {
            viewCall(v -> v.onCallLoadingDialog( false ));
            return;
        }

        List<WordsListAdapter.ItemData> callList = new ArrayList<>();
        for( CategoryContentEntity data : list ) {
            if( data == null || data.getUnit() == null ) continue;
            //子列表
            for( CategoryContentModelEntity cData : data.getUnit() ) {
                WordsListAdapter.ItemData item = new WordsListAdapter.ItemData();
                //模块状态：0未解锁、1已解锁、2已完成
                if( cData.getUnitStatus() == 0 ) continue;
                item.setId( cData.getUnitId() );
                item.setTitle( cData.getTitle() );
                item.setIconUrl( cData.getWordImgUrl() );
                item.setChildList( new ArrayList<>() );
                callList.add( item );
            }
        }

        viewCall(v -> {
            v.onCallCategoryContent( callList );
            v.onCallLoadingDialog( false );
        });
    }
}
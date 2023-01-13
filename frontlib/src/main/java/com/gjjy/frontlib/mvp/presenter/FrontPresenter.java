package com.gjjy.frontlib.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.api.entity.CategoryAllEntity;
import com.gjjy.frontlib.adapter.FrontListAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.mvp.model.FrontInfoModel;
import com.gjjy.frontlib.mvp.view.FrontView;
import com.gjjy.basiclib.api.entity.CategoryContentEntity;
import com.gjjy.basiclib.api.entity.CategoryContentModelEntity;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.entity.SectionIds;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.DOMConstant;

import java.util.ArrayList;
import java.util.List;

public class FrontPresenter extends MvpPresenter<FrontView> {
    @Model
    private UserModel mUserModel;
    @Model
    private FrontInfoModel mFrontInfo;
    private final Object[] mCurrentItem = new Object[ 5 ];


    private boolean mOnlyLoadingDialog = false;
    private int mIntroduceId;
    private boolean isIntroduce;

    public FrontPresenter(@NonNull FrontView view) {
        super(view);
        mFrontInfo.setUid( mUserModel.getUid(), mUserModel.getToken() );
    }

    public boolean isOnlyLoadingDialog() { return mOnlyLoadingDialog; }
    public void setOnlyLoadingDialog(boolean mOnlyLoadingDialog) {
        this.mOnlyLoadingDialog = mOnlyLoadingDialog;
    }

    @Override
    public void onLifeCreate(@Nullable Bundle savedInstanceState) {
        super.onLifeCreate(savedInstanceState);
        //更新时区
        mUserModel.updatedTimeZone( null );
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        //加载所有分类
        DOM.getInstance().setResult( DOMConstant.NOTIFY_FRONT_LIST, 1 );
        setOnlyLoadingDialog( true );
        notifyAllCategory();
    }

    public void updateCurrentItem(int position,
                                  int itemPosition,
                                  int levelId,
                                  String levelName,
                                  FrontListAdapter.ChildItem data) {
        mCurrentItem[ 0 ] = position;
        mCurrentItem[ 1 ] = itemPosition;
        mCurrentItem[ 2 ] = levelId;
        mCurrentItem[ 3 ] = levelName;
        mCurrentItem[ 4 ] = data;
    }

    public void startNormalAnswerDesc() {
        int position = (int) mCurrentItem[ 0 ];
        int itemPosition = (int) mCurrentItem[ 1 ];
        int levelId = (int) mCurrentItem[ 2 ];
        String levelName = (String) mCurrentItem[ 3 ];
        FrontListAdapter.ChildItem data = (FrontListAdapter.ChildItem) mCurrentItem[ 4 ];

        StartUtil.startNormalAnswerDescActivity(
                new AnswerBaseEntity()
                        .setCategoryId( mCurrentCategoryId )
                        .setCategoryName( mCurrentCategoryName )
                        .setUnitId( data.getId() )
                        .setUnitName( data.getTitle() )
                        .setUnitStatus( data.getUnitStatus() )
                        .setUnitId( data.getId() )
                        .setUnitName( data.getTitle() ),
                data.getSectionIds(),
                data.getSectionNum()
        );

        BuriedPointEvent.get().onCourseListOfModule(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                data.getId(),
                data.getTitle(),
                mCurrentCategoryId,
                mCurrentCategoryName,
                levelId,
                levelName
        );
        LogUtil.e("startNormalAnswerDesc -> pos:" + position +
                " | iPos:" + itemPosition +
                " | data -> " + data);
    }

    public void buriedPointUnLockModelTips() {
        int levelId = (int) mCurrentItem[ 2 ];
        String levelName = (String) mCurrentItem[ 3 ];
        FrontListAdapter.ChildItem data = (FrontListAdapter.ChildItem) mCurrentItem[ 4 ];
        //非会员的限时弹窗
        BuriedPointEvent.get().onLearnPageOfTestOutPopupOfUnlimitedTimeButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                data.getId(),
                data.getTitle(),
                mCurrentCategoryId,
                mCurrentCategoryName,
                levelId,
                levelName
        );
    }

    public void buriedPointUnLockTestTips(int id, String title) {
        //非会员的限时弹窗
        BuriedPointEvent.get().onLearnPageOfTestOutPopupOfUnlimitedStudyButton(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                id,
                title,
                mCurrentCategoryId,
                mCurrentCategoryName,
                (int) mCurrentItem[ 2 ],
                (String) mCurrentItem[ 3 ]
        );
    }

    public boolean isVip() { return mUserModel.isVip(); }

    public int getVipStatus() { return mUserModel.getVipStatus(); }

    public boolean isLoginResult() { return mUserModel.isLoginResult(); }

    private int mCurrentCategoryId;
    private String mCurrentCategoryName;
    public void setSelectCategoryId(int id, String name, boolean isCache) {
        isIntroduce = id == mIntroduceId;
        if( id == -1 ) {
            viewCall(v -> {
                v.onCallLoadingDialog( false );
                v.onCallCategoryContent( -1, new ArrayList<>() );
            });
            return;
        }
        mCurrentCategoryId = id;
        mCurrentCategoryName = name;
        //加载分类内容
        loadCategoryContent( id, isCache );
    }

    public int getCurrentCategoryId() { return mCurrentCategoryId; }

    private boolean isNotifying = false;
    public void notifyAllCategory() {
        if( isNotifying ) return;
        mFrontInfo.setUid( mUserModel.getUid(), mUserModel.getToken() );
        viewCall( v -> v.onCallLoadingDialog( true ) );
//        mOnlyLoadingDialog = true;
        isNotifying = true;
        //刷新分类
        mFrontInfo.getAllCategory( false, list -> {
            callFrontMenuDataList( list );
            isNotifying = false;
        } );
        //刷新金币
        refreshMoney();
    }

    public boolean isIntroduce() { return isIntroduce; }

    public void buriedPointCategory(int id, String name) {
        BuriedPointEvent.get().onCourseListOfSelectionSort(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                id,
                name
        );
    }

    public void buriedPointTestOut(int levelId, String levelName) {
        BuriedPointEvent.get().onCourseListOfTestOutPhase(
                getContext(),
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                levelId,
                levelName
        );
    }

    public void buriedPointPreface(int levelId, String levelName, int unitId, String unitName) {
        BuriedPointEvent.get().onDailyCourseListOfPrefaceModule(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName( getResources() ),
                mCurrentCategoryId, mCurrentCategoryName,
                levelId, levelName,
                unitId, unitName
        );
    }

    public void buriedPointLearnSignUpPopupPageOfSignUpLoginButton(boolean result) {
        post( () -> BuriedPointEvent.get().onLearnSignUpPopupPageOfSignUpLoginButton(
                getContext(),
                mUserModel.getUid(), mUserModel.getUserName( getResources() ),
                result
        ) );
    }

    private void loadCategoryContent(int id, boolean isCache) {
        isCache = mFrontInfo.getCategoryContent(id, isCache, list -> callItemDataList( id, list ));
        if( !isCache ) viewCall(v -> v.onCallLoadingDialog( true ));
    }

    public void refreshMoney() {
        mUserModel.getAward(data -> viewCall(v -> {
            int l = data.getLightning();
            int ol = data.getOldLightning();
            int h = data.getHeart();
            int oh = data.getOldHeart();
            v.onCallRewardMoney( l, ol, l != ol, h, oh, h != oh );
        }));
    }

    private void callFrontMenuDataList(List<CategoryAllEntity> list) {
        List<FrontMenuAdapter.ItemData> callList = new ArrayList<>();
        FrontMenuAdapter.ItemData currentItem = null;
        for (int i = 0; i < list.size(); i++) {
            CategoryAllEntity data = list.get( i );
            if( i == 0 ) mIntroduceId = data.getCategoryId();
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
            setSelectCategoryId( currentItem.getId(), currentItem.getTitle(), false );
        }

        FrontMenuAdapter.ItemData finalCurrentItem = currentItem;
        viewCall(v -> v.onCallAllCategory( callList, finalCurrentItem ));
    }

    @UiThread
    private void callItemDataList(int id, List<CategoryContentEntity> list) {
        if( list == null || list.size() == 0 ) {
            viewCall(v -> v.onCallLoadingDialog( false ));
            return;
        }

        List<FrontListAdapter.ItemData> callList = new ArrayList<>();

        for( CategoryContentEntity data : list ) {
            FrontListAdapter.ItemData item = new FrontListAdapter.ItemData();
            List<FrontListAdapter.ChildItem> cList = new ArrayList<>();
            int levelId = data.getLevelId();
            //子列表
            if( data.getUnit() != null ) {
                for( CategoryContentModelEntity cData : data.getUnit() ) {
                    FrontListAdapter.ChildItem cItem = new FrontListAdapter.ChildItem();
                    //模块状态：0未解锁、1已解锁、2已完成（isModelFullUnlock。题目全解锁）
                    int unitStatus = Config.isModelFullUnlock ? 1 : cData.getUnitStatus();
//                    int unitStatus = cData.getUnitStatus();
                    int sectionNum = cData.getSectionNum();
                    ArrayList<SectionIds> ids = cData.getSectionIds();

                    cItem.setId( cData.getUnitId() );
                    cItem.setSectionIds( ids );
                    cItem.setSectionNum( sectionNum );
                    cItem.setTitle( cData.getTitle() );
                    cItem.setImgUrl( cData.getImgUrl() );
//                    cItem.setImgUrl( unitStatus == 0 ? cData.getLockImgUrl() : cData.getImgUrl() );
                    cItem.setUnitStatus( unitStatus );
                    cItem.setTimestamp( System.currentTimeMillis() );
                    cItem.setComplete( unitStatus == 2 );
                    cItem.setUnlockSeconds( cData.getUnlockSeconds() );
                    cList.add( cItem );
                }
            }
//            阶段状态：0未解锁、1已解锁、2已完成（isModelFullUnlock。题目全解锁）
            int levelStatus = Config.isModelFullUnlock ? 1 : data.getLevelStatus();

            item.setId( levelId );
            item.setTitle( data.getTitle() );
            item.setLevelStatus( levelStatus );
            item.setTestComplete( levelStatus == 2 );
            item.setScore( data.getScore() );
            item.setData( cList );

            callList.add( item );
        }

        viewCall(v -> {
            v.onCallCategoryContent( id, callList );
            v.onCallLoadingDialog( false );
        });
    }

    public FrontListAdapter.ChildItem getIntroduceItemData() {
        return mFrontInfo.getIntroduceItemData( getContext() );
    }
}

package com.gjjy.steerlib.mvp.presenter;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.steerlib.mvp.view.GuideView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;

public class GuidePresenter extends MvpPresenter<GuideView> {
    @Model
    private UserModel mUserModel;

    private int mLanguageLevelType, mObjectiveType;
    private boolean isLoginResult;
    private boolean isShowGuideFrontPage;
    private boolean isShowLoginPage;
    private boolean isInitPage;

    public GuidePresenter(@NonNull GuideView view) {
        super(view);
    }

    public void onIntent(Intent intent) {
        if( intent == null ) return;
        isInitPage = intent.getBooleanExtra(
                Constant.GUIDE_INIT_PAGE, false
        );
        isShowGuideFrontPage = intent.getBooleanExtra(
                Constant.GUIDE_SHOW_GUIDE_HOME_PAGE, true
        );
        isShowLoginPage = intent.getBooleanExtra(
                Constant.GUIDE_SHOW_LOGIN_PAGE, true
        );
        LogUtil.e("Guide onIntent -> " +
                "isInitPage:" + isInitPage + " | " +
                "isShowGuideFrontPage:" + isShowLoginPage + " | " +
                "isShowLoginPage:" + isShowGuideFrontPage
        );
    }

    public boolean isInitPage() { return isInitPage; }

    public boolean isShowGuideFrontPage() { return isShowGuideFrontPage; }

    public boolean isShowLoginPage() {
        return isShowLoginPage;
    }

//    @Override
//    public void onLifeResume() {
//        super.onLifeResume();
//        //新增用户
//        mUserModel.addUser(result -> post(() -> {
//            GuideView v1 = getView();
//            if( v1 != null ) v1.onCallShowLoadingDialog( false );
//        }));
//    }

    public void setSelectedType(int position, int type) {
        switch ( position ) {
            case 1:         //LanguageLevel
                mLanguageLevelType = type;
                break;
            case 2:         //Objective
                mObjectiveType = type;
                break;
        }
        LogUtil.i( "SelectedType -> Position -> " + position + " | Type -> " + type );
    }

    public void setLoginSuccess(boolean loginSuccess) {
        isLoginResult = loginSuccess;
    }

    public boolean isLoginResult() {
        return isLoginResult;
    }

    /**
     * 结束选择
     */
    public void endSelected() {
        viewCall(v -> v.onCallShowLoadingDialog( true ));
        doReqEnd( 1 );
    }

    private void doReqEnd(int type) {
        switch ( type ) {
            case 1:
                //清空之前的数据
                mUserModel.logOut( r -> doReqEnd( 2 ) );
                break;
            case 2:
                //新增用户
                mUserModel.addUser( r -> post( () -> doReqEnd( 3 ) ) );
                break;
            case 3:
                //添加目的
                mUserModel.addAnswer(mLanguageLevelType, mObjectiveType, r ->
                        viewCall( v -> v.onCallShowLoadingDialog( false ) )
                );
                break;
        }
    }
}

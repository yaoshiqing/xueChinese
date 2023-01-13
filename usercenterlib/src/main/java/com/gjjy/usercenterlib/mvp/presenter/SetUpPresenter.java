package com.gjjy.usercenterlib.mvp.presenter;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.dao.entity.SkipAnswerTypeEntity;
import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.RemindModel;
import com.gjjy.basiclib.mvp.model.SetUpModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.mvp.view.SetUpView;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.toast.ToastManage;

public class SetUpPresenter extends MvpPresenter<SetUpView> {
    @Model
    private UserModel mUserModel;
    @Model
    private OtherModel mOtherModel;
    @Model
    private SetUpModel mSetUpModel;
    private RemindModel mRemindModel;

    private UserDetailEntity mUserDetail;

    private boolean isBindAccount;

    public SetUpPresenter(@NonNull SetUpView view) {
        super(view);
    }

    @Override
    public void onLifeResume() {
        super.onLifeResume();
        mRemindModel = mUserModel.getRemindModel();
        mOtherModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
        mRemindModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
        //加载用户信息
        loadUserInfo();

        //显示/隐藏重置密码按钮
        viewCall( v -> v.onCallShowResetPassword( mUserModel.isEmailLogin() ) );
    }

//    public void setLogOut(boolean logOut) { isLogOut = logOut; }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        if( mUserDetail == null ) {
            mUserDetail = mUserModel.getUserDetail();
            isBindAccount = mUserDetail.getIsBindAccount();
            viewCall( v -> {
                v.onCallUpdateUserData( mUserDetail );
                v.onCallLoginResult( isBindAccount );
            });
//            mUserModel.getUserDetail(data -> viewCall(v -> {
//                mUserDetail = data;
//                isBindAccount = data.getIsBindAccount();
//                v.onCallUpdateUserData( data );
//                v.onCallLoginResult( isBindAccount );
//            }));
        }
        //学习提醒
        mRemindModel.getRemindDetail(true, entity ->
                viewCall(v -> v.onCallRemindSwitch( entity.isRemindSwitch() ))
        );
    }

    public boolean isVip() { return mUserModel.isVip(); }

    public boolean isLoginResult() { return mUserModel.isLoginResult(); }

    public String getLoginId() { return mUserDetail.getLoginId(); }

    /**
     * 编辑名称
     * @param newName   新名称
     */
    public void editUserName(String newName) {
        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mUserModel.editUserInfo(
                null, newName, null, -1, null, null, result -> viewCall(v -> {
            if( mUserDetail != null && result ) {
                mUserDetail.setNickname( newName );
                v.onCallUpdateUserData( mUserDetail );
            }
            v.onCallShowLoadingDialog( false );
        }));
    }

    public void updateUserPhoto(Bitmap bmp) {
        if( getContext() == null ) return;
        viewCall( v -> v.onCallShowLoadingDialog( true ) );
        mUserModel.editUserAvatarUrl( getContext(), bmp, data -> {
            if( mUserDetail == null || data == null ) {
                viewCall( v -> {
                    ToastManage.get().showToast( getContext(), R.string.stringError );
                    v.onCallShowLoadingDialog( false );
                } );
                return;
            }
            mUserDetail.setAvatarUrl( data.getLocalFilePath() );
            viewCall( v -> {
                v.onCallUpdateUserData( mUserDetail );
                v.onCallShowLoadingDialog( false );
            } );
        } );
    }

//    private boolean isLogOut = false;
//
//    @Override
//    public void onLifeDestroy() {
//        super.onLifeDestroy();
//        LogUtil.e("Setting -> isLogOut:" + isLogOut);
//        if( isLogOut ) mUserModel.logOut( null );
//    }

    public void startLearningReminderActivity() {
        //埋点学习提醒
        BuriedPointEvent.get().onSetUpPageOfLearningReminder(
                getContext(), mUserModel.getUid(), mUserModel.getUserName( getResources() )
        );
        //学习提醒页面
        StartUtil.startLearningReminderActivity();
    }

    public void startEmailResetPasswordActivity() {
        BuriedPointEvent.get().onSetUpPageOfResetPassword( getContext() );
        StartUtil.startEmailUpdatePasswordActivity();
    }

    public void startEvaluationActivity() {
        if( getContext() == null ) return;
        if( SysUtil.startGooglePlay( getContext() ) ) return;
        //评价页面打开失败
        ToastManage.get().showToast( getContext(), R.string.stringStartEvaluationError );
    }

    public void initRemind() {
        mRemindModel.initData();
    }

    /**
     * 退出登录
     */
    public void logOut() {
//        viewCall(v1 -> v1.onCallLogOut( isLogOut ));
//        DOM.getInstance().setResult( DOMConstant.LOG_OFF );
        mUserModel.doLogOut( getActivity() );
    }
    public void logOutOfOnActivityResult(int requestCode, int resultCode) {
        mUserModel.doLogOutOfOnActivityResult( getActivity(), requestCode, resultCode );
    }

    public void onQuestionSwitch(int position, boolean isChecked) {
        mSetUpModel.saveSkipQuestion( position, isChecked );
    }

    public void onBuriedPointSwitchQuestionType(boolean isChecked, String switchName) {
        BuriedPointEvent.get().onSetUpPageOfQuestionTypeSwitch(
                getContext(),
                isChecked,
                switchName,
                mUserModel.isLoginResult() ?
                        PageName.SETTING :
                        PageName.GUEST_SETTING
        );
    }

    public boolean[] getQuestionSwitchIsEnable() {
        SkipAnswerTypeEntity data = mSetUpModel.getSkipQuestion();
        return new boolean[] {
                data.isHearing(),
                data.isVoice(),
                data.isTranslate(),
                data.isSound(),
                data.isSnail()
        };
    }
}

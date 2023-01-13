package com.gjjy.loginlib.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.LoginType;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.OtherModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.login.facebook.AccountResult;
import com.gjjy.loginlib.mvp.view.LoginView;
import com.gjjy.loginlib.utils.StartUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.ybear.ybutils.utils.LogUtil;

public class LoginPresenter extends MvpPresenter<LoginView> {
    @Model
    private UserModel mUserModel;
    @Model
    private OtherModel mOtherModel;
    @Model
    private LoginModel mLoginModel;

    private boolean isShowBackBtn = true;
    private boolean isLoginResult;
    private boolean isAuthLogin = false;
    private Intent mCallData;
    private String mPageName = "";

    public LoginPresenter(@NonNull LoginView view) {
        super(view);
    }

    public void initIntent(Intent intent) {
        if( intent == null ) return;
        isShowBackBtn = !intent.getBooleanExtra( Constant.LOGIN_NOT_BACK_BTN, false );

        mPageName = intent.getStringExtra( Constant.PAGE_NAME );
    }

    public void checkUid() {
        if( mUserModel.isExistUid() ) return;
        viewCall(v -> v.onCallIsShowLoading( true ));
        mUserModel.addUser(aBoolean -> viewCall(v -> v.onCallIsShowLoading( false )));
    }

    public void setAuthLogin(boolean authLogin) { isAuthLogin = authLogin; }

    public boolean isShowBackBtn() { return isShowBackBtn; }

    public void onFinish() {
        Activity activity = getActivity();
        if( activity == null ) return;
        activity.setResult(
                isLoginResult ? Activity.RESULT_OK :
                        isBindAccount ? Activity.RESULT_FIRST_USER : Activity.RESULT_CANCELED,
                mCallData
        );
    }

    public void startErrorActivity() {
        StartUtil.startLoginErrorActivity( getActivity() );
    }

    public void loginGoogle(GoogleSignInAccount account) {
        if( mLoginModel.checkTouchIntervalTimeOut( LoginModel.TimeOut.DEFAULT ) ) return;
        Uri avatarUrl = account.getPhotoUrl();
        login( "GOOGLE",
                account.getId(),
                account.getDisplayName(),
                avatarUrl != null ? avatarUrl.toString() : "",
                0,
                "",
                account.getEmail()
        );
//        LogUtil.e( "loginGoogle -> " + Arrays.toString( account.getGrantedScopes().toArray( new Scope[ 0 ] ) ) );
    }

    public void loginFacebook(AccountResult account) {
        if( mLoginModel.checkTouchIntervalTimeOut( LoginModel.TimeOut.DEFAULT ) ) return;

        String fbGender = account.getGender();
        int gender = "male".equals( fbGender ) ? 1 : "female".equals( fbGender ) ? 2 : 0;
        login( "FACEBOOK",
                account.getId(),
                account.getName(),
                account.getPhotoUrl(),
                gender,
                "",
                account.getEmail());

        LogUtil.e( "login -> facebook -> account -> " + account.toString() );
    }

    public void loginEmail(String id) {
        String type = "EMAIL";
        viewCall( v -> v.onCallIsShowLoading( true ) );
        onDoLoginResult(type, id, 1, callId -> onCallLoginSuccess( type, callId ) );
    }

    private boolean isBindAccount = false;
    private void login(String type,
                       String id,
                       @Nullable String nickname,
                       @Nullable String avatarUrl,
                       @IntRange(from = -1, to = 2) int gender,
                       @Nullable String phone,
                       @Nullable String email) {
        LogUtil.i("LoginPresenter -> type:" + type + " | id:" + id +
                " | nickname:" + nickname + " | avatarUrl:" + avatarUrl +
                " | gender:" + gender + " | phone:" + phone + " | email:" + email);
        LoginView v = getView();
        if( v != null ) post(() -> v.onCallIsShowLoading( true ));

        mUserModel.loginUid(type, id, result -> onDoLoginResult(type, id, result, callId -> {
            //上传信息
            mUserModel.editUserInfo(
                    id, nickname, avatarUrl, gender, phone, email,
                    editResult -> onCallLoginSuccess( type, callId )
            );
        }));
    }

    public void onDoLoginResult(String type, String id, int result, Consumer<String> callSuccess) {
        LogUtil.e("LoginPresenter -> onDoLoginResult:" + id + " | result:" + result);
        mCallData = new Intent();
        mCallData.putExtra( Constant.LOGIN_CALL_RESULT, result );
        if( result == 1 ) {
            if( callSuccess != null ) callSuccess.accept( id );
        }else {
            isBindAccount = result == 2;
            onCallLoginSuccess( type, id );
//            mUserModel.getUserDetail(data -> viewCall(v -> {
//                v.onCallIsShowLoading( false );
//                v.onCallLoginResult( type, id, result );
//            }));
        }
    }

    private void onCallLoginSuccess(String type, String id) {
        LogUtil.e("LoginPresenter -> onCallLoginSuccess:" + id + " | type:" + type);
        isLoginResult = true;
        mUserModel.getUserDetail(data -> viewCall(v -> {
            //埋点
            buriedPoint( type, id );
            mOtherModel.refreshUid( mUserModel.getUid(), mUserModel.getToken() );
            mOtherModel.bindPushId( null );
            v.onCallIsShowLoading( false );
            v.onCallLoginResult( type, id, 1 );
        }));
    }

    public void onCheckAuthLogin() {
        viewCall( v -> v.onCallDisLoginTips( !isAuthLogin ) );
    }

    public boolean isAuthLogin() { return isAuthLogin; }

    private void buriedPoint(String loginType, String uid) {
        String type = "Null";
        switch ( loginType ) {
            case "GOOGLE":
                type = LoginType.GOOGLE;
                break;
            case "FACEBOOK":
                type = LoginType.FACEBOOK;
                break;
            case "EMAIL":
                type = LoginType.EMAIL;
                break;
        }
        BuriedPointEvent.get().onRegisterLoginOfLoginMethod(
                getContext(),
                uid,
                mUserModel.getUserName( getResources() ),
                type,
                mPageName
        );
    }
}
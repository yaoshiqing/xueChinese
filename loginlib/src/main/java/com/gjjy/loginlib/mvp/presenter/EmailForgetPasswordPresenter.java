package com.gjjy.loginlib.mvp.presenter;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.loginlib.mvp.view.EmailForgetPasswordView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.ReqUserModel;

public class EmailForgetPasswordPresenter extends MvpPresenter<EmailForgetPasswordView> {
    @Model
    private LoginModel mLoginModel;
    @Model
    private ReqUserModel mReqUserModel;

    public EmailForgetPasswordPresenter(@NonNull EmailForgetPasswordView view) {
        super(view);
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        mLoginModel.hideKeyboard( getActivity(), ev );
    }

    public void forget(String account) {
        if( mLoginModel.checkTouchIntervalTimeOut( LoginModel.TimeOut.LONG_TIME ) ) return;

        boolean checkAcc = mLoginModel.checkEmail( account );

        viewCall( v -> {
            v.onCallShowEmailTips( !checkAcc );
            v.onCallShowLoadingDialog( checkAcc );
        } );

        //埋点忘记密码按钮点击时
        BuriedPointEvent.get().onForgetPasswordPageOfConfirmButton( getContext() );

        if( !checkAcc ) return;

        mReqUserModel.reqSendEmailCode(account, result -> viewCall(v -> {
            if( result == 1 ) {
                //登录流程
                v.onCallForgetPassword( account );
                v.onCallShowLoadingDialog( false );
            }else {
                v.onCallError( result );
                v.onCallShowLoadingDialog( false );
            }
        }));
    }

    public boolean checkAccount(@NonNull String account) {
        boolean checkAcc = mLoginModel.checkEmail( account.trim() );
        viewCall(v -> v.onCallShowEmailTips( !checkAcc ));
        return checkAcc;
    }
}
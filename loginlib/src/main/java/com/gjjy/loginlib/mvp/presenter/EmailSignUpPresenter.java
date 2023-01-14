package com.gjjy.loginlib.mvp.presenter;

import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.UserRegisterReq;
import com.gjjy.basiclib.mvp.model.LoginModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.loginlib.mvp.view.EmailSignUpView;
import com.gjjy.loginlib.utils.StartUtil;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.presenter.MvpPresenter;

public class EmailSignUpPresenter extends MvpPresenter<EmailSignUpView> {
    @Model
    private LoginModel mLoginModel;
    @Model
    private UserModel mUserModel;

    private boolean mNameIsNull = true;
    private boolean mEmailIsNull = true;
    private boolean mPwdIsNull = true;
    private boolean mPwd2IsNull = true;
    private boolean mPhoneIsNull = true;
    private boolean mSmsCodeIsNull = true;

    public EmailSignUpPresenter(@NonNull EmailSignUpView view) {
        super(view);
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        mLoginModel.hideKeyboard(getActivity(), ev);
    }

    private void checkIsNull() {
        viewCall(v -> v.onCallAuthSignUp(
                !mNameIsNull && !mEmailIsNull && !mPwdIsNull
                        && !mPwd2IsNull && !mPhoneIsNull && !mSmsCodeIsNull));
    }

    public void startEmailLoginActivity() {
        //埋点登录按钮
        BuriedPointEvent.get().onVisitorMePageOfSignupLoginButton(getContext(), mUserModel.getUid(), mUserModel.getUserName(getResources()));
        //邮箱登录页
        StartUtil.startEmailLoginActivity(getActivity());
    }

    // 注册
    public void signUp(String name, String account, String password, String checkPassword) {
        if (mLoginModel.checkTouchIntervalTimeOut(LoginModel.TimeOut.LONG_TIME)) {
            return;
        }
        //数据检查
        if (!check(name, account, password, checkPassword)) {
            return;
        }
        //注册
        mUserModel.bindEmail(name, account, password, result -> {
            //埋点注册
            BuriedPointEvent.get().onEmailLoginPageOfSignUpButton(getContext(), mUserModel.getUid(), mUserModel.getUserName(getResources()), result == 1);
            //注册流程
            viewCall(v -> v.onCallSignUpResult(account.trim(), password.trim(), result == 1, result));
        });
    }

    private boolean check(String name, String account, String pwd1, String pwd2) {
        return checkName(name)
                && checkAccount(account)
                && checkPassword(pwd1)
                && checkEqPassword(pwd1, pwd2);
    }

    private boolean checkValid(String name, String account, String pwd1, String pwd2, String phone, String verifyCode) {
        boolean checkResult = check(name, account, pwd1, pwd2);
        return checkResult && checkPhone(phone) && checkVerifyCode(verifyCode);
    }

    public boolean checkName(@NonNull String name) {
        boolean checkName = mLoginModel.checkName(name);
        viewCall(v -> v.onCallShowNameTips(!checkName));

        mNameIsNull = TextUtils.isEmpty(name);
        checkIsNull();
        return checkName;
    }

    public boolean checkAccount(@NonNull String account) {
        boolean checkAcc = mLoginModel.checkEmail(account.trim());
        viewCall(v -> v.onCallShowEmailTips(!checkAcc));

        mEmailIsNull = TextUtils.isEmpty(account);
        checkIsNull();
        return checkAcc;
    }

    public boolean checkPassword(@NonNull String pwd) {
        boolean checkPwd = mLoginModel.checkPassword(pwd.trim());
        viewCall(v -> v.onCallShowPasswordTips(!checkPwd));

        mPwdIsNull = TextUtils.isEmpty(pwd);
        checkIsNull();
        return checkPwd;
    }

    public boolean checkEqPassword(@NonNull String pwd1, @NonNull String pwd2) {
        boolean eqPwd = mLoginModel.checkPassword(pwd1.trim(), pwd2.trim());
        viewCall(v -> v.onCallShowCheckPasswordTips(!eqPwd));

        mPwd2IsNull = TextUtils.isEmpty(pwd2);
        checkIsNull();
        return eqPwd;
    }

    public boolean checkPhone(@NonNull String phone) {
        mPhoneIsNull = TextUtils.isEmpty(phone);
        checkIsNull();
        return mPhoneIsNull;
    }

    public boolean checkVerifyCode(@NonNull String smsCode) {
        boolean checkSmsCode = mLoginModel.checkVerifyCode(smsCode.trim());
        mSmsCodeIsNull = TextUtils.isEmpty(smsCode);
        checkIsNull();
        return checkSmsCode;
    }

    public void setLoginText(String left, String right) {
        EmailSignUpView v = getView();
        if (v != null) {
            v.onCallLoginText(mLoginModel.getAccountTipsTextStyle(left, right));
        }
    }

    /**
     * 手机发送验证码
     */
    public void sendSmsCode(String phone, String countryCode) {
        mUserModel.reqSendSmsCode(phone, countryCode, result -> {
            viewCall(v -> v.onCallSendSmsCodeResult(phone, result.getErrCode(), result.getErrMsg()));
        });
    }

    // 新注册方法
    public void userRegister(String nickname, String email, String password, String checkPassword, String phone, String smsCode) {
        if (mLoginModel.checkTouchIntervalTimeOut(LoginModel.TimeOut.LONG_TIME)) {
            return;
        }
        //数据检查
        if (!checkValid(nickname, email, password, checkPassword, phone, smsCode)) {
            return;
        }
        UserRegisterReq req = new UserRegisterReq();
        req.setEmail(email);
        req.setNickname(nickname);
        req.setPassword(password);
        req.setLang(Config.getLang());
        req.setUserId((int) mUserModel.getUserId());
        req.setSmsCode(smsCode);
        req.setPhone(phone);

        //注册
        mUserModel.reqUserRegister(req, result -> {
            //埋点注册
            BuriedPointEvent.get().onEmailLoginPageOfSignUpButton(getContext(), mUserModel.getUid(), mUserModel.getUserName(getResources()), result.isSuccess());
            //注册流程
            viewCall(view -> view.onCallUserRegisterResult(email.trim(), password.trim(), phone.trim(), result.isSuccess(), result.getErrMsg()));
        });
    }
}
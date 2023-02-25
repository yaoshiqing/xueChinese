package com.gjjy.loginlib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.login.facebook.AccountResult;
import com.gjjy.login.facebook.FacebookCallback;
import com.gjjy.login.google.GoogleCallback;
import com.gjjy.loginlib.R;
import com.gjjy.loginlib.mvp.presenter.LoginPresenter;
import com.gjjy.loginlib.mvp.view.LoginView;
import com.gjjy.loginlib.widget.EmailLoginButton;
import com.gjjy.loginlib.widget.FacebookLoginButton;
import com.gjjy.loginlib.widget.GoogleLoginButton;
import com.facebook.FacebookException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ybear.mvp.annotations.LifeStatus;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.Build;

/**
 * 登录界面
 */
@Route(path = "/login/loginActivity")
public class LoginActivity extends BaseActivity implements LoginView {
    @Presenter
    private LoginPresenter mPresenter;
    private Toolbar tbToolbar;
    private EmailLoginButton elbEmailBtn;
    private GoogleLoginButton glbGoogleBtn;
    private FacebookLoginButton flbFacebookBtn;
    private TextView tvProtocol;
    private TextView tvPrivacyPolicy;
//    private ShapeTextView stvLoginPrivacyPolicyTips;

    private int mLoginType = LoginErrorActivity.LoginType.GOOGLE;
    private Build mToastBuild;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPresenter.initIntent(getIntent());
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flbFacebookBtn.unregisterCallback();
    }

    @Override
    public void finish() {
        mPresenter.onFinish();
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("LoginActivity -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode + " | " +
                "data:" + data
        );
        glbGoogleBtn.onActivityResult(requestCode, resultCode, data);
        flbFacebookBtn.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case StartUtil.REQUEST_CODE_LOGIN_ERROR:            //登录错误返回结果
                switch (mLoginType) {
                    case LoginErrorActivity.LoginType.GOOGLE:       //Google登录
                        glbGoogleBtn.performClick();
                        break;
                    case LoginErrorActivity.LoginType.FACEBOOK:     //Facebook登录
                        flbFacebookBtn.performClick();
                        break;
//                case LoginErrorActivity.LoginType.EMAIL:        //邮箱登录
//                    elbEmailBtn.performClick();
//                    break;
                }
                break;
            case StartUtil.REQUEST_CODE_EMAIL_LOGIN_RESULT:     //邮箱登录成功
                if (data == null) return;
                mPresenter.loginEmail(data.getStringExtra(Constant.EMAIL));
                break;
        }
    }

    private void initView() {
        tbToolbar = findViewById(R.id.login_tb_toolbar);
        elbEmailBtn = findViewById(R.id.login_elb_email_btn);
        glbGoogleBtn = findViewById(R.id.login_glb_google_btn);
        flbFacebookBtn = findViewById(R.id.login_flb_facebook_btn);
        tvProtocol = findViewById(R.id.login_tv_protocol);
        tvPrivacyPolicy = findViewById(R.id.login_tv_privacy_policy);
//        stvLoginPrivacyPolicyTips = findViewById( R.id.login_stv_login_privacy_policy_tips );
    }

    private DialogOption mLoadingDialog;

    private void initData() {
        setStatusBarHeightForSpace(findViewById(R.id.toolbar_height_space));

        //默认勾选协议
        switchPrivacyPolicy();

        tbToolbar.setBackBtnOfImg(R.drawable.ic_white_back);
        tbToolbar.showBackBtnOfImg(mPresenter.isShowBackBtn());
        mLoadingDialog = createLoadingDialog();

        elbEmailBtn.setEnableClick(mPresenter.isAuthLogin());
        glbGoogleBtn.setEnableClick(mPresenter.isAuthLogin());
        flbFacebookBtn.setEnableClick(mPresenter.isAuthLogin());

        String[] pps = getString(R.string.stringLoginPrivacyPolicy).split("\\|");
        tvProtocol.setText(String.format(pps[0], AppUtil.getAppName(this)));
        tvPrivacyPolicy.setText(Html.fromHtml(String.format(pps[1], "<u>", "</u>")));

        mToastBuild = new Build();
        mToastBuild.setTextColor(getResources().getColor(R.color.colorFailureToastText));
        mToastBuild.setBackgroundResource(R.drawable.shape_login_toast_bg);
        mToastBuild.setGravity(Gravity.BOTTOM);
        mToastBuild.setYOffset(Utils.dp2Px(this, 10));

        mPresenter.checkUid();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        elbEmailBtn.setOnClickListener(v -> {
            mLoginType = LoginErrorActivity.LoginType.EMAIL;
            com.gjjy.loginlib.utils.StartUtil.startEmailLoginActivity(this);
        });
        glbGoogleBtn.setOnClickListener(v -> mLoginType = LoginErrorActivity.LoginType.GOOGLE);
        flbFacebookBtn.setOnClickListener(v -> mLoginType = LoginErrorActivity.LoginType.FACEBOOK);

        Consumer<Boolean> onIsEnableClick = enable -> {
            if (enable) return;
            mPresenter.onCheckAuthLogin();
        };
        elbEmailBtn.setOnIsEnableClickListener(onIsEnableClick);
        flbFacebookBtn.setOnIsEnableClickListener(onIsEnableClick);
        glbGoogleBtn.setOnIsEnableClickListener(onIsEnableClick);

        glbGoogleBtn.registerCallback(this, new GoogleCallback() {
            @Override
            public void onSuccess(GoogleSignInAccount account, boolean isFirst) {
//                glbGoogleBtn.startRequestGenders( account, genders -> {
//                    for( Gender gender : genders ) {
//                        LogUtil.d( "startRequest", "gender:" + gender );
//                    }
//                } );
//                glbGoogleBtn.startRequestEmail( account, emailAddresses -> {
//                    for( EmailAddress email : emailAddresses ) {
//                        LogUtil.d( "startRequest", "email:" + email );
//                    }
//                } );
                mPresenter.loginGoogle(account);
            }

            @Override
            public void onCancel() {
                LogUtil.i("[Google Login] onCancel");
            }

            @Override
            public void onFailure() {
                mPresenter.startErrorActivity();
                LogUtil.e("[Google Login] onFailure");
            }
        });

        flbFacebookBtn.registerCallback(this, new FacebookCallback() {
            @Override
            public void onSuccess(AccountResult result) {
                mPresenter.loginFacebook(result);
            }

            @Override
            public void onCancel() {
                LogUtil.i("[Facebook Login] onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                mPresenter.startErrorActivity();
                LogUtil.e("[Facebook Login] onError -> " + error);
            }
        });

        tvProtocol.setOnClickListener(v -> {
            switchPrivacyPolicy();
            mPresenter.onCheckAuthLogin();
        });

        tvPrivacyPolicy.setOnClickListener(v -> StartUtil.startPolicyActivity());
    }

    @Override
    public int onStatusBarIconColor() {
        return SysUtil.StatusBarIconColor.WHITE;
    }

    /**
     * 回调登录结果
     *
     * @param result 登录结果
     */
    @Override
    public void onCallLoginResult(String type, String id, int result) {
        LogUtil.e("onCallLoginResult:" + result);
        switch (result) {
            case -1:
                if (getLifeStatus() != LifeStatus.PAUSE) {
                    mPresenter.startErrorActivity();
                }
                break;
            case 1:
            case 2:
                onSignIn(type, id);
                finish();
                break;
        }
    }

    /**
     * 是否显示等待对话框
     *
     * @param isShow 是否显示
     */
    @Override
    public void onCallIsShowLoading(boolean isShow) {
        if (isShow) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onCallDisLoginTips(boolean isDisable) {
        elbEmailBtn.setEnableClick(!isDisable);
        glbGoogleBtn.setEnableClick(!isDisable);
        flbFacebookBtn.setEnableClick(!isDisable);

        if (isDisable) {
            showToast(R.string.stringLoginPrivacyPolicyTips, mToastBuild);
        }
//        AnimUtil.setAlphaAnimator(350, animator ->
//                stvLoginPrivacyPolicyTips.setVisibility(isDisable ? View.VISIBLE : View.INVISIBLE),
//                stvLoginPrivacyPolicyTips
//        );
    }

    private void switchPrivacyPolicy() {
        boolean flag = "0".equals(tvProtocol.getTag());
        tvProtocol.setTag(flag ? "1" : "0");
        tvProtocol.setCompoundDrawablesWithIntrinsicBounds(
                flag ? R.drawable.ic_privacy_policy_selected : R.drawable.ic_privacy_policy_unselected,
                0,
                0,
                0
        );
        LogUtil.e("switchPrivacyPolicy -> " + flag);
        mPresenter.setAuthLogin(flag);
    }
}

package com.gjjy.loginlib.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.statistical.StatisticalManage;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.EditView;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.loginlib.R;
import com.gjjy.loginlib.mvp.presenter.EmailSignUpPresenter;
import com.gjjy.loginlib.mvp.view.EmailSignUpView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.toast.Build;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 邮箱登录注册
 */
@Route(path = "/login/emailSignUpActivity")
public class EmailSignUpActivity extends BaseActivity implements EmailSignUpView {
    @Presenter
    private EmailSignUpPresenter mPresenter;
    private Toolbar tbToolbar;
    private TextView tvSignUpBtn;
    private EditView evName;
    private EditView evEmail;
    private EditView evPassword;
    private EditView evCheckPassword;
    private EditView evMobileNum;
    private EditView evVerifyCode;
    private TextView tvLoginBtn;
    private TextView txtCountryCode;

    private Build mToastBuild;
    private TextView btnSend;
    // 发送短信倒计时 60s
    private int index = 60;
    private String selectedCountryCode = "+86";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        initView();
        initData();
        initListener();
        //清除透明状态栏
        clearTranslucentStatus();
    }

    @Override
    public boolean onEnableImmersive() {
        return false;
    }

    private void initView() {
        tbToolbar = findViewById(R.id.email_sign_up_tb_toolbar);
        tvLoginBtn = findViewById(R.id.email_sign_up_tv_login_btn);
        evName = findViewById(R.id.email_sign_up_ev_name);
        evEmail = findViewById(R.id.email_sign_up_ev_email);
        evPassword = findViewById(R.id.email_sign_up_ev_pwd);
        evCheckPassword = findViewById(R.id.email_sign_up_ev_check_pwd);
        evMobileNum = findViewById(R.id.email_sign_up_ev_mobile);
        evVerifyCode = findViewById(R.id.email_sign_up_ev_verify_code);
        tvSignUpBtn = findViewById(R.id.email_sign_up_tv_sign_up_btn);
        txtCountryCode = findViewById(R.id.mobile_num_pre);
        btnSend = findViewById(R.id.email_sign_up_btn_send);
    }

    private void initData() {
        mPresenter.setLoginText(
                getString(R.string.stringEmailHaveEmail),
                getString(R.string.stringEmailLogIn)
        );

        mToastBuild = new Build();
        mToastBuild.setGravity(Gravity.BOTTOM);
        mToastBuild.setYOffset(Utils.dp2Px(this, 30));
    }


    private void initListener() {
        txtCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 发送验证码
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                index--;
                                btnSend.setText(index + "s");
                                btnSend.setEnabled(false);
                                if (index == 2) {
                                    Random random = new Random();
                                    int i = random.nextInt(899999);
                                    int random_ed = i + 100000;
                                    btnSend.setText(random_ed + "");
                                }
                                if (index <= 0) {
                                    btnSend.setEnabled(true);
                                    btnSend.setText(getResources().getString(R.string.stringEmailMobileVerifyCodeButton));
                                    timer.cancel();
                                    index = 5;
                                }
                            }
                        });
                    }
                }, 0, 1000);

                String phone = evMobileNum.getTextString();
                String countryCode = selectedCountryCode.substring(1);
                mPresenter.sendSmsCode(phone, countryCode);
            }
        });

        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        //登录
        tvLoginBtn.setOnClickListener(v -> {
            if (getStackManage().isHaveExistActivity(EmailLoginActivity.class)) {
                finish();
                return;
            }
            mPresenter.startEmailLoginActivity();
        });

        EditText etNameView = evName.getView();
        etNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etNameView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) mPresenter.checkName(etNameView.getText().toString());
        });

        EditText etEmailView = evEmail.getView();
        etEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkAccount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etEmailView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) mPresenter.checkAccount(etEmailView.getText().toString());
        });

        EditText etPwdView = evPassword.getView();
        etPwdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPwdView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) mPresenter.checkPassword(etPwdView.getText().toString());
        });

        EditText etPwd2View = evCheckPassword.getView();
        etPwd2View.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkEqPassword(etPwdView.getText().toString(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etPwd2View.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) return;
            mPresenter.checkEqPassword(
                    etPwdView.getText().toString(),
                    etPwd2View.getText().toString()
            );
        });

        //注册
//        tvSignUpBtn.setOnClickListener(v -> mPresenter.signUp(
//                evName.getTextString(),
//                evEmail.getTextString(),
//                evPassword.getTextString(),
//                evCheckPassword.getTextString()
//        ));

        // 新注册
        tvSignUpBtn.setOnClickListener(v -> mPresenter.userRegister(
                evName.getTextString(),
                evEmail.getTextString(),
                evPassword.getTextString(),
                evCheckPassword.getTextString(),
                evMobileNum.getTextString(),
                evVerifyCode.getTextString()
        ));
    }

    private void onFinishOfResult(String id, String pwd) {
        // 注册成功
        StatisticalManage.get().registerEvent(id);

        Intent intent = new Intent();
        intent.putExtra(Constant.EMAIL, id);
        intent.putExtra(Constant.PASSWORD, pwd);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCallLoginText(Spanned s) {
        tvLoginBtn.setText(s);
    }

    @Override
    public void onCallShowNameTips(boolean isShow) {
        if (isShow) {
            evName.showTips();
        } else {
            evName.hideTips();
        }
    }

    @Override
    public void onCallShowEmailTips(boolean isShow) {
        if (isShow) {
            evEmail.showTips();
        } else {
            evEmail.hideTips();
        }
    }

    @Override
    public void onCallShowPasswordTips(boolean isShow) {
        if (isShow) {
            evPassword.showTips();
        } else {
            evPassword.hideTips();
        }
    }

    @Override
    public void onCallShowCheckPasswordTips(boolean isShow) {
        if (isShow) {
            evCheckPassword.showTips();
        } else {
            evCheckPassword.hideTips();
        }
    }

    @Override
    public void onCallSendSmsCodeResult(String phone, int errCode, String errMsg) {
        Resources res = getResources();
        if (errCode != 0) {
            mToastBuild.setTextColor(res.getColor(R.color.colorFailureToastText));
            mToastBuild.setBackgroundResource(R.drawable.shape_failure_toast_bg);

            if (errMsg != null) {
                showToast(errMsg, mToastBuild);
            }
        }
    }

    @Override
    public void onCallUserRegisterResult(String id, String pwd, String phone, boolean isSuccess, String errMsg) {
        Resources res = getResources();
        if (isSuccess) {
            mToastBuild.setTextColor(res.getColor(R.color.colorSuccessToastText));
            mToastBuild.setBackgroundResource(R.drawable.shape_success_toast_bg);
            showToast(R.string.stringEmailSignUpSuccessTips, mToastBuild);

            onFinishOfResult(id, pwd);
        } else {
            mToastBuild.setTextColor(res.getColor(R.color.colorFailureToastText));
            mToastBuild.setBackgroundResource(R.drawable.shape_failure_toast_bg);

            showToast(errMsg, mToastBuild);
        }
    }

    @Override
    public void onCallSignUpResult(String id, String pwd, boolean result, int errorCode) {
        Resources res = getResources();
        if (result) {
            mToastBuild.setTextColor(res.getColor(R.color.colorSuccessToastText));
            mToastBuild.setBackgroundResource(R.drawable.shape_success_toast_bg);
            showToast(R.string.stringEmailSignUpSuccessTips, mToastBuild);

            onFinishOfResult(id, pwd);
//            //打开登录页
//            StartUtil.startEmailLoginActivity( this, id, pwd );
        } else {
            mToastBuild.setTextColor(res.getColor(R.color.colorFailureToastText));
            mToastBuild.setBackgroundResource(R.drawable.shape_failure_toast_bg);
            //注册失败
            switch (errorCode) {
                case 1000:      //未知错误
                    showToast(R.string.stringEmailSignUpErrorTips, mToastBuild);
                    break;
                case 1101:      //账号已存在
                case 1108:      //用户已存在绑定信息
                    showToast(R.string.stringEmailSignUpAccExistTips, mToastBuild);
                    break;
            }
        }
    }

    @Override
    public void onCallAuthSignUp(boolean isAuth) {
        tvSignUpBtn.setEnabled(isAuth);
        tvSignUpBtn.setBackgroundResource(
                isAuth ? R.drawable.ic_touch_btn_true : R.drawable.ic_touch_btn_false
        );
    }
}
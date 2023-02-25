package com.gjjy.basiclib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.ContentWebView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * terms_policy_integral等介绍页面
 */
@Route(path = "/protocol/protocolActivity")
public class ProtocolActivity extends BaseActivity {
    private Toolbar tbToolbar;
    private ContentWebView cwvWebView;

    private String mUrl = null;
    private int mTitleResId = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        initIntent();
        initView();
        initData();
        initListener();
    }

    @Override
    public void onBackPressed() {
        if (cwvWebView != null && cwvWebView.canGoBack()) {
            cwvWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        int type = intent.getIntExtra(Constant.AGREEMENT_TYPE, -1);

        if (type == -1) {
            finish();
            return;
        }

        switch (type) {
            case 0:     //terms
                mUrl = Config.mTermsUrl;
                break;
            case 1:     //policy
                mUrl = Config.mPolicyUrl;
                break;
            case 2:     //integral
                mTitleResId = R.string.stringRules;
                mUrl = Config.mIntegralRulesUrl;
                break;
        }
    }

    private void initView() {
        tbToolbar = findViewById(R.id.protocol_tb_toolbar);
        cwvWebView = findViewById(R.id.protocol_cwv_content);
    }

    private void initData() {
        setStatusBarHeightForSpace(findViewById(R.id.toolbar_height_space));
        if (mTitleResId != 0) {
            tbToolbar.setTitle(mTitleResId);
        }

        if (!TextUtils.isEmpty(mUrl)) {
            cwvWebView.loadUrl(mUrl);
            return;
        }
        finish();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());
    }
}

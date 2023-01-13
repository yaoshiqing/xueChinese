package com.gjjy.steerlib.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.steerlib.R;
import com.gjjy.steerlib.StartUtil;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

/**
 * 是否需要登录页面
 */
public class NeedLoginFragment extends BaseFragment {
    private TextView tvLoginBtn;
    private TextView tvTalkLaterBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_need_login, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvLoginBtn = findViewById( R.id.need_login_tv_login_btn );
        tvTalkLaterBtn = findViewById( R.id.need_login_tv_talk_later_btn );
    }

    private void initData() {

    }

    private void initListener() {
        tvLoginBtn.setOnClickListener(v -> {
            StartUtil.startLoginActivity( getActivity(), PageName.GUEST_SIGN_IN );
            BuriedPointEvent.get().onWhetherLoginOfLogin(
                    getActivity(),
                    PageName.WHETHER_LOGIN
            );
        });

        tvTalkLaterBtn.setOnClickListener(v -> {
            BuriedPointEvent.get().onWhetherLoginOfLater(
                    getActivity(),
                    PageName.WHETHER_LOGIN
            );
            //1.2.4开通会员
            com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( getActivity() );
            finish();
        });
    }
}

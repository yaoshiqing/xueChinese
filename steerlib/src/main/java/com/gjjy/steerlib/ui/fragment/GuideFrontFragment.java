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
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.steerlib.R;
import com.gjjy.steerlib.StartUtil;
import com.gjjy.steerlib.ui.activity.GuideActivity;

/**
 * 引导页面
 */
public class GuideFrontFragment extends BaseFragment {
    private TextView tvNewStartBtn;
    private TextView tvLoginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide_front, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvNewStartBtn = findViewById( R.id.guide_front_tv_new_start_btn );
        tvLoginBtn = findViewById( R.id.guide_front_tv_login_btn );
    }

    private void initData() { }

    private void initListener() {
        tvNewStartBtn.setOnClickListener(v -> {
            GuideActivity activity = ((GuideActivity) getActivity());
            if( activity == null ) return;
            activity.onSelectedType( 0, 0 );
            BuriedPointEvent.get().onGuidePageOfFirstMeeting( getContext() );
        });

        tvLoginBtn.setOnClickListener(v -> {
            StartUtil.startLoginActivity( getActivity(), PageName.GUEST_SIGN_IN );
            BuriedPointEvent.get().onGuidePageOfHaveAccount( getContext() );
        });

    }
}

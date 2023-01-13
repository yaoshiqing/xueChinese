package com.gjjy.basiclib.ui.fragment;

import com.gjjy.basiclib.statistical.StatisticalManage;

public class BaseFragment extends com.ybear.baseframework.BaseFragment {
    @Override
    public void onResume() {
        super.onResume();
        StatisticalManage.get().onResumeOfFragment( getClass().getSimpleName() );
    }

    @Override
    public void onPause() {
        super.onPause();
        StatisticalManage.get().onPauseOfFragment( getClass().getSimpleName() );
    }
}

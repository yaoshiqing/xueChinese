package com.gjjy.frontlib.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.R;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.widget.NetworkErrorView;

/**
 * 空列表
 */
public class NotDataFragment extends BaseFragment {
    private NetworkErrorView nevNetworkError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_not_data, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        switchPage();
    }

    private void initView() {
        nevNetworkError = findViewById( R.id.not_data_nev_network_error );
    }

    private void initListener() {
        nevNetworkError.setOnRefreshClickListener(v -> setCallResult( DOMConstant.REFRESH_ANSWER ));
    }

    private int mAnswerType = -1;
    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        if( args == null ) return;
        mAnswerType = args.getInt( Constant.ANSWER_TYPE );
    }

    private void switchPage() {
        //正常，考试，跳级展示网络错误页面
        if( mAnswerType == Constant.AnswerType.NORMAL ||
                mAnswerType == Constant.AnswerType.TEST || mAnswerType ==
                Constant.AnswerType.SKIP_TEST ) {
            nevNetworkError.show();
        }
    }
}

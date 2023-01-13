package com.gjjy.frontlib.mvp.presenter;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.ybear.mvp.presenter.MvpPresenter;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.view.TestAnswerDescView;
import com.gjjy.basiclib.utils.Constant;

/**
 * P层 - 考试答题介绍页面
 */
public class TestAnswerDescPresenter extends MvpPresenter<TestAnswerDescView> {
    private long mUnitId;

    public TestAnswerDescPresenter(@NonNull TestAnswerDescView view) {
        super(view);
    }

    public void initIntent(Intent intent) {
        if( intent == null ) {
            callError();
            return;
        }
        //获取课程id
        mUnitId = intent.getLongExtra( Constant.ID_FOR_INT, -1 );
        if( mUnitId == -1 ) {
            callError();
            return;
        }
    }

    @UiThread
    private void callError() {
        TestAnswerDescView v = getView();
        if( v == null || v.getContext() == null ) return;
        //暂无数据
        v.oCallError( v.getContext().getResources().getString( R.string.stringNotData ) );
    }
}

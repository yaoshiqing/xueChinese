package com.gjjy.frontlib.mvp.view;

import android.view.MotionEvent;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.adapter.LabelDragAdapter;
import com.ybear.mvp.view.MvpViewable;

import java.util.List;

public interface LabelTranslateView extends MvpViewable {
    void dispatchTouchEvent(MotionEvent ev);
    void onCallTitle(String title);
    void onCallQuestion(String question);
    void onCallOptionsLayout(@OptionsLayout int layout, OptionsEntity... opts);
    List<LabelDragAdapter.ItemData> onCallDataList();
    void onCallOptionsError();
    void onCheckVisibility(int visibility);
}


package com.gjjy.frontlib.mvp.view;

import androidx.annotation.StringRes;

import com.ybear.mvp.view.MvpViewable;
import com.ybear.mvp.view.fragment.MvpFragment;
import com.gjjy.basiclib.utils.Constant;

import java.util.List;

public interface AnswerView extends MvpViewable {
    void onCallCombo(int num);
    void onCallPageSelected(int position, @StringRes int checkText, int visibility);
    void onCallError(String s);
    void onCallFragment(boolean isAdd, List<MvpFragment> list);
    void onCallAddFragment(int index, MvpFragment f);
    void onCallAnswerBarVisibility(int visibility);
    void onCallShowLoadingDialog(boolean isShow);
    void onCallWrongAnswer(boolean isSkip);
    void onCallCorrectAnswer(boolean isSkip);
    void onCallWrongAnswerOfModuleTest();
    void onCallAddWrongQuestionSet(int questionId, boolean isUpload);
    void onCallRemoveWrongQuestionSet(int questionId, boolean isUpload);
    void onCallWrongQuestionSetDaoResult(boolean result);
    void onCallNotAnswer(@Constant.AnswerType int type);
}
package com.gjjy.basiclib.mvp.model;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.dao.entity.SkipAnswerTypeEntity;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.basiclib.dao.sql.SetUpSQL;

public class SetUpModel extends MvpModel {
    @Model
    private UserModel mUserModel;
    @Model
    private SetUpSQL mSetUpSQL;

    @NonNull
    public SkipAnswerTypeEntity getSkipQuestion() { return mSetUpSQL.querySkipQuestion(); }

    public boolean isExistEnableSkip() {
        return isSkipHearing() || isSkipVoice() || isSkipTranslate();
    }

    public boolean isSkipHearing() { return !getSkipQuestion().getHearing(); }

    public boolean isSkipVoice() { return !getSkipQuestion().getVoice(); }

    public boolean isSkipTranslate() { return !getSkipQuestion().getTranslate(); }

    public boolean isSkipSound() { return !getSkipQuestion().getSound(); }

    public boolean isSkipSnail() { return !getSkipQuestion().getSnail(); }

    public void saveSkipQuestion(int position, boolean enable) {
        switch ( position ) {
            case 0:
                mSetUpSQL.updateSkipQuestionOfHearing( enable );
                break;
            case 1:
                mSetUpSQL.updateSkipQuestionOfVoice( enable );
                break;
            case 2:
                mSetUpSQL.updateSkipQuestionOfTranslate( enable );
                break;
            case 3:
                mSetUpSQL.updateSkipQuestionOfSound( enable );
                break;
            case 4:
                mSetUpSQL.updateSkipQuestionOfSnail( enable );
                break;
        }

        LogUtil.i("saveSkipQuestion -> " +
                "Pos:" + position + " | " +
                "Enable:" + enable
        );
    }
}

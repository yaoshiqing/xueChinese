package com.gjjy.basiclib.dao.sql;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.dao.SQLAction;
import com.gjjy.basiclib.dao.SkipAnswerTypeEntityDao;
import com.gjjy.basiclib.dao.entity.SkipAnswerTypeEntity;
import com.ybear.mvp.model.MvpModel;

public class SetUpSQL extends MvpModel {
    private final SkipAnswerTypeEntityDao mSkipAnswerTypeEntityDao;

    private SkipAnswerTypeEntity mSkipEntity;

    public SetUpSQL() {
        SQLAction sql = SQLAction.get();
        mSkipAnswerTypeEntityDao = sql.getSkipAnswerTypeEntityDao();
        mSkipEntity = mSkipAnswerTypeEntityDao.queryBuilder().unique();
        if( mSkipEntity == null ) {
            mSkipEntity = new SkipAnswerTypeEntity();
            mSkipAnswerTypeEntityDao.insertOrReplace( mSkipEntity );
        }
        restoreSetUp();
    }

    public void insertSkipQuestion(@NonNull SkipAnswerTypeEntity data) {
//        mSkipAnswerTypeEntityDao.deleteAll();
//        mSkipAnswerTypeEntityDao.insertOrReplace( data );
    }

    public void restoreSetUp() {
        mSkipEntity.init();
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }

    @NonNull
    public SkipAnswerTypeEntity querySkipQuestion() {
//        SkipAnswerTypeEntity data = mSkipAnswerTypeEntityDao.queryBuilder().unique();
//        if( data == null ) {
//            data = new SkipAnswerTypeEntity();
//            insertSkipQuestion( data );
//        }
//       return data;
        return mSkipEntity;
    }

    public void updateSkipQuestionOfHearing(boolean enable) {
//        SkipAnswerTypeEntity data = querySkipQuestion();
//        data.setHearing( enable );
//        mSkipAnswerTypeEntityDao.update( data );
        mSkipEntity.setHearing( enable );
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }

    public void updateSkipQuestionOfVoice(boolean enable) {
//        SkipAnswerTypeEntity data = querySkipQuestion();
//        data.setVoice( enable );
//        mSkipAnswerTypeEntityDao.update( data );
        mSkipEntity.setVoice( enable );
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }

    public void updateSkipQuestionOfTranslate(boolean enable) {
//        SkipAnswerTypeEntity data = querySkipQuestion();
//        data.setTranslate( enable );
//        mSkipAnswerTypeEntityDao.update( data );
        mSkipEntity.setTranslate( enable );
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }

    public void updateSkipQuestionOfSound(boolean enable) {
        mSkipEntity.setSound( enable );
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }

    public void updateSkipQuestionOfSnail(boolean enable) {
//        SkipAnswerTypeEntity data = querySkipQuestion();
//        data.setSnail( enable );
//        mSkipAnswerTypeEntityDao.update( data );
        mSkipEntity.setSnail( enable );
        mSkipAnswerTypeEntityDao.update( mSkipEntity );
    }
}

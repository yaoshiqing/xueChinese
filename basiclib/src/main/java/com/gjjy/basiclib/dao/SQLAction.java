package com.gjjy.basiclib.dao;

import android.app.Application;

import com.gjjy.basiclib.dao.SkipAnswerTypeEntityDao;
import com.gjjy.basiclib.dao.UserDetailEntityDao;

public class SQLAction {
    private final SQL mSQL;
    private SQLAction() {
        mSQL = new SQL();
    }
    public static SQLAction get() { return SQLAction.HANDLER.I; }
    private static final class HANDLER { private static final SQLAction I = new SQLAction(); }

    public void init(Application app) {
        mSQL.init(
                app,
                UserDetailEntityDao.class,
                SkipAnswerTypeEntityDao.class
        );
//        mSQL.getDaoSession().clear();
    }

    public UserDetailEntityDao getUserDetailEntityDao() {
        UserDetailEntityDao dao = mSQL.getDaoSession().getUserDetailEntityDao();
        dao.detachAll();
        return dao;
    }

    public SkipAnswerTypeEntityDao getSkipAnswerTypeEntityDao() {
        return mSQL.getDaoSession().getSkipAnswerTypeEntityDao();
    }
}

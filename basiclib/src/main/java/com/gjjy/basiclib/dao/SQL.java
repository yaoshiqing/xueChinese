package com.gjjy.basiclib.dao;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.dao.DaoMaster;
import com.gjjy.basiclib.dao.DaoSession;
import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

class SQL {
    private DaoSession mSession;

    @SafeVarargs
    final void init(@NonNull Application app, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        SuperOpenHelper helper = new SuperOpenHelper(
                app,
                "db_gjjy_xuechinese" + ".db",
                null,
                daoClasses
        );
        mSession = new DaoMaster( helper.getWritableDatabase() ).newSession();
    }

    @NonNull
    DaoSession getDaoSession() {
        if( mSession != null ) return mSession;
        throw new NullPointerException("Need to be init, You can call the init().");
    }

    /**
     * GreenDao升级助手
     */
    private static class SuperOpenHelper extends DaoMaster.OpenHelper {
        private final Class<? extends AbstractDao<?, ?>>[] mDaoClasses;

        @SafeVarargs
        SuperOpenHelper(@NonNull Context context,
                        @NonNull String name,
                        SQLiteDatabase.CursorFactory factory,
                        Class<? extends AbstractDao<?, ?>>... daoClasses) {
            super(context, name, factory);
            mDaoClasses = daoClasses;
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, mDaoClasses);
        }
    }
}
package com.gjjy.basiclib.dao.sql;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.basiclib.dao.UserDetailEntityDao;
import com.gjjy.basiclib.dao.SQLAction;

import java.util.List;

public class UserSQL extends MvpModel {
    private UserDetailEntityDao mDetailDao;

    public UserSQL() {
        SQLAction sql = SQLAction.get();
        mDetailDao = sql.getUserDetailEntityDao();
        mDetailDao.detachAll();
    }

    /**
     * 插入用户信息
     * @param data          用户信息
     */
    public void insertUserDetail(@NonNull UserDetailEntity data) {
        data.setIsDelete( false );
        mDetailDao.insertOrReplace( data );
    }

    /**
     * 更新用户信息
     * @param data          用户信息
     */
    public void updatedUserDetail(@NonNull UserDetailEntity data) {
//        mDetailDao.deleteByKey( mDetailDao.getKey( data ) );
//        insertUserDetail( data );
        mDetailDao.update( data );
    }

//    /**
//     * 查询用户信息
//     * @param uid           唯一id
//     * @return              查询结果
//     */
//    public UserDetailEntity queryUserDetail(String uid) {
//        UserDetailEntity data = null;
//        try {
//            data = mDetailDao.queryBuilder()
//                    .where( UserDetailEntityDao.Properties.Uid.eq( uid ) )
//                    .build()
//                    .uniqueOrThrow();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return data;
//    }

    /**
     * 查询用户信息
     * @return              查询结果
     */
    @NonNull
    public UserDetailEntity queryUserDetail() {
        UserDetailEntity data;
        List<UserDetailEntity> list = mDetailDao.queryBuilder()
                .where( UserDetailEntityDao.Properties.IsDelete.eq( false ) )
                .build()
                .list();
        if( list != null && list.size() > 0 ) {
            LogUtil.e("queryUserDetail -> size:" + list.size());
            data = list.get( list.size() - 1 );
            if( data.getIsDelete() ) {
                data.setUid( null );
                data.setToken( null );
            }
            LogUtil.e("queryUserDetail -> " + data);
            return data;
        }
        return new UserDetailEntity();
    }

    public boolean deleteUserDetail(UserDetailEntity data) {
//        mDetailDao.deleteByKey( data.getId() );
        data.setIsDelete( true );
        updatedUserDetail( data );
        mDetailDao.detachAll();
        return true;
    }
}
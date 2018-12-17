package com.api.demo.db.greendao;

import com.api.demo.db.User;
import com.ljy.devring.db.GreenTableManager;

import org.greenrobot.greendao.AbstractDao;

/**
 * author:  ljy
 * date:    2018/12/13
 * description: 用户表管理者 for GreenDao
 * DevRing中提供了GreenTableManager(数据表管理者基类)，继承它然后实现getDao方法，将GreenDao自动生成的对应XXXDao返回即可。
 */

public class UserGreenTableManager extends GreenTableManager<User, Long> {

    private DaoSession mDaoSession;

    public UserGreenTableManager(DaoSession daoSession) {
        this.mDaoSession = daoSession;
    }

    @Override
    public AbstractDao<User, Long> getDao() {
        return mDaoSession.getUserDao();
    }
}

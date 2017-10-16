package com.dev.base.model.db;

import android.content.Context;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * author:  ljy
 * date:    2017/10/16
 * description: 用于数据库初始化操作，提供DaoSession。
 */

public class DaoManager {
    private static final String DB_NAME = "test.db";
    private MySQLiteOpenHelper mSQLiteOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static Context mContext;

    public static DaoManager getInstance() {
        return DaoManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DaoManager instance = new DaoManager();
    }

    public DaoManager() {
    }

    public static void init(Context context) {
        mContext = context;
    }

    //获取DaoSession，从而获取各个表的操作DAO类
    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            initDataBase();
        }
        return mDaoSession;
    }

    private void initDataBase(){
        setDebugMode(true);//默认开启Log打印
        mSQLiteOpenHelper = new MySQLiteOpenHelper(mContext, DB_NAME, null);//建库
        mDaoMaster = new DaoMaster(mSQLiteOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        mDaoSession.clear();//清空所有数据表的缓存
    }

    public void setDebugMode(boolean flag) {
        MigrationHelper.DEBUG = true;//如果查看数据库更新的Log，请设置为true
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

}

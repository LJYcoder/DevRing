package com.dev.base.app;

import android.app.Application;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.dev.base.util.ActivityStackManager;
import com.dev.base.model.db.DaoMaster;
import com.dev.base.model.db.DaoSession;
import com.dev.base.model.db.MySQLiteOpenHelper;
import com.dev.base.model.net.RetrofitUtil;
import com.dev.base.util.CrashLogUtil;
import com.dev.base.util.FrescoUtil;
import com.dev.base.util.ToastUtil;
import com.dev.base.util.log.LogUtil;

/**
 * author:  ljy
 * date:    2017/9/13
 * description: 全局初始化操作
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private ActivityStackManager mStackManager;//activity堆栈管理
    private static DaoSession mDaoSession;//数据库操作管理

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        mStackManager = ActivityStackManager.getInstance();//初始化activity堆栈管理

        ToastUtil.init(this);//初始化吐司
        LogUtil.init(true);//初始化Log打印
        CrashLogUtil.getInstance().init(this);//初始化崩溃输出
        RetrofitUtil.init(this);//初始化retrofit
        FrescoUtil.getInstance().initializeFresco(this);//初始化Fresco

    }

    //初始化数据库
    private static void initDataBase() {
        MigrationHelper.DEBUG = true;//如果你想查看日志信息，请将DEBUG设置为true
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(instance, "test.db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    //获取DaoSession，从而获取各个表的操作DAO类
    public static DaoSession getDaoSession() {
        if (mDaoSession == null) {
            initDataBase();
        }
        return mDaoSession;
    }

    public ActivityStackManager getActivityStackManager() {
        return mStackManager;
    }

}

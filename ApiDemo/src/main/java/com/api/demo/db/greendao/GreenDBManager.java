package com.api.demo.db.greendao;

import android.support.v4.util.SimpleArrayMap;

import com.api.demo.db.User;
import com.ljy.devring.DevRing;
import com.ljy.devring.db.support.GreenOpenHelper;
import com.ljy.devring.db.support.IDBManager;
import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.db.support.MigrationHelper;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * author:  ljy
 * date:    2018/3/10
 * description:
 * 由于GreenDao的特殊性以及具体数据表的不确定，无法很好地集成到DevRing当中。
 * 所以需要实现IDBManager接口，并通过DevRing.configureDB()方法传入。
 * 1.在init()中对数据库进行初始化操作，如建库建表。
 * 2.在putTableManager()方法中将数据表管理者存进参数map中，请记清楚key值
 *   后面对数据表的操作是通过DevRing.tableManager(key)方法得到数据表管理者，然后进行相关增删改查。
 * 3.可在本类中添加IDBManager接口以外的方法
 * ，然后通过DevRing.<GreenDBManager>dbManager()来调用。
 *
 * https://www.jianshu.com/p/11bdd9d761e6
 */

public class GreenDBManager implements IDBManager {

    DaoSession mDaoSession;
    UserGreenTableManager mUserGreenTableManager;

    @Override
    public void init() {

        String dbName = "test_green.db";
        Integer dbVersion = DaoMaster.SCHEMA_VERSION;
        Class<? extends AbstractDao<?, ?>>[] classes = new Class[]{UserDao.class};

        //这里使用DevRing提供的GreenOpenHelper对DaoMaster进行初始化，这样就可以实现数据库升级时的数据迁移
        //默认的DaoMaster.OpenHelper不具备数据迁移功能，它会在数据库升级时将数据删除。
        GreenOpenHelper openHelper = new GreenOpenHelper(DevRing.application(), dbName, dbVersion, classes);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
//      DaoMaster daoMaster = new DaoMaster(greenOpenHelper.getEncryptedWritableDb("your_secret"));//加密

        mDaoSession = daoMaster.newSession();

        mUserGreenTableManager = new UserGreenTableManager(mDaoSession);

        //查看数据库更新版本时数据迁移的log
        MigrationHelper.DEBUG = false;
        //数据库增删改查时的log
        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
        //清空缓存
        mDaoSession.clear();
    }

    @Override
    public void putTableManager(SimpleArrayMap<Object, ITableManger> mapTables) {
        mapTables.put(User.class, mUserGreenTableManager);
    }

}

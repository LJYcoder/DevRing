package com.dev.base.mvp.model.db.greendao;

import android.support.v4.util.SimpleArrayMap;

import com.dev.base.di.component.other.DaggerDBComponent;
import com.dev.base.di.module.other.GreenDBModule;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.ljy.devring.DevRing;
import com.ljy.devring.db.support.IDBManager;
import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.db.support.MigrationHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;

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

    @Inject
    MovieGreenTableManager mMovieTableManager;
    @Inject
    DaoSession mDaoSession;

    @Override
    public void init() {
        /**
         * 建库，建表
         * 初始化mDaoSession与mMovieTableManager，具体初始化过程请查看 {@link GreenDBModule}
         * */
        DaggerDBComponent.builder().ringComponent(DevRing.ringComponent()).build().inject(this);

        //查看数据库更新版本时数据迁移的log
        MigrationHelper.DEBUG = true;
        //数据库增删改查时的log
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        clearAllTableCache();
    }

    @Override
    public void putTableManager(SimpleArrayMap<Object, ITableManger> mapTables) {
        mapTables.put(MovieCollect.class, mMovieTableManager);
    }

    public void clearAllTableCache() {
        mDaoSession.clear();
    }

    public DaoSession getSession() {
        return mDaoSession;
    }
}

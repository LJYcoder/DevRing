package com.ljy.devring.db.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 用于在数据库更新版本时，对旧数据进行迁移，避免数据丢失
 */

public class GreenOpenHelper extends DatabaseOpenHelper {

    Class<? extends AbstractDao<?, ?>>[] mDaoClasses;

    public GreenOpenHelper(Context context, String name, int version, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        super(context, name, version);
        this.mDaoClasses = daoClasses;
    }

    public GreenOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        super(context, name, factory, version);
        this.mDaoClasses = daoClasses;
    }

    @Override
    public void onCreate(Database db) {
        MigrationHelper.createAllTables(db, false, mDaoClasses);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                MigrationHelper.createAllTables(db, ifNotExists, mDaoClasses);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                MigrationHelper.dropAllTables(db, ifExists, mDaoClasses);
            }
        }, mDaoClasses);
    }

}

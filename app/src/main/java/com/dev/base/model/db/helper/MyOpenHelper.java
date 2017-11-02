package com.dev.base.model.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dev.base.model.db.DaoMaster;
import com.dev.base.model.db.MovieCollectDao;

import org.greenrobot.greendao.database.Database;

/**
 * author:  ljy
 * date:    2017/10/10
 * description: 用于在数据库更新版本时，对旧数据进行迁移，避免数据丢失
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        },  MovieCollectDao.class);
    }
}
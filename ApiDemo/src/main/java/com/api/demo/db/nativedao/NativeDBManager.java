package com.api.demo.db.nativedao;

import android.support.v4.util.SimpleArrayMap;

import com.api.demo.db.User;
import com.ljy.devring.DevRing;
import com.ljy.devring.db.support.IDBManager;
import com.ljy.devring.db.support.ITableManger;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 原生数据库的管理者。
 * 需要实现IDBManager接口，并通过DevRing.configureDB()方法传入
 * 1.在init()中对数据库进行初始化操作，如建库建表。
 * 2.在putTableManager()方法中将数据表管理者存进参数map中，请记清楚key值
 *   后面对数据表的操作是通过DevRing.tableManager(key)方法得到数据表管理者，然后进行相关增删改查。
 * 3.可在本类中添加IDBManager接口以外的方法
 * ，然后通过DevRing.<NativeDBManager>dbManager()来调用。
 *
 *
 * 仅仅是为了演示如何替换默认的GreenDao，并不建议使用原生数据库，可以对比发现需要自己写的代码量多了很多
 */

public class NativeDBManager implements IDBManager {

    UserNativeTableManager mUserNativeTableManager;

    @Override
    public void init() {
        String dbName = "test_native.db";
        Integer dbVersion = 1;
        NativeOpenHelper nativeOpenHelper = new NativeOpenHelper(DevRing.application(), dbName, null, dbVersion);
        mUserNativeTableManager = new UserNativeTableManager(nativeOpenHelper);
    }

    @Override
    public void putTableManager(SimpleArrayMap<Object, ITableManger> mapTables) {
        mapTables.put(User.class, mUserNativeTableManager);
    }
}

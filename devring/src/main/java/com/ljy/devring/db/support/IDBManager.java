package com.ljy.devring.db.support;

import android.support.v4.util.SimpleArrayMap;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 数据库管理者接口
 */

public interface IDBManager {

    void init();//进行初始化操作，如建库建表

    void putTableManager(SimpleArrayMap<Object, ITableManger> mapTables);//将各个表的管理者存进mapTables中

}

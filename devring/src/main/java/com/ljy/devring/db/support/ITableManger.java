package com.ljy.devring.db.support;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.List;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 表管理者接口
 */

public interface ITableManger<M, K> {

    boolean insertOne(@NotNull M m);//插入一个数据

    boolean insertSome(@NotNull List<M> list);//插入多个数据

    boolean insertOrReplaceOne(@NotNull M m);//插入一个数据，如果已存在则进行替换，根据主键来判断是否已存在

    boolean insertOrReplaceSome(@NotNull List<M> list);//插入多个数据，如果已存在则进行替换，根据主键来判断是否已存在

    boolean deleteOne(@NotNull M m);//删除一个数据

    boolean deleteSome(@NotNull List<M> list);//删除多个数据

    boolean deleteOneByKey(@NotNull K key);//根据主键删除一个数据

    boolean deleteSomeByKeys(@NotNull List<K> list);//根据主键删除多个数据

    boolean deleteAll();//删除表中所有数据

    boolean updateOne(@NotNull M m);//更新一个数据

    boolean updateSome(@NotNull List<M> list);//更新多个数据

    M loadOne(@NotNull K key);//根据主键获取一个数据

    List<M> loadAll();//获取全部数据

    long count();//获取表数据的数量

    List<M> queryBySQL(String sql, String[] selectionArgs);//条件查询

    boolean execSQL(String sql);//执行SQL语句

}

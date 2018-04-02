package com.dev.base.mvp.model.db.nativedao;

import android.support.v4.util.SimpleArrayMap;

import com.dev.base.di.component.other.DaggerDBComponent;
import com.dev.base.di.module.other.NativeDBModule;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.ljy.devring.DevRing;
import com.ljy.devring.db.support.IDBManager;
import com.ljy.devring.db.support.ITableManger;

import javax.inject.Inject;

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

    @Inject
    MovieNativeTableManager mMovieTableManager;

    @Override
    public void init() {
        /**
         * 建库，建表
         * 具体初始化过程请查看 {@link NativeDBModule}
         * */
        DaggerDBComponent.builder().ringComponent(DevRing.ringComponent()).build().inject(this);
    }

    @Override
    public void putTableManager(SimpleArrayMap<Object, ITableManger> mapTables) {
        mapTables.put(MovieCollect.class, mMovieTableManager);
    }
}

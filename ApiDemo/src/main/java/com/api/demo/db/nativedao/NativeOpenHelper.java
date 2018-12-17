package com.api.demo.db.nativedao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 原生数据库的OpenHelper
 * 仅仅是为了演示如何替换默认的GreenDao，不建议使用原生数据库，可以对比发现需要自己写的代码量多了很多
 */
public class NativeOpenHelper extends SQLiteOpenHelper {

    AtomicInteger mOpenCounter;

    public NativeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mOpenCounter = new AtomicInteger();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //建表
        new UserNativeTableManager(this).createTable(sqLiteDatabase);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        mOpenCounter.incrementAndGet();
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        mOpenCounter.incrementAndGet();
        return super.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

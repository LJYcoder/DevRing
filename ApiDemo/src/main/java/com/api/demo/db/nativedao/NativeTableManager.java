package com.api.demo.db.nativedao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.ljy.devring.db.support.ITableManger;
import com.ljy.devring.other.RingLog;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 基本的数据表管理者 for 原生数据库
 * 仅仅是为了演示如何替换默认的GreenDao，不建议使用原生数据库，可以对比发现需要自己写的代码量多了很多
 */

public abstract class NativeTableManager<M, K> implements ITableManger<M, K> {

    protected NativeOpenHelper mOpenHelper;
    protected String mTableName;

    public NativeTableManager(NativeOpenHelper openHelper, String tableName) {
        mOpenHelper = openHelper;
        mTableName = tableName;
    }

    public abstract void createTable(SQLiteDatabase sqLiteDatabase);

    public abstract String getPkName();

    public abstract K getPkValue(M m);

    public abstract ContentValues getContentValues(M m);

    public abstract List<ContentValues> getContentValuesList(List<M> list);

    public abstract M readCursor(Cursor cursor);

    public abstract List<M> readCursors(Cursor cursor);


    @Override
    public boolean insertOne(M m) {
        boolean isSuccess = false;

        if (m == null) {
            return isSuccess;
        }

        ContentValues contentValues = getContentValues(m);
        if (contentValues == null) {
            return isSuccess;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            isSuccess = db.insert(mTableName, null, contentValues) > 0;
            db.setTransactionSuccessful();
            return isSuccess;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean insertSome(List<M> list) {
        boolean isSuccess = false;

        List<ContentValues> contentValuesListList = getContentValuesList(list);
        if (contentValuesListList == null || contentValuesListList.isEmpty()) {
            return isSuccess;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (ContentValues values : contentValuesListList) {
                isSuccess = db.insert(mTableName, null, values) > 0;
            }
            db.setTransactionSuccessful();
            return isSuccess;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean insertOrReplaceOne(M m) {
        //方案一：
//        if (!updateOne(m)) {
//            return insertOne(m);
//        }
//        return false;

        //方案二：
        boolean isSuccess = false;

        if (m == null) {
            return isSuccess;
        }

        ContentValues contentValues = getContentValues(m);
        if (contentValues == null) {
            return isSuccess;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            isSuccess = db.replace(mTableName, null, contentValues) > 0;
            db.setTransactionSuccessful();
            return isSuccess;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean insertOrReplaceSome(List<M> list) {
        //方案一：
//        if (!updateSome(list)) {
//            return updateSome(list);
//        }
//        return false;

        //方案二：
        boolean isSuccess = false;

        List<ContentValues> contentValuesListList = getContentValuesList(list);
        if (contentValuesListList == null || contentValuesListList.isEmpty()) {
            return isSuccess;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (ContentValues values : contentValuesListList) {
                isSuccess = db.replace(mTableName, null, values) > 0;
            }
            db.setTransactionSuccessful();
            return isSuccess;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean deleteOne(M m) {
        return deleteOneByKey(getPkValue(m));
    }

    @Override
    public boolean deleteSome(List<M> list) {
        List<K> listKey = new ArrayList<>();
        for (M m : list) {
            listKey.add(getPkValue(m));
        }
        return deleteSomeByKeys(listKey);
    }

    @Override
    public boolean deleteOneByKey(K key) {
        boolean isSuccess = false;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            isSuccess = db.delete(mTableName, getPkName() + "=?", new String[]{String.valueOf(key)}) > 0;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean deleteSomeByKeys(List<K> list) {
        boolean isSuccess = false;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (K key : list) {
                isSuccess = db.delete(mTableName, getPkName() + "=?", new String[]{String.valueOf(key)}) > 0;
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean deleteAll() {
        return execSQL("DELETE FROM '" + mTableName + "'");
    }

    @Override
    public boolean updateOne(M m) {
        boolean isSuccess = false;

        if (m == null) {
            return isSuccess;
        }

        ContentValues contentValues = getContentValues(m);
        if (contentValues == null) {
            return isSuccess;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            isSuccess = db.update(mTableName, contentValues, getPkName() + "=?", new String[]{String.valueOf(getPkValue(m))}) > 0;
            db.setTransactionSuccessful();
            return isSuccess;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

    @Override
    public boolean updateSome(List<M> list) {
        boolean isSuccess = false;

        for (M m : list) {
            isSuccess = updateOne(m);
        }
        return isSuccess;
    }

    @Override
    public M loadOne(K key) {
        M m = null;
        String sql = "select * from " + mTableName + "where " + getPkName() + "=?";
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(key)});
            m = cursor != null ? readCursor(cursor) : null;
        } catch (SQLiteException e) {
            RingLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mOpenHelper.close();
        }
        return m;
    }

    @Override
    public List<M> loadAll() {
        List<M> list = null;
        String sql = "select * from " + mTableName;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            list = cursor != null ? readCursors(cursor) : null;
        } catch (SQLiteException e) {
            RingLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mOpenHelper.close();
        }
        return list;
    }

    @Override
    public long count() {
        int count = 0;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String sql = "select count(*) from " + mTableName;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (SQLiteException e) {
            RingLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mOpenHelper.close();
        }
        return count;
    }

    @Override
    public List<M> queryBySQL(String sql, String[] selectionArgs) {
        List<M> list = null;
        Cursor cursor = null;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        try {
            cursor = db.rawQuery(sql, selectionArgs);
            list = cursor != null ? readCursors(cursor) : null;
        } catch (SQLiteException e) {
            RingLog.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mOpenHelper.close();
        }
        return list;
    }

    @Override
    public boolean execSQL(String sql) {
        boolean isSuccess = false;

        if (TextUtils.isEmpty(sql)) {
            return isSuccess;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (SQLException e) {
            RingLog.e(e);
        } finally {
            db.endTransaction();
            mOpenHelper.close();
        }
        return isSuccess;
    }

}

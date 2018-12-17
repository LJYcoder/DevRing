package com.api.demo.db.nativedao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.api.demo.db.User;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 用户表管理者 for 原生数据库
 * 仅仅是为了演示如何替换默认的GreenDao，不建议使用原生数据库，可以对比发现需要自己写的代码量多了很多
 */

public class UserNativeTableManager extends NativeTableManager<User, Long> {

    public static final String USER_TABLE_NAME = "USER";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";

    public UserNativeTableManager(NativeOpenHelper openHelper) {
        super(openHelper, USER_TABLE_NAME);
    }

    @Override
    public void createTable(SQLiteDatabase sqLiteDatabase) {
        if ((sqLiteDatabase == null) || !sqLiteDatabase.isOpen()) {
            return;
        }
        StringBuilder sql = new StringBuilder("create table if not exists ");
        sql.append(USER_TABLE_NAME).append(" (");
        sql.append(ID).append(" integer primary key autoincrement not null,");
        sql.append(NAME).append(" varchar,");
        sql.append(AGE).append(" integer);");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public String getPkName() {
        return ID;
    }

    @Override
    public Long getPkValue(User user) {
        return user.getId();
    }

    @Override
    public ContentValues getContentValues(User user) {
        if(user==null) return null;

        ContentValues values = new ContentValues();
        values.clear();
        values.put(ID, user.getId());
        values.put(NAME, user.getName());
        values.put(AGE, user.getAge());

        return values;
    }

    @Override
    public List<ContentValues> getContentValuesList(List<User> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (User user : list) {
            values = new ContentValues();
            values.clear();
            values.put(ID, user.getId());
            values.put(NAME, user.getName());
            values.put(AGE, user.getAge());
            valuesList.add(values);
        }

        return valuesList;
    }

    @Override
    public User readCursor(Cursor cursor) {
        User entity = null;

        int id = cursor.getColumnIndexOrThrow(ID);
        int name = cursor.getColumnIndexOrThrow(NAME);
        int age = cursor.getColumnIndexOrThrow(AGE);

        if (cursor.moveToFirst()) {
            entity = new User();
            entity.setId(cursor.getLong(id));
            entity.setName(cursor.getString(name));
            entity.setAge(cursor.getInt(age));
        }

        return entity;
    }

    @Override
    public List<User> readCursors(Cursor cursor) {
        List<User> list = new ArrayList<>();

        int id = cursor.getColumnIndexOrThrow(ID);
        int name = cursor.getColumnIndexOrThrow(NAME);
        int age = cursor.getColumnIndexOrThrow(AGE);

        while (cursor.moveToNext()) {
            User entity = new User();
            entity.setId(cursor.getLong(id));
            entity.setName(cursor.getString(name));
            entity.setAge(cursor.getInt(age));
            list.add(entity);
        }

        return list;
    }
}


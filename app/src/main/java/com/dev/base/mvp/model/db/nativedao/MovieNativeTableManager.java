package com.dev.base.mvp.model.db.nativedao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dev.base.mvp.model.entity.table.MovieCollect;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 电影数据表管理者 for 原生数据库
 * 仅仅是为了演示如何替换默认的GreenDao，不建议使用原生数据库，可以对比发现需要自己写的代码量多了很多
 */

public class MovieNativeTableManager extends NativeTableManager<MovieCollect, Long> {

    public static final String MOVIE_COLLECT_TABLE_NAME = "native_movie_collect";
    public static final String ID = "id";
    public static final String MOVIE_IMAGE = "movie_image";
    public static final String TITLE = "title";
    public static final String YEAR = "year";

    public MovieNativeTableManager(NativeOpenHelper openHelper) {
        super(openHelper, MOVIE_COLLECT_TABLE_NAME);
    }

    @Override
    protected void createTable(SQLiteDatabase sqLiteDatabase) {
        if ((sqLiteDatabase == null) || !sqLiteDatabase.isOpen()) {
            return;
        }
        StringBuilder sql = new StringBuilder("create table if not exists ");
        sql.append(MOVIE_COLLECT_TABLE_NAME).append(" (");
        sql.append(ID).append(" integer primary key autoincrement not null,");
        sql.append(MOVIE_IMAGE).append(" varchar,");
        sql.append(TITLE).append(" varchar,");
        sql.append(YEAR).append(" varchar);");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    protected String getPkName() {
        return ID;
    }

    @Override
    protected Long getPkValue(MovieCollect movieCollect) {
        return movieCollect.getId();
    }

    @Override
    protected ContentValues getContentValues(MovieCollect movieCollect) {
        if(movieCollect==null) return null;

        ContentValues values = new ContentValues();
        values.clear();
        values.put(ID, movieCollect.getId());
        values.put(MOVIE_IMAGE, movieCollect.getMovieImage());
        values.put(TITLE, movieCollect.getTitle());
        values.put(YEAR, movieCollect.getYear());

        return values;
    }

    @Override
    protected List<ContentValues> getContentValuesList(List<MovieCollect> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (MovieCollect movieCollect : list) {
            values = new ContentValues();
            values.clear();
            values.put(ID, movieCollect.getId());
            values.put(MOVIE_IMAGE, movieCollect.getMovieImage());
            values.put(TITLE, movieCollect.getTitle());
            values.put(YEAR, movieCollect.getYear());
            valuesList.add(values);
        }

        return valuesList;
    }

    @Override
    protected MovieCollect readCursor(Cursor cursor) {
        MovieCollect entity = null;

        int id = cursor.getColumnIndexOrThrow(ID);
        int movieImage = cursor.getColumnIndexOrThrow(MOVIE_IMAGE);
        int title = cursor.getColumnIndexOrThrow(TITLE);
        int year = cursor.getColumnIndexOrThrow(YEAR);

        if (cursor.moveToFirst()) {
            entity = new MovieCollect();
            entity.setId(cursor.getLong(id));
            entity.setMovieImage(cursor.getString(movieImage));
            entity.setTitle(cursor.getString(title));
            entity.setYear(cursor.getString(year));
        }

        return entity;
    }

    @Override
    protected List<MovieCollect> readCursors(Cursor cursor) {
        List<MovieCollect> list = new ArrayList<>();

        int id = cursor.getColumnIndexOrThrow(ID);
        int movieImage = cursor.getColumnIndexOrThrow(MOVIE_IMAGE);
        int title = cursor.getColumnIndexOrThrow(TITLE);
        int year = cursor.getColumnIndexOrThrow(YEAR);

        while (cursor.moveToNext()) {
            MovieCollect entity = new MovieCollect();
            entity.setId(cursor.getLong(id));
            entity.setMovieImage(cursor.getString(movieImage));
            entity.setTitle(cursor.getString(title));
            entity.setYear(cursor.getString(year));
            list.add(entity);
        }

        return list;
    }
}


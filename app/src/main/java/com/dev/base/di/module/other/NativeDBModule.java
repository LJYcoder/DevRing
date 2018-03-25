package com.dev.base.di.module.other;

import android.app.Application;

import com.dev.base.di.qualifier.NativeDB;
import com.dev.base.di.scope.DBScope;
import com.dev.base.mvp.model.db.nativedao.MovieNativeTableManager;
import com.dev.base.mvp.model.db.nativedao.NativeOpenHelper;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/16
 * description:  对NavieDBManager中的相关变量进行初始化
 */
@Module
public class NativeDBModule {

    @DBScope
    @Provides
    NativeOpenHelper nativeOpenHelper(Application application, @NativeDB String dbName, @NativeDB Integer schemaVersion) {
        return new NativeOpenHelper(application, dbName, null, schemaVersion);
    }

    @NativeDB
    @DBScope
    @Provides
    String dbName() {
        return "test_native.db";
    }

    @NativeDB
    @DBScope
    @Provides
    Integer schemaVersion() {
        return 1;
    }

    @DBScope
    @Provides
    MovieNativeTableManager movieTableManager(NativeOpenHelper nativeOpenHelper) {
        return new MovieNativeTableManager(nativeOpenHelper);
    }

}

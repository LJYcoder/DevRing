package com.dev.base.di.module.other;

import android.app.Application;

import com.dev.base.di.qualifier.GreenDB;
import com.dev.base.di.scope.DBScope;
import com.dev.base.mvp.model.db.greendao.DaoMaster;
import com.dev.base.mvp.model.db.greendao.DaoSession;
import com.dev.base.mvp.model.db.greendao.MovieCollectDao;
import com.dev.base.mvp.model.db.greendao.MovieGreenTableManager;
import com.ljy.devring.db.support.GreenOpenHelper;

import org.greenrobot.greendao.AbstractDao;

import dagger.Module;
import dagger.Provides;

/**
 * author:  ljy
 * date:    2018/3/10
 * description: 对GreenDBManager中的相关变量进行初始化
 */
@Module
public class GreenDBModule {

    @DBScope
    @Provides
    DaoSession daoSession(DaoMaster daoMaster) {
        return daoMaster.newSession();
    }

    @DBScope
    @Provides
    DaoMaster daoMaster(GreenOpenHelper greenOpenHelper) {
        //这里使用DevRing提供的GreenOpenHelper对DaoMaster进行初始化，这样就可以实现数据库升级时的数据迁移
        //默认的DaoMaster.OpenHelper不具备数据迁移功能，它会在数据库升级时将数据删除。
        return new DaoMaster(greenOpenHelper.getWritableDatabase());
//        return new DaoMaster(greenOpenHelper.getEncryptedWritableDb("your_secret"));
    }

    @DBScope
    @Provides
    GreenOpenHelper greenOpenHelper(Application context, @GreenDB String dbName, @GreenDB Integer schemaVersion, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        return new GreenOpenHelper(context, dbName, schemaVersion, daoClasses);
    }

    @GreenDB
    @DBScope
    @Provides
    String dbName() {
        return "test_greendao.db";
    }

    @GreenDB
    @DBScope
    @Provides
    Integer schemaVersion() {
        //返回数据库版本号，使用DaoMaster.SCHEMA_VERSION即可
        return DaoMaster.SCHEMA_VERSION;
    }

    @DBScope
    @Provides
    Class<? extends AbstractDao<?, ?>>[] daoClasses() {
        //传入各数据表对应的Dao类
        return new Class[]{MovieCollectDao.class};
    }

    @DBScope
    @Provides
    MovieGreenTableManager movieTableManager(DaoSession daoSession) {
        return new MovieGreenTableManager(daoSession);
    }

}

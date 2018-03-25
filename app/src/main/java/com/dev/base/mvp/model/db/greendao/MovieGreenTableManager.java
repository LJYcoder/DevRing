package com.dev.base.mvp.model.db.greendao;

import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.ljy.devring.db.GreenTableManager;

import org.greenrobot.greendao.AbstractDao;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 电影数据表管理者 for GreenDao
 * DevRing中提供了GreenTableManager(基本的数据表管理者)，继承它然后实现getDao方法，将GreenDao自动生成的对应XXXDao返回即可。
 */

public class MovieGreenTableManager extends GreenTableManager<MovieCollect, Long> {

    private DaoSession mDaoSession;

    public MovieGreenTableManager(DaoSession daoSession) {
        mDaoSession = daoSession;
    }

    @Override
    public AbstractDao<MovieCollect, Long> getDao() {
        return mDaoSession.getMovieCollectDao();
    }
}


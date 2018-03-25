package com.dev.base.mvp.model;

import com.dev.base.mvp.model.entity.event.CollectCountEvent;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.imodel.ICollectModel;
import com.ljy.devring.DevRing;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * author:  ljy
 * date:    2018/3/21
 * description: “我的收藏”的model层，进行相关的数据处理与提供
 */

public class CollectModel implements ICollectModel {

    //从“电影收藏”表中获取所有收藏的电影
    @Override
    public Observable getAllCollect() {
        return Observable.create(new ObservableOnSubscribe<List<MovieCollect>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MovieCollect>> emitter) throws Exception {
                List<MovieCollect> list = DevRing.tableManager(MovieCollect.class).loadAll();
//                DevRing.<GreenTableManager>tableManager(MovieCollect.class).clearCache();//清空该表缓存数据以确保数据最新
//                List<MovieCollect> list= DevRing.<GreenTableManager>tableManager(MovieCollect.class).queryBuilder().list();
                emitter.onNext(list);
            }
        });
    }

    //从“电影收藏”表中删除某部电影
    @Override
    public void deleteFromMyCollect(MovieCollect movieCollect) {
        DevRing.tableManager(MovieCollect.class).deleteOne(movieCollect);
    }


    //从“电影收藏”表中获取收藏的数量
    @Override
    public int getCollectCount() {
        return (int) DevRing.tableManager(MovieCollect.class).count();
    }

    //更新侧滑栏菜单项的收藏数量
    @Override
    public void updateMenuCollectCount() {
        DevRing.busManager().postEvent(new CollectCountEvent(getCollectCount()));
    }
}

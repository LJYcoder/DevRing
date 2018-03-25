package com.dev.base.mvp.model;

import com.dev.base.mvp.model.entity.event.CollectCountEvent;
import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.http.MovieApiService;
import com.dev.base.mvp.model.imodel.IMovieMoel;
import com.ljy.devring.DevRing;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:  豆瓣电影的model层。进行相关的数据处理与提供
 */

public class MovieModel implements IMovieMoel{

    /**
     * 获取正在上映的电影
     *
     * @param start            请求的起始点
     * @param count            获取的电影数量
     */
    @Override
    public Observable getPlayingMovie(int start,int count) {
        return DevRing.httpManager().getService(MovieApiService.class).getPlayingMovie(start, count);
    }

    /**
     * 获取即将上映的电影
     *
     * @param start            请求的起始点
     * @param count            获取的电影数量
     */
    @Override
    public Observable getCommingMovie(int start,int count) {
        return DevRing.httpManager().getService(MovieApiService.class).getCommingMovie(start, count);
    }

    //加入某部电影到“电影收藏”表
    @Override
    public void addToMyCollect(MovieCollect movieCollect) {
        DevRing.tableManager(MovieCollect.class).insertOrReplaceOne(movieCollect);
    }

    //更新侧滑栏菜单项的收藏数量
    @Override
    public void updateMenuCollectCount() {
        int count = (int) DevRing.tableManager(MovieCollect.class).count();
        DevRing.busManager().postEvent(new CollectCountEvent(count));
    }

}

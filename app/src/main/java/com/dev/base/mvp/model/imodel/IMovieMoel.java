package com.dev.base.mvp.model.imodel;

import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.imodel.base.IBaseModel;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/21
 * description:
 */

public interface IMovieMoel extends IBaseModel {
    Observable getPlayingMovie(int start, int count);

    Observable getCommingMovie(int start, int count);

    void addToMyCollect(MovieCollect movieCollect);

    void updateMenuCollectCount();

}

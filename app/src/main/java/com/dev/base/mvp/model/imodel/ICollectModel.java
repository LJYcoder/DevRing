package com.dev.base.mvp.model.imodel;

import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.model.imodel.base.IBaseModel;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/21
 * description:
 */

public interface ICollectModel extends IBaseModel {

    Observable getAllCollect();

    void deleteFromMyCollect(MovieCollect movieCollect);

    int getCollectCount();

    void updateMenuCollectCount();
}

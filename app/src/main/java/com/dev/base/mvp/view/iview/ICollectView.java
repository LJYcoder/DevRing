package com.dev.base.mvp.view.iview;

import com.dev.base.mvp.model.entity.table.MovieCollect;
import com.dev.base.mvp.view.iview.base.IBaseView;

import java.util.List;

/**
 * author:  ljy
 * date:    2018/3/21
 * description:
 */

public interface ICollectView extends IBaseView {

    void getCollectSuccess(List<MovieCollect> list);

}

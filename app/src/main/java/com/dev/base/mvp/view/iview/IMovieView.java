package com.dev.base.mvp.view.iview;

import com.dev.base.mvp.model.entity.res.MovieRes;
import com.dev.base.mvp.view.iview.base.IBaseView;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:
 */

public interface IMovieView extends IBaseView {
    void getMovieSuccess(List<MovieRes> list, int type);

    void getMovieFail(int status, String desc, int type);

}

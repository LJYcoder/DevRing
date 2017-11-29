package com.dev.base.presenter.iview;

import com.dev.base.model.entity.res.MovieRes;

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

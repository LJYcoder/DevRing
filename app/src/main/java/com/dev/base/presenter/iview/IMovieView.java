package com.dev.base.presenter.iview;

import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.presenter.base.IBaseView;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:
 */

public interface IMovieView extends IBaseView {
    void getMovieSuccess(List<MovieRes> list);

    void getMovieFail(int status, String desc);
}

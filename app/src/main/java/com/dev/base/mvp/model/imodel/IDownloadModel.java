package com.dev.base.mvp.model.imodel;

import com.dev.base.mvp.model.imodel.base.IBaseModel;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:
 */

public interface IDownloadModel extends IBaseModel {

    Observable downloadFile();

}

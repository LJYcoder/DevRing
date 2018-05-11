package com.dev.base.mvp.model;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.http.DownloadApiService;
import com.dev.base.mvp.model.imodel.IDownloadModel;
import com.ljy.devring.DevRing;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: 下载文件的model层，进行相关的数据处理与提供
 */

public class DownloadModel implements IDownloadModel {

    /**
     * 下载文件
     * @return 返回下载文件的请求
     */
    @Override
    public Observable downloadFile() {
        return DevRing.httpManager().getService(DownloadApiService.class).downloadFile(UrlConstants.DOWNLOAD);
    }



}

package com.dev.base.mvp.view.iview;

import com.dev.base.mvp.view.iview.base.IBaseView;
import com.ljy.devring.http.support.body.ProgressInfo;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:
 */

public interface IDownloadView extends IBaseView {
    void onDownloading(ProgressInfo progressInfo);

    void onDownloadSuccess(String filePath);

    void onDownloadFail(long progressInfoId, String errMsg);
}

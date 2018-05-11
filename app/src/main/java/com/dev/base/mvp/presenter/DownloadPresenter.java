package com.dev.base.mvp.presenter;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.imodel.IDownloadModel;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.iview.IDownloadView;
import com.ljy.devring.DevRing;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.http.support.observer.DownloadObserver;
import com.ljy.devring.http.support.throwable.HttpThrowable;
import com.ljy.devring.util.RxLifecycleUtil;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: 下载文件的Presenter层
 */

public class DownloadPresenter extends BasePresenter<IDownloadView, IDownloadModel> {

    public DownloadPresenter(IDownloadView iView, IDownloadModel iModel) {
        super(iView, iModel);
    }

    DownloadObserver mDownloadObserver;

    /**
     * 下载文件
     * @param file 下载内容将保存到该目标文件
     */
    public void downloadFile(File file) {
        //不为空则不重新构造DownloadObserver，避免创造了多个进度监听回调
        if (mDownloadObserver == null) {
            //DownloadObserver构造函数传入要要监听的下载地址
            mDownloadObserver = new DownloadObserver(UrlConstants.DOWNLOAD) {
                @Override
                public void onResult(boolean isSaveSuccess, String filePath) {
                    if (mIView != null) {
                        if (isSaveSuccess) {
                            mIView.onDownloadSuccess(filePath);
                        }else {
                            mIView.onDownloadFail(0,"save file fail");
                        }
                    }
                }

                @Override
                public void onError(long progressInfoId, HttpThrowable throwable) {
                    if (mIView != null) {
                        mIView.onDownloadFail(progressInfoId, throwable.message);
                    }
                }

                @Override
                public void onProgress(ProgressInfo progressInfo) {
                    if (mIView != null) {
                        mIView.onDownloading(progressInfo);
                    }
                }
            };
        }
        DevRing.httpManager().downloadRequest(file, mIModel.downloadFile(), mDownloadObserver, RxLifecycleUtil.bindUntilDestroy(mIView));
    }
}

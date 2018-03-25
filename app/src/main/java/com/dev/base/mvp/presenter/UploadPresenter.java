package com.dev.base.mvp.presenter;

import android.content.Intent;
import android.net.Uri;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.imodel.IUploadModel;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.iview.IUploadView;
import com.ljy.devring.DevRing;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.http.support.observer.UploadObserver;
import com.ljy.devring.util.RxLifecycleUtil;

import java.io.File;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: 上传文件的Presenter层
 */

public class UploadPresenter extends BasePresenter<IUploadView, IUploadModel> {

    public UploadPresenter(IUploadView iView, IUploadModel iModel) {
        super(iView, iModel);
    }

    public File handlePhoto(int reqCode, Intent intent, Uri photoUri) {
        return mIModel.handlePhoto(reqCode, intent, photoUri, mIView.getActivity());
    }

    UploadObserver mUploadObserver;
    public void uploadFile(File file) {
        //不为空则不重新构造UploadObserver，避免创造了多个进度监听回调
        if (mUploadObserver == null) {
            //UploadObserver构造函数传入要要监听的上传地址
            mUploadObserver = new UploadObserver(UrlConstants.UPLOAD) {
                @Override
                public void onResult(Object result) {
                    if (mIView != null) {
                        mIView.onUploadSuccess();
                    }
                }

                @Override
                public void onError(long progressInfoId, String errMessage) {
                    if (mIView != null) {
                        mIView.onUploadFail(progressInfoId, errMessage);
                    }
                }

                @Override
                public void onProgress(ProgressInfo progressInfo) {
                    if (mIView != null) {
                        mIView.onUploading(progressInfo);
                    }
                }
            };
        }
        DevRing.httpManager().uploadRequest(mIModel.uploadFile(file), mUploadObserver, RxLifecycleUtil.bindUntilDestroy(mIView));
    }
}

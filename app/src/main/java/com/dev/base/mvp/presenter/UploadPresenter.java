package com.dev.base.mvp.presenter;

import android.content.Intent;

import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.imodel.IUploadModel;
import com.dev.base.mvp.presenter.base.BasePresenter;
import com.dev.base.mvp.view.iview.IUploadView;
import com.ljy.devring.DevRing;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.http.support.observer.UploadObserver;
import com.ljy.devring.http.support.throwable.HttpThrowable;
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

    public void getImageFromCamera() {
        mIModel.getImageFromCamera(mIView.getActivity());
    }

    public void getImageFromAlbums() {
        mIModel.getImageFromAlbums(mIView.getActivity());
    }

    public void cropImage(int reqCode, Intent intent) {
        mIModel.cropImage(mIView.getActivity(), reqCode, intent);
    }

    public File getUploadFile() {
        return mIModel.getUploadFile();
    }

    public File getUploadFile(int reqCode, Intent intent) {
        return mIModel.getUploadFile(mIView.getActivity(), reqCode, intent);
    }

    public void deleteTempFile() {
        mIModel.deleteTempFile(mIView.getActivity());
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
                public void onError(long progressInfoId, HttpThrowable throwable) {
                    if (mIView != null) {
                        mIView.onUploadFail(progressInfoId, throwable.message);
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

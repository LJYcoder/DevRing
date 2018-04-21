package com.dev.base.mvp.view.iview;

import android.app.Activity;

import com.dev.base.mvp.view.iview.base.IBaseView;
import com.ljy.devring.http.support.body.ProgressInfo;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:
 */

public interface IUploadView extends IBaseView {

    Activity getActivity();

    void onUploading(ProgressInfo progressInfo);

    void onUploadSuccess();

    void onUploadFail(long progressInfoId, String errMsg);
}

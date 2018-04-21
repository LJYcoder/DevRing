package com.dev.base.mvp.model.imodel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.dev.base.mvp.model.imodel.base.IBaseModel;

import java.io.File;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:
 */

public interface IUploadModel extends IBaseModel {

    Observable uploadFile(File file);

    File handlePhoto(int reqCode, Intent intent, Uri photoUri, Activity activity);

    void deleteTempFile(Activity activity);
}

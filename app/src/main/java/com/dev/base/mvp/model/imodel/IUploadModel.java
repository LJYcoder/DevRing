package com.dev.base.mvp.model.imodel;

import android.app.Activity;
import android.content.Intent;

import com.dev.base.mvp.model.imodel.base.IBaseModel;

import java.io.File;

import io.reactivex.Observable;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:
 */

public interface IUploadModel extends IBaseModel {

    void getImageFromCamera(Activity activity);

    void getImageFromAlbums(Activity activity);

    void cropImage(Activity activity, int reqCode, Intent intent);

    File getUploadFile(Activity activity, int reqCode, Intent intent);

    File getUploadFile();

    void deleteTempFile(Activity activity);

    Observable uploadFile(File file);
}

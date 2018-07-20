package com.dev.base.mvp.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.dev.base.R;
import com.dev.base.app.constant.UrlConstants;
import com.dev.base.mvp.model.http.UploadApiService;
import com.dev.base.mvp.model.imodel.IUploadModel;
import com.dev.base.util.CommonUtil;
import com.ljy.devring.DevRing;
import com.ljy.devring.util.FileUtil;
import com.ljy.devring.util.ImageUtil;
import com.ljy.devring.util.RingToast;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author:  ljy
 * date:    2018/3/23
 * description: 上传页面的model层，进行相关的数据处理与提供
 */

public class UploadModel implements IUploadModel {

    private File mDirectoryTemp;
    private File mFileTempCamera;
    private File mFileUpload;
    private Bitmap mBitmap;

    public UploadModel(Context context) {
        mDirectoryTemp = FileUtil.getDirectory(FileUtil.getExternalCacheDir(context), "upload_image");
    }


    @Override
    public void getImageFromCamera(Activity activity) {
        String fileNameTempCamera = "temp_camera_" + CommonUtil.getRandomString(5) + ".jpg";
        mFileTempCamera = FileUtil.getFile(mDirectoryTemp, fileNameTempCamera);
        ImageUtil.getImageFromCamera(activity, FileUtil.getUriForFile(activity, mFileTempCamera));
    }

    @Override
    public void getImageFromAlbums(Activity activity) {
        ImageUtil.getImageFromAlbums(activity);
    }

    @Override
    public void cropImage(Activity activity, int reqCode, Intent intent) {
        Uri photoUri = null;
        switch (reqCode) {
            case ImageUtil.REQ_PHOTO_ALBUM:
                if (intent == null) {
                    RingToast.show(R.string.operate_fail);
                    return;
                }
                photoUri = intent.getData();
                break;

            case ImageUtil.REQ_PHOTO_CAMERA:
                photoUri = FileUtil.getUriForFile(activity, mFileTempCamera);
                break;
        }
        String fileNameUpload = "temp_upload_" + CommonUtil.getRandomString(5) + ".jpg";
        mFileUpload = FileUtil.getFile(mDirectoryTemp, fileNameUpload);
        ImageUtil.cropImage(activity, 800, 800, photoUri, mFileUpload);
    }

    @Override
    public File getUploadFile() {
        return mFileUpload;
    }

    @Override
    public File getUploadFile(Activity activity, int reqCode, Intent intent) {
        String filePath = null;

        switch (reqCode) {
            case ImageUtil.REQ_PHOTO_ALBUM:
                Uri photoUri;
                if (intent != null && intent.getData() != null) {
                    photoUri = intent.getData();
                } else {
                    RingToast.show(R.string.operate_fail);
                    return null;
                }
                // 根据uri从系统数据库中取得该图片的路径
                String[] pojo = {MediaStore.MediaColumns.DATA};
                Cursor cursor = activity.getContentResolver().query(photoUri, pojo, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                    cursor.moveToFirst();
                    filePath = cursor.getString(columnIndex);
                    try {
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //少部分机型cursor会为空，则采用以下方式获取路径。
                else {
                    filePath = photoUri.getPath();
                }
                break;

            case ImageUtil.REQ_PHOTO_CAMERA:
                filePath = mFileTempCamera.getAbsolutePath();
                break;
        }

        // 回收清空bitmap，节省内存
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mBitmap = ImageUtil.fileToBitmap(filePath);
        if (mBitmap != null) {
            //尺寸压缩图片为800*800尺寸
//            mBitmap = ImageUtil.sizeCompress(mBitmap, 800, 800);
            // 进行旋转调整，否则部分手机得到的图片的方向会不对。
            Matrix matrix = new Matrix();
            matrix.preRotate(ImageUtil.getFixRotate(filePath));
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

            //保存图片到文件
            String fileNameUpload = "temp_upload_" + CommonUtil.getRandomString(5) + ".jpg";
            File fileUpload = FileUtil.getFile(mDirectoryTemp, fileNameUpload);
            //质量压缩Bitmap不大于3M，并保存到指定文件
            if (ImageUtil.qualityCompress(mBitmap, 3 * 1024, fileUpload)) {
                mBitmap.recycle();
                return fileUpload;
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public void deleteTempFile(Activity activity) {
        //将临时保存的图片文件删除
        FileUtil.deleteFile(FileUtil.getDirectory(FileUtil.getExternalCacheDir(activity), "upload_image"), false);
    }

    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 上传文件请求
     */
    @Override
    public Observable uploadFile(File file) {
        return DevRing.httpManager().getService(UploadApiService.class).upLoadFile(UrlConstants.UPLOAD, RequestBody.create(MediaType.parse("multipart/form-data"), file));
    }
}

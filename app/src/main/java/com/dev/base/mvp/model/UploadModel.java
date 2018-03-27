package com.dev.base.mvp.model;

import android.app.Activity;
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

    private String mFilePath;
    private Bitmap mBitmap;

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 上传文件请求
     */
    @Override
    public Observable uploadFile(File file) {
        return DevRing.httpManager().getService(UploadApiService.class).upLoadFile(UrlConstants.UPLOAD, RequestBody.create(MediaType.parse("multipart/form-data"), file));
    }

    //处理相机或相册返回的相片数据，保存到本地文件并返回
    @Override
    public File handlePhoto(int reqCode, Intent intent, Uri photoUri, Activity activity) {
        // 从相册取图片，有些手机有异常情况，请注意
        if (reqCode == ImageUtil.REQ_PHOTO_ALBUM) {
            if (intent == null) {
                RingToast.show(R.string.operate_fail);
                return null;
            }
            photoUri = intent.getData();
            if (photoUri == null) {
                RingToast.show(R.string.operate_fail);
                return null;
            }
        }

        // 根据uri从系统数据库中取得该图片的路径
        String[] pojo = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            mFilePath = cursor.getString(columnIndex);
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 回收清空bitmap，节省内存
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        //裁减图片为500*500尺寸，并且压缩图片为不大于3M的大小（为了更好地查看上传过程，这里就不进行压缩了）
//        mBitmap = ImageUtil.qualityCompress(ImageUtil.scaleCompress(mFilePath, 500, 500), 3 * 1024);
        mBitmap = ImageUtil.fileToBitmap(mFilePath);
        // 进行旋转，否则得到的图片可能会方向不对。
        Matrix matrix = new Matrix();
        matrix.preRotate(ImageUtil.fixDirection(mFilePath));
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        //保存图片到文件
        String imageName = "avatar_" + CommonUtil.getRandomString(5) + ".jpg";
        File filePhoto = FileUtil.getFile(FileUtil.getDirectory(FileUtil.getExternalCacheDir(activity),"upload_image"), imageName);
        if (ImageUtil.saveBitmapToFile(mBitmap, filePhoto)) {
            mBitmap.recycle();
            return filePhoto;
        }
        return null;
    }

    @Override
    public void deleteTempFile(Activity activity) {
        //将临时保存的图片文件删除
        FileUtil.deleteFile(FileUtil.getDirectory(FileUtil.getExternalCacheDir(activity), "upload_image"));
    }
}

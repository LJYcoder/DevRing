package com.dev.base.mvp.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.base.R;
import com.dev.base.di.component.activity.DaggerUploadActivityComponent;
import com.dev.base.di.module.activity.UploadActivityModule;
import com.dev.base.mvp.presenter.UploadPresenter;
import com.dev.base.mvp.view.activity.base.BaseActivity;
import com.dev.base.mvp.view.iview.IUploadView;
import com.dev.base.mvp.view.widget.MaterialDialog;
import com.dev.base.mvp.view.widget.PhotoPopWindow;
import com.dev.base.util.SystemTypeUtil;
import com.ljy.devring.DevRing;
import com.ljy.devring.http.support.body.ProgressInfo;
import com.ljy.devring.other.permission.PermissionListener;
import com.ljy.devring.util.ImageUtil;
import com.ljy.devring.util.RingToast;
import com.ljy.devring.util.RxLifecycleUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;

/**
 * author:  ljy
 * date:    2018/3/23
 * description:  上传页面
 */

public class UploadActivity extends BaseActivity<UploadPresenter> implements View.OnClickListener, IUploadView {

    @BindView(R.id.base_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_select_photo)
    ImageView mIvPhoto;
    @BindView(R.id.btn_upload)
    Button mBtnUpload;
    @BindView(R.id.pb_upload)
    ProgressBar mPbUpload;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.tv_length)
    TextView mTvLength;

    @BindString(R.string.upload)
    String mStrTitle;
    @BindString(R.string.operate_fail)
    String mStrOperateFail;
    @BindString(R.string.permission_request)
    String mStrPermission;

    @Inject
    PhotoPopWindow mPhontoPopWindow;
    @Inject
    MaterialDialog mPermissionDialog;

    private Uri mPhotoUri;//用于选择图片
    private File mFilePhoto;//选择完的图片将保存在此File中

    @Override
    protected int getContentLayout() {
        return R.layout.activity_upload;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //使用Dagger2对本类中相关变量进行初始化
        DaggerUploadActivityComponent.builder().uploadActivityModule(new UploadActivityModule(this)).build().inject(this);

        mToolbar.setTitle("");
        this.setSupportActionBar(mToolbar);
        mToolbar.setTitle(mStrTitle);

        mPermissionDialog.setMessage(mStrPermission);

        DevRing.imageManager().loadRes(R.mipmap.ic_image_load, mIvPhoto);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvent() {
        //点击toolbar左侧的返回图标则结束页面
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPermissionDialog.setPositiveButton("前往授权", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPermissionDialog.dismiss();
                SystemTypeUtil.goToPermissionManager(UploadActivity.this);
            }
        });
        mPermissionDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPermissionDialog.dismiss();
            }
        });

        mIvPhoto.setOnClickListener(this);
        mBtnUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_select_photo:
                //申请必要权限
                DevRing.permissionManager().requestEachCombined(this, new PermissionListener() {
                    @Override
                    public void onGranted(String permissionName) {
                        //全部权限都被授予的话，则弹出底部选项
                        mPhontoPopWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
                    }

                    @Override
                    public void onDenied(String permissionName) {
                        //如果用户拒绝了其中一个授权请求，则提醒用户
                        RingToast.show(mStrPermission);
                    }

                    @Override
                    public void onDeniedWithNeverAsk(String permissionName) {
                        //如果用户拒绝了其中一个授权请求，且勾选了不再提醒，则需要引导用户到权限管理页面开启
                        mPermissionDialog.show();
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;

            case R.id.btn_item_camera:
                mPhontoPopWindow.dismiss();
                mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                ImageUtil.getImageFromCamera(this, mPhotoUri);
                break;

            case R.id.btn_item_album:
                mPhontoPopWindow.dismiss();
                ImageUtil.getImageFromAlbums(this);
                break;

            case R.id.btn_upload:
                if (mFilePhoto != null && mFilePhoto.exists()) {
                    //手动终止未完成的上传请求
                    RxLifecycleUtil.getActivityLifeSubject(this.toString()).onNext(ActivityEvent.DESTROY);
                    //开启新的上传请求
                    mPresenter.uploadFile(mFilePhoto);
                } else {
                    RingToast.show(R.string.select_photo_first);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            RingToast.show(mStrOperateFail);
            return;
        }
        switch (requestCode) {
            case ImageUtil.REQ_PHOTO_CAMERA:
            case ImageUtil.REQ_PHOTO_ALBUM:
                mFilePhoto = mPresenter.handlePhoto(requestCode, data, mPhotoUri);
                if (mFilePhoto != null) {
                    DevRing.imageManager().loadFile(mFilePhoto, mIvPhoto);
                }else{
                    RingToast.show(mStrOperateFail);
                }
                break;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    //上传进度回调
    @Override
    public void onUploading(ProgressInfo progressInfo) {
        mPbUpload.setProgress(progressInfo.getPercent());
        mTvSpeed.setText("" + progressInfo.getSpeed() / 1024 + " KB/s");
        mTvLength.setText("" + progressInfo.getContentLength() / 1024 + " KB");
        if (progressInfo.isFinish()) {
            RingToast.show(R.string.upload_into_request_body_success);
            mTvSpeed.setText("");
            mTvLength.setText("");
        }
    }

    @Override
    public void onUploadSuccess() {
        //这里不可能回调，因为要上传成功还需要七牛云平台的token，本例子仅演示上传文件到请求实体，而不是到服务器中。
    }

    @Override
    public void onUploadFail(long progressInfoId, String errMsg) {
        if (progressInfoId != 0) {
            //上传文件至请求实体的过程中发生异常，一般时读写过程出错，重试即可
            //手动终止未完成的上传请求也会回调这里
            mPbUpload.setProgress(0);
        } else {
            //成功上传文件至请求实体，但因为缺少必要参数（七牛云平台的token），所以请求结果会失败 {"error":"token not specified"}。
        }

        mTvSpeed.setText("");
        mTvLength.setText("");
    }

    @Override
    protected void onDestroy() {
        //页面销毁时，将临时保存的图片文件删除
        mPresenter.deleteTempFile();
        super.onDestroy();
    }
}

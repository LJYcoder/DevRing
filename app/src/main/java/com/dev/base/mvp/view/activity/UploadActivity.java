package com.dev.base.mvp.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.dev.base.mvp.view.widget.PhotoDialogFragment;
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
    PhotoDialogFragment mPhotoDialogFragment;
    @Inject
    MaterialDialog mPermissionDialog;

    private File mFileUpload;//选择完的图片将保存在此File中

    @Override
    protected int getContentLayout() {
        return R.layout.activity_upload;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //使用Dagger2对本类中相关变量进行初始化
        //如果提示找不到DaggerUploadActivityComponent类，请重新编译下项目。
        DaggerUploadActivityComponent.builder().uploadActivityModule(new UploadActivityModule(this)).build().inject(this);

        //如果经过了配置变化而重建(如横竖屏切换)，且tag为photo的DialogFragment不为空，则不使用新建的DialogFragment。
        //不做此操作的话而使用新建的DialogFragment的话，会导致“弹出菜单栏---> 配置变化(如横竖屏切换)---> 点击菜单项触发dissmiss() ---> 空指针异常”
        //java.lang.NullPointerException: Attempt to invoke virtual method 'android.app.FragmentTransaction android.app.FragmentManager.beginTransaction()' on a null object reference
        //因为新建的DialogFragment还未通过show方法进行FragmentTransaction的add、commit操作。
        if (savedInstanceState != null && getFragmentManager().findFragmentByTag("photo") != null) {
            mPhotoDialogFragment = (PhotoDialogFragment) getFragmentManager().findFragmentByTag("photo");
        }

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
                SystemTypeUtil.goPermissionPage(UploadActivity.this);
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
                        mPhotoDialogFragment.show(getFragmentManager(), "photo");
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
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                //小Tips：
                //android6.0权限申请中，只要某个权限授予了，那么与它在同一个权限组里的其他权限都会自动授权.
                //android8.0后规则有所改动，某个权限授予后，与它在同一个权限组里的其他权限并不会自动授权
                //https://blog.csdn.net/yanzhenjie1003/article/details/76719487
                break;

            case R.id.btn_item_camera:
                mPhotoDialogFragment.dismiss();
                mPresenter.getImageFromCamera();

                break;

            case R.id.btn_item_album:
                mPhotoDialogFragment.dismiss();
                mPresenter.getImageFromAlbums();
                break;

            case R.id.btn_upload:
                if (mFileUpload != null && mFileUpload.exists()) {
                    //手动终止未完成的上传请求
                    RxLifecycleUtil.getActivityLifeSubject(this.toString()).onNext(ActivityEvent.DESTROY);
                    //开启新的上传请求
                    mPresenter.uploadFile(mFileUpload);
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
//                mFileUpload = mPresenter.getUploadFile(requestCode, data);
//                if (mFileUpload != null) {
//                    DevRing.imageManager().loadFile(mFileUpload, mIvPhoto);
//                } else {
//                    RingToast.show(mStrOperateFail);
//                }

                mPresenter.cropImage(requestCode, data);
                break;

            case ImageUtil.REQ_PHOTO_CROP:
                mFileUpload = mPresenter.getUploadFile();
                if (mFileUpload != null) {
                    DevRing.imageManager().loadFile(mFileUpload, mIvPhoto);
                } else {
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

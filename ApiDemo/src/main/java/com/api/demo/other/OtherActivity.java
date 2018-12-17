package com.api.demo.other;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.api.demo.R;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.other.RingLog;
import com.ljy.devring.other.permission.PermissionListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/14
 * description: 其他模块
 * <p>
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 */

public class OtherActivity extends AppCompatActivity implements IBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        ButterKnife.bind(this);
        setTitle("其他模块");
    }

    @OnClick({R.id.btn_request_permission1, R.id.btn_request_permission2})
    protected void onClick(View view) {
        switch (view.getId()) {
            //请求权限方法1：请求的每个权限的选择结果，最后都会回调
            case R.id.btn_request_permission1:
                DevRing.permissionManager().requestEach(this, new PermissionListener() {
                    @Override
                    public void onGranted(String permissionName) {
                        RingLog.e("允许了权限：" + permissionName);
                    }

                    @Override
                    public void onDenied(String permissionName) {
                        RingLog.e("拒绝了权限：" + permissionName);
                    }

                    @Override
                    public void onDeniedWithNeverAsk(String permissionName) {
                        RingLog.e("拒绝了权限，且勾选了不再提醒：" + permissionName);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;

            //请求权限方法2：请求的所有权限最后只会回调一次结果
            case R.id.btn_request_permission2:
                DevRing.permissionManager().requestEachCombined(this, new PermissionListener() {
                    @Override
                    public void onGranted(String permissionName) {
                        RingLog.e("全部权限都被允许了：" + permissionName);
                    }

                    @Override
                    public void onDenied(String permissionName) {
                        RingLog.e("至少一个权限被拒绝了：" + permissionName);
                    }

                    @Override
                    public void onDeniedWithNeverAsk(String permissionName) {
                        RingLog.e("至少一个权限被拒绝了，且勾选了不再提醒：" + permissionName);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;
        }
    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public boolean isUseFragment() {
        return false;
    }
}

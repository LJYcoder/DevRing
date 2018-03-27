package com.ljy.devring.other;

import android.app.Activity;

import com.ljy.devring.other.permission.PermissionListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * author:  ljy
 * date:    2018/3/16
 * description: 权限管理者
 */

public class PermissionManager {

    //请求单个权限建议用这个
    public void requestEach(Activity activity, final PermissionListener listener, String... permissions) {
        if (activity != null) {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        // `permission.name` is granted !
                        if (listener != null) {
                            listener.onGranted(permission.name);
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                        if (listener != null) {
                            listener.onDenied(permission.name);
                        }
                    } else {
                        // Denied permission with ask never again
                        // Need to go to the settings
                        if (listener != null) {
                            listener.onDeniedWithNeverAsk(permission.name);
                        }
                    }
                }
            });
        }
    }

    //请求多个权限建议用这个
    public void requestEachCombined(Activity activity, final PermissionListener listener, String... permissions) {
        if (activity != null) {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.requestEachCombined(permissions).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        // All permissions are granted !
                        if (listener != null) {
                            listener.onGranted(permission.name);
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // At least one denied permission without ask never again
                        if (listener != null) {
                            listener.onDenied(permission.name);
                        }
                    } else {
                        // At least one denied permission with ask never again
                        // Need to go to the settings
                        if (listener != null) {
                            listener.onDeniedWithNeverAsk(permission.name);
                        }
                    }
                }
            });
        }
    }
}

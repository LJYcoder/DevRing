package com.dev.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * 6.0权限申请工具类
 * 出自http://blog.csdn.net/u011106915/article/details/76458448?locationNum=6&fps=1
 *
 * 申请流程：
 * 1.在 AndroidManifest.xml 添加权限声明。
 *
 * 2.使用 checkSelfPermission 检查某个权限是否已经申请。
 *
 * 3.权限未申请，使用 requestPermissions 申请权限，然后会回调onRequestPermissionsResult。
 *
 * 4.在 onRequestPermissionsResult 回调中判断权限是否申请成功。
 *
 * 5.申请失败使用 shouldShowRequestPermissionRationale 判断用户是否勾选了 "不再提醒"。
 *   勾选了的话，弹出一个 Dialog 引导用户到设置界面授予权限。
 *   没勾选的话，可以什么都不做，也可以弹出弹出一个 Dialog 引导用户到设置界面授予权限。
 *
 */
public class PermissionUtil {
    /**
     * 是否需要检查权限
     */
    private static boolean needCheckPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取sd存储卡读写权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getExternalStoragePermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 获取拍照权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getCameraPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    /**
     * 获取麦克风权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getAudioPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    /**
     * 获取定位权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getLocationPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    /**
     * 获取读取联系人权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getContactsPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.READ_CONTACTS);
    }
    /**
     * 获取发送短信权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getSendSMSPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.SEND_SMS);
    }
    /**
     * 获取拨打电话权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getCallPhonePermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.CALL_PHONE);
    }


    /**
     * 传入权限，检查并返回其中未成功获取的权限。
     */
    public static List<String> getDeniedPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return null;
        }
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            return deniedPermissions;
        }

        return null;
    }

    /**
     * 是否拥有传入的所有权限
     */
    public static boolean hasPermissons(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对于传入的权限，如果用户点击了拒绝并且勾选“不再询问”，则返回true，此时可以弹出消息提示，引导用户跳转到权限管理页面.
     * 如果用户点击了拒绝但没有勾选“不再询问”，则返回false.
     */
    public static boolean deniedRequestAgain(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return false;
        }
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        for (String permission : deniedPermissions) {
            //没授权
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED) {

                //勾选了“不再询问”
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 打开app详细设置界面
     * 在 onActivityResult() 中没有必要对 resultCode 进行判断，因为用户只能通过返回键才能回到我们的 App 中，
     * 所以 resultCode 总是为 RESULT_CANCEL，所以不能根据返回码进行判断。
     * 在 onActivityResult() 中还需要对权限进行判断，因为用户有可能没有授权就返回了！
     */
    public static void startApplicationDetailsSettings(@NonNull Activity activity, int requestCode) {
        Toast.makeText(activity, "点击权限，并打开全部权限", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);


    }

    /**
     * 申请权限
     * 使用onRequestPermissionsResult方法，实现回调结果或者自己普通处理
     *
     * @return 是否已经获取权限
     */
    public static boolean requestPerssions(Activity activity, int requestCode, String... permissions) {

        if (!needCheckPermission()) {
            return true;
        }

        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        if (deniedPermissions != null) {
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            //返回结果onRequestPermissionsResult
            return false;
        }else {
            return true;
        }
    }

    /**
     * 申请权限返回方法
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults, @NonNull OnRequestPermissionsResultCallbacks callBack) {
        // Make a collection of granted and denied permissions from the request.
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (null != callBack) {
            if (!granted.isEmpty()) {
                callBack.onPermissionsGranted(requestCode, granted, denied.isEmpty());
            }
            if (!denied.isEmpty()) {
                callBack.onPermissionsDenied(requestCode, denied, granted.isEmpty());
            }
        }


    }


    /**
     * 申请权限返回
     */
//    public interface OnRequestPermissionsResultCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {
    public interface OnRequestPermissionsResultCallbacks {

        /**
         * @param isAllGranted 是否全部同意
         */
        void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted);

        /**
         * @param isAllDenied 是否全部拒绝
         */
        void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied);

    }
}
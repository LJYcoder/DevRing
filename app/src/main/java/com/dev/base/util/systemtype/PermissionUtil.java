package com.dev.base.util.systemtype;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.dev.base.BuildConfig;

/**
 * author:  ljy
 * date:    2017/7/25
 * description:
 */

public class PermissionUtil {

    public static void goToPermissionManager(Context context) {
        if (AndroidRomUtil.isFlyme()) {
//            ToastUtil.show("我是魅族");
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(getAppDetailSettingIntent(context));
            }
        } else if (AndroidRomUtil.isMIUI()) {
//            ToastUtil.show("我是小米");
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.setComponent(componentName);
            intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(getAppDetailSettingIntent(context));
            }
        } else if (AndroidRomUtil.isEMUI()) {
//            ToastUtil.show("我是华为");
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
                intent.setComponent(comp);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(getAppDetailSettingIntent(context));
            }
        }
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }
}

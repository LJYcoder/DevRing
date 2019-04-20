package com.ljy.devring.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * author:  ljy
 * date:    2017/10/20
 * description: 与手机系统类型相关的工具类
 * 比如：判断手机类型、跳转到权限管理页面、设置状态栏中图文的颜色模式(深色或亮色)
 */

public class SystemTypeUtil {

    public static final int REQ_CODE_PERMISSION = 123;

    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";


    /**
     * 是否为华为手机
     *
     * @return
     */
    public static boolean isEMUI() {
        try {
            return getProperty(KEY_EMUI_API_LEVEL, null) != null || getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null || getProperty(KEY_EMUI_VERSION, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 是否为小米手机
     *
     * @return
     */
    public static boolean isMIUI() {
        try {
            return getProperty(KEY_MIUI_VERSION_CODE, null) != null || getProperty(KEY_MIUI_VERSION_NAME, null) != null || getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 是否为魅族手机
     *
     * @return
     */
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }


    private static String getProperty(String name, String defaultValue) throws IOException {
        //Android 8.0以下可通过访问build.prop文件获取相关属性，8.0及以上无法访问，需采用反射获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            return properties.getProperty(name, defaultValue);
        }else {
            try {
                Class<?> clz = Class.forName("android.os.SystemProperties");
                Method get = clz.getMethod("get", String.class, String.class);
                String property = (String) get.invoke(clz, name, defaultValue);
                if(TextUtils.isEmpty(property)) return null;
                else return property;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return defaultValue;
        }
    }

    //跳转到权限管理页面，兼容不同手机系统类型
    public static void goPermissionPage(Activity context) {
        if (isFlyme()) {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            try {
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
            }
        } else if (isMIUI()) {
            try {
                // 高版本MIUI 访问的是PermissionsEditorActivity，如果不存在再去访问AppPermissionsEditorActivity
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.setComponent(componentName);
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                try {
                    // 低版本MIUI
                    Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.setComponent(componentName);
                    intent.putExtra("extra_pkgname", context.getPackageName());
                    context.startActivityForResult(intent, REQ_CODE_PERMISSION);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
                }
            }
        } else if (isEMUI()) {
            Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            try {
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
            }
        } else {
            context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
        }
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

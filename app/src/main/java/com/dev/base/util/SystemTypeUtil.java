package com.dev.base.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dev.base.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * ljy 2017/10/20
 * 与手机系统类型相关的工具类
 * 如：判断手机类型、跳转到权限管理页面、设置状态栏中图文的颜色模式(深色或亮色)
 */
public class SystemTypeUtil {

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";


    /**
     * 是否为华为手机
     * @return
     */
    public static boolean isEMUI() {
        try {
            return getProperty(KEY_EMUI_VERSION_CODE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 是否为小米手机
     * @return
     */
    public static boolean isMIUI() {
        try {
            return getProperty(KEY_MIUI_VERSION_CODE, null) != null
                  || getProperty(KEY_MIUI_VERSION_NAME, null) != null
                  || getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 是否为魅族手机
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


    public static String getProperty(String name, String defaultValue) throws IOException{
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        return properties.getProperty(name, defaultValue);
    }


    //跳转到权限管理页面，兼容不同手机系统类型
    public static void goToPermissionManager(Context context) {
        if (isFlyme()) {
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
        } else if (isMIUI()) {
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
        } else if (isEMUI()) {
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

    //设置状态栏中图标、字体的颜色模式（深色模式/亮色模式）
    //只有魅族（Flyme4+），小米（MIUI6+），android（6.0+）可以设置
    public static boolean setStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (MIUISetStatusBarLightMode(window, isDark)) {
                result = true;
            } else if (FlymeSetStatusBarLightMode(window, isDark)) {
                result = true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Android6SetStatusBarLightMode(window, isDark);
                result = true;
            }
        }
        return result;
    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    public static boolean MIUISetStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void Android6SetStatusBarLightMode(Window window, boolean isDark) {
        if (isDark) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}

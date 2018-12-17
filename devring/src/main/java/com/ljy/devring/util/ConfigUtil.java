package com.ljy.devring.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * author:  ljy
 * date:    2018/7/20
 * description:
 */

public class ConfigUtil {

    /**
     * 获取屏幕宽高，会加上虚拟按键栏的长度
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];
        if (Build.VERSION.SDK_INT >= 17) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(dm);
            size[0] = dm.widthPixels;  // 屏幕宽
            size[1] = dm.heightPixels;  // 屏幕高
        } else {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            @SuppressWarnings("rawtypes") Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked") Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            size[0] = dm.widthPixels;  // 屏幕宽
            size[1] = dm.heightPixels;  // 屏幕高
        }
        return size;
    }

    /**
     * 获取屏幕的长边（即竖屏下的高，横屏下的宽）
     */
    public static int getScreenLongSide(Context context) {
        int[] screenSize = getScreenSize(context);
        return Math.max(screenSize[0], screenSize[1]);
    }

    /**
     * 获取屏幕的短边（即竖屏下的宽，横屏下的高）
     */
    public static int getScreenShortSide(Context context) {
        int[] screenSize = getScreenSize(context);
        return Math.min(screenSize[0], screenSize[1]);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (hasNavigationBar(context)) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 判断是否由虚拟按键
     */
    public static boolean hasNavigationBar(Context context) {
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean hasNavBarFun = false;
        if (id > 0) {
            hasNavBarFun = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String)m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavBarFun = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavBarFun = true;
            }
        } catch (Exception e) {
            hasNavBarFun = false;
        }
        return hasNavBarFun;
    }

    /**
     * 判断当前应用是否是debug模式
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}

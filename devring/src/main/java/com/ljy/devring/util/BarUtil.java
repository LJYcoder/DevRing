package com.ljy.devring.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zack on 17-5-14.
 * 导航栏/状态栏颜色设置工具类
 * modify ljy
 */

public class BarUtil {

    private static final int TYPE_COLOR = 1;

    private static final int TYPE_TRANSPARENT = 2;

    private static final int TYPE_IMMERSION = 3;

    private static final int TYPE_DRAWER = 4;

    private static final int TYPE_HIDE = 5;

    private Activity activity;

    private int type;

    public BarUtil(Activity activity) {
        this.activity = activity;
    }

    private ColorBuilder colorBuilder;

    private BarUtil(Activity activity, ColorBuilder builder) {
        this.activity = activity;
        this.type = TYPE_COLOR;
        this.colorBuilder = builder;
    }

    private TransparentBuilder transparentBuilder;

    private BarUtil(Activity activity, TransparentBuilder builder) {
        this.activity = activity;
        this.type = TYPE_TRANSPARENT;
        this.transparentBuilder = builder;
    }

    private ImmersionBuilder immersionBuilder;

    private BarUtil(Activity activity, ImmersionBuilder builder) {
        this.activity = activity;
        this.type = TYPE_IMMERSION;
        this.immersionBuilder = builder;
    }

    private DrawerBuilder drawerBuilder;

    private BarUtil(Activity activity, DrawerBuilder builder) {
        this.activity = activity;
        this.type = TYPE_DRAWER;
        this.drawerBuilder = builder;
    }

    private HideBuilder hideBuilder;

    private BarUtil(Activity activity, HideBuilder builder) {
        this.activity = activity;
        this.type = TYPE_HIDE;
        this.hideBuilder = builder;
    }

    public static ColorBuilder newColorBuilder() {
        return new ColorBuilder();
    }

    public static TransparentBuilder newTransparentBuilder() {
        return new TransparentBuilder();
    }

    public static ImmersionBuilder newImmersionBuilder() {
        return new ImmersionBuilder();
    }

    public static DrawerBuilder newDrawerBuilder() {
        return new DrawerBuilder();
    }

    public static HideBuilder newHideBuilder() {
        return new HideBuilder();
    }

    public void apply() {
        if (type == TYPE_COLOR) {
            setColorBar(colorBuilder.statusColor, colorBuilder.statusAlpha, colorBuilder.applyNav, colorBuilder.navColor, colorBuilder.navAlpha);
        } else if (type == TYPE_TRANSPARENT) {
            setTransparentBar(transparentBuilder.statusColor, transparentBuilder.statusAlpha, transparentBuilder.applyNav, transparentBuilder.navColor, transparentBuilder
                    .navAlpha);
        } else if (type == TYPE_IMMERSION) {
            setTransparentBar(Color.TRANSPARENT, 0, immersionBuilder.applyNav, Color.TRANSPARENT, 0);
        } else if (type == TYPE_DRAWER) {
            setColorBarForDrawer(drawerBuilder.statusColor, drawerBuilder.statusAlpha, drawerBuilder.applyNav, drawerBuilder.navColor, drawerBuilder.navAlpha);
        } else if (type == TYPE_HIDE) {
            setHideBar(hideBuilder.applyNav);
        }
    }

    /**
     * StatusBar and navigationBar color
     *
     * @param statusColor StatusBar color
     * @param statusDepth StatusBar color depth
     * @param navColor    NavigationBar color
     * @param navDepth    NavigationBar color depth
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBar(@ColorInt int statusColor, int statusDepth, @ColorInt int navColor, int navDepth) {
        setColorBar(statusColor, statusDepth, true, navColor, navDepth);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBar(@ColorInt int statusColor, @ColorInt int navColor) {
        setColorBar(statusColor, 0, navColor, 0);
    }


    /**
     * StatusBar and NavigationBar hide
     *
     * @param applyNav apply NavigationBar
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setHideBar(boolean applyNav) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (applyNav) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(option);
        }
    }


    /**
     * StatusBar and NavigationBar color
     *
     * @param statusColor StatusBar color
     * @param statusDepth StatusBar color depth
     * @param applyNav    apply NavigationBar or no
     * @param navColor    NavigationBar color (applyNav == true)
     * @param navDepth    NavigationBar color depth (applyNav = true)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setColorBar(@ColorInt int statusColor, int statusDepth, boolean applyNav, @ColorInt int navColor, int navDepth) {
        int realStatusAlpha = limitDepthOrAlpha(statusDepth);
        int realNavDepth = limitDepthOrAlpha(navDepth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            int finalStatusColor = Color.argb(realStatusAlpha, Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor));

            window.setStatusBarColor(finalStatusColor);
            if (applyNav) {
//                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                int finalNavColor = Color.argb(realNavDepth, Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.setNavigationBarColor(finalNavColor);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            int finalStatusColor = realStatusAlpha == 0 ? statusColor : calculateColor(statusColor, realStatusAlpha);
            int finalStatusColor = Color.argb(realStatusAlpha, Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor));
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            decorView.addView(createStatusBarView(activity, finalStatusColor));
            if (applyNav && navigationBarExist(activity)) {
//                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                int finalNavColor = Color.argb(realNavDepth, Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                decorView.addView(createNavBarView(activity, finalNavColor));
            }
            setRootView(activity, true);
        }
    }


    /**
     * StatusBar and NavigationBar transparent
     *
     * @param statusColor StatusBar color
     * @param statusAlpha StatusBar alpha
     * @param applyNav    apply NavigationBar or no
     * @param navColor    NavigationBar color (applyNav == true)
     * @param navAlpha    NavigationBar alpha (applyNav == true)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTransparentBar(@ColorInt int statusColor, int statusAlpha, boolean applyNav, @ColorInt int navColor, int navAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            int finalStatusColor = statusColor == 0 ? Color.TRANSPARENT : Color.argb(limitDepthOrAlpha(statusAlpha), Color.red(statusColor), Color.green(statusColor), Color.blue
                    (statusColor));
            window.setStatusBarColor(finalStatusColor);
            if (applyNav) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                int finalNavColor = navColor == 0 ? Color.TRANSPARENT : Color.argb(limitDepthOrAlpha(navAlpha), Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                window.setNavigationBarColor(finalNavColor);
            }
            decorView.setSystemUiVisibility(option);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalStatusColor = statusColor == 0 ? Color.TRANSPARENT : Color.argb(limitDepthOrAlpha(statusAlpha), Color.red(statusColor), Color.green(statusColor), Color.blue
                    (statusColor));
            decorView.addView(createStatusBarView(activity, finalStatusColor));
            if (applyNav && navigationBarExist(activity)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                int finalNavColor = navColor == 0 ? Color.TRANSPARENT : Color.argb(limitDepthOrAlpha(navAlpha), Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                decorView.addView(createNavBarView(activity, finalNavColor));
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setColorBarForDrawer(@ColorInt int statusColor, int statusDepth, boolean applyNav, @ColorInt int navColor, int navDepth) {
        int realStatusDepth = limitDepthOrAlpha(statusDepth);
        int realNavDepth = limitDepthOrAlpha(navDepth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            int finalStatusColor = Color.argb(realStatusDepth, Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor));
            window.setStatusBarColor(finalStatusColor);
            if (applyNav) {
//                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                int finalNavColor = Color.argb(realNavDepth, Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.setNavigationBarColor(finalNavColor);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
//            int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
            int finalStatusColor = Color.argb(realStatusDepth, Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor));
            decorView.addView(createStatusBarView(activity, finalStatusColor));
            if (applyNav && navigationBarExist(activity)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//                int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
                int finalNavColor = Color.argb(realNavDepth, Color.red(navColor), Color.green(navColor), Color.blue(navColor));
                decorView.addView(createNavBarView(activity, finalNavColor));
            }
            handleFitWindowForDrawer(activity, true);
        }
    }

    private void handleFitWindowForDrawer(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof DrawerLayout) {
                childView.setFitsSystemWindows(!fit);
                ((ViewGroup) childView).setClipToPadding(!fit);

                View childViewOfDrawer = ((DrawerLayout) childView).getChildAt(0);
                if (childViewOfDrawer instanceof ViewGroup) {
                    childViewOfDrawer.setFitsSystemWindows(fit);
                    ((ViewGroup) childViewOfDrawer).setClipToPadding(fit);
                }
            }
        }
    }

    private int limitDepthOrAlpha(int depthOrAlpha) {
        if (depthOrAlpha < 0) {
            return 0;
        }
        if (depthOrAlpha > 255) {
            return 255;
        }
        return depthOrAlpha;
    }


    private View createStatusBarView(Context context, @ColorInt int color) {
        View statusBarView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ConfigUtil.getStatusBarHeight(context));
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    private View createNavBarView(Context context, @ColorInt int color) {
        View navBarView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ConfigUtil.getNavigationBarHeight(context));
        params.gravity = Gravity.BOTTOM;
        navBarView.setLayoutParams(params);
        navBarView.setBackgroundColor(color);
        return navBarView;
    }


    private boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    @ColorInt
    private int calculateColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }


    private void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup) childView).setClipToPadding(fit);
            }
        }
    }

    public static class ColorBuilder {

        @ColorInt
        private int statusColor;

        private int statusAlpha = 255;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明，默认完全不透明。

        private boolean applyNav;

        @ColorInt
        private int navColor;

        private int navAlpha = 255;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明，默认完全不透明。

        public ColorBuilder statusColor(@ColorInt int statusColor) {
            this.statusColor = statusColor;
            return this;
        }

        public ColorBuilder statusAlpha(int statusAlpha) {
            this.statusAlpha = statusAlpha;
            return this;
        }

        public ColorBuilder applyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public ColorBuilder navColor(@ColorInt int navColor) {
            this.navColor = navColor;
            return this;
        }

        public ColorBuilder navAlpha(int navAlpha) {
            this.navAlpha = navAlpha;
            return this;
        }

        public BarUtil build(Activity activity) {
            return new BarUtil(activity, this);
        }

    }

    public static class TransparentBuilder {

        @ColorInt
        private int statusColor;

        private int statusAlpha;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明。

        private boolean applyNav;

        @ColorInt
        private int navColor;

        private int navAlpha;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明。

        public TransparentBuilder statusColor(@ColorInt int statusColor) {
            this.statusColor = statusColor;
            return this;
        }

        public TransparentBuilder statusAlpha(int statusAlpha) {
            this.statusAlpha = statusAlpha;
            return this;
        }

        public TransparentBuilder applyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public TransparentBuilder navColor(@ColorInt int navColor) {
            this.navColor = navColor;
            return this;
        }

        public TransparentBuilder navAlpha(int navAlpha) {
            this.navAlpha = navAlpha;
            return this;
        }

        public BarUtil build(Activity activity) {
            return new BarUtil(activity, this);
        }

    }

    public static class ImmersionBuilder {

        private boolean applyNav;

        public ImmersionBuilder applyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public BarUtil build(Activity activity) {
            return new BarUtil(activity, this);
        }

    }

    public static class DrawerBuilder {

        @ColorInt
        private int statusColor;

        private int statusAlpha = 255;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明。

        private boolean applyNav;

        @ColorInt
        private int navColor;

        private int navAlpha = 255;//颜色的透明度值，取值0~255,0表示完全透明，255表示完全不透明。

        public DrawerBuilder statusColor(@ColorInt int statusColor) {
            this.statusColor = statusColor;
            return this;
        }

        public DrawerBuilder statusAlpha(int statusAlpha) {
            this.statusAlpha = statusAlpha;
            return this;
        }

        public DrawerBuilder applyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public DrawerBuilder navColor(@ColorInt int navColor) {
            this.navColor = navColor;
            return this;
        }

        public DrawerBuilder navAlpha(int navAlpha) {
            this.navAlpha = navAlpha;
            return this;
        }

        public BarUtil build(Activity activity) {
            return new BarUtil(activity, this);
        }

    }

    public static class HideBuilder {

        private boolean applyNav;

        public HideBuilder applyNav(boolean applyNav) {
            this.applyNav = applyNav;
            return this;
        }

        public BarUtil build(Activity activity) {
            return new BarUtil(activity, this);
        }

    }

    //设置状态栏中图标、字体的颜色模式（深色模式/亮色模式）
    //只有魅族（Flyme4+），小米（MIUI6+），android（6.0+）可以设置
    public static boolean setStatusBarLightMode(Activity activity, boolean isDark) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (setMiuiStatusBarLightMode(activity.getWindow(), isDark)) {
                result = true;
            } else if (setFlymeStatusBarLightMode(activity.getWindow(), isDark)) {
                result = true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setAndroid6StatusBarLightMode(activity.getWindow(), isDark);
                result = true;
            }
        }
        return result;
    }

    private static boolean setFlymeStatusBarLightMode(Window window, boolean isDark) {
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
                result = false;
            }
        }
        return result;
    }


    private static boolean setMiuiStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
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

                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAndroid6StatusBarLightMode(window, isDark);
                }
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }

    private static void setAndroid6StatusBarLightMode(Window window, boolean isDark) {
        if (isDark) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}
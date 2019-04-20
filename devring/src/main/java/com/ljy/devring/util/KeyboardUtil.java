package com.ljy.devring.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * author:  ljy
 * date:    2018/5/10
 * description: 输入键盘工具类
 */

public class KeyboardUtil {

    /**
     * 解决软键盘挡住输入框的问题
     * （横屏的话键盘默认会全屏显示，但这样会导致失效，所以需在EditText中加入android:imeOptions="flagNoExtractUi"）
     *
     * @param activity     页面activity
     */
    public static void fixInput(final Activity activity) {
        //一些定制的EditText（如输入内容后显示删除图标）在设置setCompoundDrawablesWithIntrinsicBounds显示图标时会反复回调onGlobalLayout
        //所以需要加此标志位，防止异常的反复的scroll
        final boolean[] isKeyboardShowed = {false};
        //页面的根视图
        final View viewRoot = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        //存放根视图当前可见区域的位置信息
        final Rect rectRootView = new Rect();
        //存放当前焦点所在EditText的位置信息
        final int[] location = new int[2];
        //屏幕高度
        int screenHeight = 0;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenHeight = ConfigUtil.getScreenShortSide(activity);
        } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenHeight = ConfigUtil.getScreenLongSide(activity);
        }
        final int finalScreenHeight = screenHeight;
        //状态栏高度
        final int statusbarHeight = ConfigUtil.getStatusBarHeight(activity);
        //导航栏高度
        final int navigationBarHeight = ConfigUtil.getNavigationBarHeight(activity);

        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                viewRoot.getWindowVisibleDisplayFrame(rectRootView);//存放根视图当前可见区域的位置信息
                View view = activity.getCurrentFocus();
                if (view != null) view.getLocationInWindow(location);//存放最下方view的位置信息
                int invisibleHeight = viewRoot.getRootView().getHeight() - rectRootView.bottom;//计算不可视区域（键盘高度），用main.getHeight()，否则部分机型会异常

                //invisibleHeight大于屏幕高度的三分之一，则视为键盘弹起了
                if (!isKeyboardShowed[0] && invisibleHeight > finalScreenHeight / 3) {
                    isKeyboardShowed[0] = true;

                    int editTextBottom = location[1];
                    if (view != null) {
                        editTextBottom = location[1] + view.getHeight();
                    }

                    //如果键盘弹起后并没有高过需要显示的最下方View，则没必要滚动布局
                    if (rectRootView.bottom < editTextBottom) {
                        //获取scroll的窗体坐标，计算出main需要滚动的高度( 额外+10会美观些 )
                        int scrollHeight = (editTextBottom + 10) - rectRootView.bottom;
                        //让界面整体上移的高度
                        viewRoot.scrollTo(0, scrollHeight); //如果viewRoot是scrollview，那么要确保srcollview可滚动的空间足够
//                        viewRoot.setTranslationY(viewRoot.getTranslationY() - scrollHeight);
                    }
                }
                //invisibleHeight小于状态栏导航栏之和，则视为键盘收起了。（主要是考虑全屏模式）
                else if (isKeyboardShowed[0] && invisibleHeight <= navigationBarHeight + statusbarHeight) {
                    isKeyboardShowed[0] = false;

                    //移回原来的位置
                    viewRoot.scrollTo(0, 0);
//                    viewRoot.setTranslationY(0);
                }
            }

        });
    }

    /**
     * （加强版）解决软键盘挡住输入框的问题
     * （横屏的话键盘默认会全屏显示，但这样会导致失效，所以需在EditText中加入android:imeOptions="flagNoExtractUi"）
     *
     * @param activity     页面activity
     * @param scrollView   如果EditText包裹在ScrollView里并且该ScrollView不是页面根布局，那么就传入该ScrollView，其他情况传null即可
     * @param isFullScreen 页面是否为全屏模式。 用于全屏模式下弹出键盘再收起后自动恢复回全屏
     */
    public static void fixInput(final Activity activity, View scrollView, final boolean isFullScreen) {
        //一些定制的EditText（如输入内容后显示删除图标）在设置setCompoundDrawablesWithIntrinsicBounds显示图标时会反复回调onGlobalLayout
        //所以需要加此标志位，防止异常的反复的scroll
        final boolean[] isKeyboardShowed = {false};
        //页面的根视图
        final View viewRoot;
        if (scrollView == null) {
            viewRoot = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        }else {
            viewRoot = scrollView;
        }
        //存放根视图当前可见区域的位置信息
        final Rect rectRootView = new Rect();
        //存放当前焦点所在EditText的位置信息
        final int[] location = new int[2];
        //屏幕高度
        int screenHeight = 0;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenHeight = ConfigUtil.getScreenShortSide(activity);
        } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenHeight = ConfigUtil.getScreenLongSide(activity);
        }
        final int finalScreenHeight = screenHeight;
        //状态栏高度
        final int statusbarHeight = ConfigUtil.getStatusBarHeight(activity);
        //导航栏高度
        final int navigationBarHeight = ConfigUtil.getNavigationBarHeight(activity);

        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                viewRoot.getWindowVisibleDisplayFrame(rectRootView);//存放根视图当前可见区域的位置信息
                View view = activity.getCurrentFocus();
                if (view != null) view.getLocationInWindow(location);//存放最下方view的位置信息
                int invisibleHeight = viewRoot.getRootView().getHeight() - rectRootView.bottom;//计算不可视区域（键盘高度），用main.getHeight()，否则部分机型会异常

                //invisibleHeight大于屏幕高度的三分之一，则视为键盘弹起了
                if (!isKeyboardShowed[0] && invisibleHeight > finalScreenHeight / 3) {
                    isKeyboardShowed[0] = true;

                    int editTextBottom = location[1];
                    if (view != null) {
                        editTextBottom = location[1] + view.getHeight();
                    }

                    //如果键盘弹起后并没有高过需要显示的最下方View，则没必要滚动布局
                    if (rectRootView.bottom < editTextBottom) {
                        //获取scroll的窗体坐标，计算出main需要滚动的高度( 额外+10会美观些 )
                        int scrollHeight = (editTextBottom + 10) - rectRootView.bottom;
                        //让界面整体上移的高度
                        viewRoot.scrollTo(0, scrollHeight); //如果viewRoot是scrollview，那么要确保srcollview可滚动的空间足够
//                        viewRoot.setTranslationY(viewRoot.getTranslationY() - scrollHeight);
                    }
                }
                //invisibleHeight小于状态栏导航栏之和，则视为键盘收起了。（主要是考虑全屏模式）
                else if (isKeyboardShowed[0] && invisibleHeight <= navigationBarHeight + statusbarHeight) {
                    isKeyboardShowed[0] = false;

                    //移回原来的位置
                    viewRoot.scrollTo(0, 0);
//                    viewRoot.setTranslationY(0);

                    //全屏模式下弹出键盘再收起，并不会自动恢复回全屏，所以重新设置
                    if (isFullScreen) {
                        //恢复全屏模式
                        BarUtil.newHideBuilder().applyNav(true).build(activity).apply();
                    }
                }
            }

        });
    }


    /**
     * 根据输入法状态打开或隐藏键盘
     *
     * @param context 上下文
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出键盘
     *
     * @param view 对于的edittext
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 收起键盘
     *
     * @param view 对于的edittext
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputmanger = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputmanger != null) {
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 根据触摸事件判断释放应该收起键盘
     *
     * @param activity    页面
     * @param event       触摸事件
     * @param viewNotHide 触摸后不需收起键盘的view
     * @return
     */
    public static boolean handleKeyboardHide(Activity activity, MotionEvent event, View... viewNotHide) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View viewCheck;
            for (int i = 0; i < viewNotHide.length; i++) {
                viewCheck = viewNotHide[i];
                if (viewCheck.isClickable() && isViewTouch(viewCheck, event)) {
                    return false;
                }
            }

            View viewFocus = activity.getCurrentFocus();
            if (viewFocus != null && (viewFocus instanceof EditText)) {
                if (isViewTouch(viewFocus, event)) {
                    return false;
                } else {
                    hideKeyboard(viewFocus);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 指定的View是否在触摸事件内
     */
    public static boolean isViewTouch(View view, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }
        return false;
    }
}

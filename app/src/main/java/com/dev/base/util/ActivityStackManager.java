package com.dev.base.util;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * date：      2015/10/26 & 12:11
 * version     1.0
 * description: Activity 栈管理器
 * modify by
 */
public class ActivityStackManager {

    private static ActivityStackManager mInstance;
    /**
     * activity栈
     */
    private Stack<Activity> mActivityStack;

    private ActivityStackManager() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static ActivityStackManager getInstance() {
        if (mInstance == null) {
            synchronized (ActivityStackManager.class) {
                if (mInstance == null) {
                    mInstance = new ActivityStackManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 把一个activity压入栈中
     *
     * @param activity
     */
    public void pushOneActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶的activity，先进后出原则
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = null;
        if ((null != mActivityStack) && !mActivityStack.empty()) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    public Activity firstActivity() {
        Activity activity = null;
        if ((null != mActivityStack) && !mActivityStack.empty()) {
            activity = mActivityStack.firstElement();
        }
        return activity;
    }

    /**
     * 移除一个activity
     *
     * @param activity
     */
    public void popOneActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if ((mActivityStack == null) || mActivityStack.isEmpty()) return;

        activity.finish();
        mActivityStack.remove(activity);
    }

    /**
     * 移除一个activity
     *
     * @param cls
     */
    public void popOneActivity(Class cls) {
        if (mActivityStack == null) {
            return;
        }
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity == null) {
                continue;
            }
            if (activity.getClass().equals(cls)) {
                popOneActivity(activity);
                break;
            }
        }
    }

    public void popOneActivityReverse(Class cls) {
        if (mActivityStack == null) {
            return;
        }
        int size = mActivityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = mActivityStack.get(i);
            if (activity == null) {
                continue;
            }
            if (activity.getClass().equals(cls)) {
                popOneActivity(activity);
                break;
            }
        }
    }

    /**
     * 退出栈中除当前外的所有Activity
     *
     * @param cls
     */
    public void popAllActivityExceptCurrent(Class cls) {
        Activity activity = null;
        while (true) {
            activity = firstActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                if (getStackSize() <= 1) {
                    break;
                } else continue;
            }
            popOneActivity(activity);
        }
    }

    /**
     * 退出应用程序
     */
    public void exitApplication() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popOneActivity(activity);
        }
    }

    /**
     * 获取应用 activity的数量
     *
     * @return
     */
    public int getStackSize() {
        if (mActivityStack != null) {
            return mActivityStack.size();
        }
        return 0;
    }
}

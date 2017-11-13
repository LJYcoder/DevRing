package com.dev.base.util;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * date：      ljy 2017.11.13
 * description: Activity 栈管理器，可用于退出单个activity、退出全部activity等
 */
public class ActivityStackManager {

    private Stack<Activity> mActivityStack;

    /**
     * 单例模式
     */
    public static ActivityStackManager getInstance() {
        return ActivityStackManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ActivityStackManager instance = new ActivityStackManager();
    }

    private ActivityStackManager() {
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
        mActivityStack.push(activity);
    }

    /**
     * 把栈顶的activity弹出
     *
     * @param activity
     */
    public void popOneActivity(Activity activity) {
        if (activity == null || CollectionUtil.isEmpty(mActivityStack)) {
            return;
        }

        mActivityStack.pop();
    }

    /**
     * 获取栈顶的activity，先进后出原则
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!CollectionUtil.isEmpty(mActivityStack)) {
            activity = mActivityStack.peek();
        }
        return activity;
    }

    /**
     * 获取栈底(第一个压住栈)的activity，先进后出原则
     *
     * @return
     */
    public Activity firstActivity() {
        Activity activity = null;
        if (!CollectionUtil.isEmpty(mActivityStack)) {
            activity = mActivityStack.firstElement();
        }
        return activity;
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

    /**
     * 退出activity
     * @param activity
     */
    public void exitActivity(Activity activity) {
        activity.finish();
        mActivityStack.remove(activity);
    }

    /**
     * 退出类型为cls的Activity
     * @param cls
     */
    public void exitActivity(Class cls) {
        if (CollectionUtil.isEmpty(mActivityStack)) {
            return;
        }

        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity == null) {
                continue;
            }
            if (activity.getClass().equals(cls)) {
                exitActivity(activity);
            }
        }
    }

    /**
     * 退出类型为cls且第一个进栈的Activity
     * @param cls
     */
    public void exitActivityByFirstIn(Class cls) {
        if (CollectionUtil.isEmpty(mActivityStack)) {
            return;
        }

        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity == null) {
                continue;
            }
            if (activity.getClass().equals(cls)) {
                exitActivity(activity);
                break;
            }
        }
    }

    /**
     * 退出类型为cls且第后一个进栈的Activity
     * @param cls
     */
    public void exitActivityByLastIn(Class cls) {
        if (CollectionUtil.isEmpty(mActivityStack)) {
            return;
        }

        int size = mActivityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = mActivityStack.get(i);
            if (activity == null) {
                continue;
            }
            if (activity.getClass().equals(cls)) {
                activity.finish();
                mActivityStack.removeElementAt(mActivityStack.lastIndexOf(activity));
                break;
            }
        }
    }

    /**
     * 退出除cls类型以外的所有Activity
     *
     * @param cls 页面activity的类型
     */
    public void exitAllActivityExceptCurrent(Class cls) {
        Activity activity;
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
            exitActivity(activity);
        }
    }

    /**
     * 退出应用程序
     */
    public void exitApplication() {
        while (true) {
            Activity activity = firstActivity();
            if (activity == null) {
                break;
            }
            exitActivity(activity);
        }
    }





}

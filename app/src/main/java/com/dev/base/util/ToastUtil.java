package com.dev.base.util;

import android.content.Context;
import android.widget.Toast;


/**
 * @author tc
 * @ClassName ToastUtil.java
 * @Description 自定义toast
 * @datetime 2015年8月20日下午4:04:50
 * modify: ljy
 */
public class ToastUtil {
    private static Toast mToast;
    private static Context mContext = null;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 2000ms
     * @param charSequence 字符串
     */
    public static void show(CharSequence charSequence) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, charSequence, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(charSequence);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
//        mToast.getView().setBackgroundColor(mContext.getResources().getColor(R.color.theme_orange));

        mToast.show();
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 3500ms
     * @param charSequence 字符串
     */
    public static void showLong(CharSequence charSequence) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, charSequence, Toast.LENGTH_LONG);
        } else {
            mToast.setText(charSequence);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
//        mToast.getView().setBackgroundColor(mContext.getResources().getColor(R.color.theme_orange));

        mToast.show();
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 2000ms
     * @param resId String资源ID
     */
    public static void show(int resId) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        mToast.show();
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 3500ms
     * @param resId String资源ID
     */
    public static void showLong(int resId) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_LONG);
        } else {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_LONG);
        }

        mToast.show();
    }

    /**
     * 取消Toast显示
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}

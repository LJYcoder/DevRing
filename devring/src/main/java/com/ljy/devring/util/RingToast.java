package com.ljy.devring.util;

import android.content.Context;
import android.widget.Toast;



/**
 * author:  ljy
 * date:    2017/3/15
 * description: 吐司工具类
 */
public class RingToast {
    private static Toast mToast;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 显示Toast,页面中重复Toast不会重复实例化Toast对象
     * 2000ms
     * @param charSequence 字符串
     */
    public static void show(CharSequence charSequence) {
        Preconditions.checkNotNull(mContext,"context不能为空，请先在application中对DevRing进行初始化");
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
        Preconditions.checkNotNull(mContext,"context不能为空，请先在application中对DevRing进行初始化");
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
        Preconditions.checkNotNull(mContext,"context不能为空，请先在application中对DevRing进行初始化");
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
        Preconditions.checkNotNull(mContext,"context不能为空，请先在application中对DevRing进行初始化");
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

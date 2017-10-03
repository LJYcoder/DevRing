package com.dev.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author：   zp
 * date：     2015/8/19 0019 17:47
 * description: 网络工具
 * modify by
 */
public class NetworkUtil {

    public static final int INVALID_NETWORK_TYPE = -1;
    public static final int MOBILE_NETWORK_TYPE = 0;
    public static final int WIF_NETWORK_TYPE = 1;

    /**
     * 判断当前网络状态是否可用
     *
     * @param context context
     * @return 可用返回true, 否则false
     */
    public static boolean isNetWorkAvailable(Context context) {

        boolean hasWifoCon = false;
        boolean hasMobileCon = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {

            String type = net.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                if (net.isConnected()) {
                    hasWifoCon = true;
//					UIToastUtil.setToast(context, context.getResources().getString(R.string.getwificonn));
                }
            }

            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
//					UIToastUtil.setToast(context, context.getResources().getString(R.string.getlocationconn));
                }
            }
        }

        return hasWifoCon || hasMobileCon;

    }

    /**
     * 获取当前网络连接类型
     *
     * @param context context
     * @return -1不可用，0移动网络，WIFI网络
     */
    public static int getNetWorkType(Context context) {
        NetworkInfo mNetworkInfo = null;
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        }
        return (mNetworkInfo != null && mNetworkInfo.isAvailable()) ? mNetworkInfo.getType() : INVALID_NETWORK_TYPE;
    }

    /**
     * 当前是否为WIFI连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 当前是否为移动网络
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }


    /**
     * 获取apn 类型
     *
     * @param context context
     * @return 类型
     */
    public static String getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }
        if (networkInfo.getExtraInfo() == null) {
            return null;
        }
        return networkInfo.getExtraInfo().toLowerCase();
    }
}

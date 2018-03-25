package com.ljy.devring.other.log;


import android.util.Log;

import com.ljy.devring.other.RingLog;

/**
 * Created by zhaokaiqiang on 15/11/18.
 */
public class BaseLog {

    public static void printDefault(int type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case RingLog.V:
                Log.v(tag, sub);
                break;
            case RingLog.D:
                Log.d(tag, sub);
                break;
            case RingLog.I:
                Log.i(tag, sub);
                break;
            case RingLog.W:
                Log.w(tag, sub);
                break;
            case RingLog.E:
                Log.e(tag, sub);
                break;
            case RingLog.A:
                Log.wtf(tag, sub);
                break;
        }
    }

}

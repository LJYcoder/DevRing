package com.dev.base.util;

import android.os.CountDownTimer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zp
 * @class name: com.tomtop.shop.helper
 * @description: 倒计时辅助类
 * @date 2015/7/28 0028 上午 10:48
 */
public class CountdownUtil {

    /**
     * 倒计时容器
     */
    private Map<String, CountDown> mCountdownMap;

    /**
     * 实例
     */
    private static CountdownUtil instance;

    private CountdownUtil() {
        mCountdownMap = new HashMap<>();
    }

    public static CountdownUtil getInstance() {
        if (instance == null) {
            instance = new CountdownUtil();
        }
        return instance;
    }

    /**
     * 构建一个新的计时器,并添加到容器里面
     *
     * @param millisInFuture
     * @param countDownInterval
     * @param impl
     * @param tag
     */
    public void newTimer(long millisInFuture, long countDownInterval, ICountDown impl, String tag) {
        cancel(tag);
        CountDown countDown = new CountDown(millisInFuture, countDownInterval, impl);
        mCountdownMap.put(tag, countDown);
        start(tag);
    }

    /**
     * @param tag
     */
    private void start(String tag) {
        mCountdownMap.get(tag).start();
    }

    public void cancel(String tag) {
        if (mCountdownMap.containsKey(tag)) {
            mCountdownMap.get(tag).cancel();
            mCountdownMap.get(tag).impl = null;
            mCountdownMap.remove(tag);
        }
    }

    public void cancelAll() {
        Iterator iterator = mCountdownMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            CountDown countDown = (CountDown) entry.getValue();
            countDown.cancel();
        }
        mCountdownMap.clear();
    }

    private class CountDown extends CountDownTimer {
        ICountDown impl;

        public CountDown(long millisInFuture, long countDownInterval, ICountDown impl) {
            super(millisInFuture, countDownInterval);
            this.impl = impl;
        }

        @Override
        public void onFinish() {
            impl.onFinish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            impl.onTick(millisUntilFinished);
        }
    }


    public interface ICountDown {

        /**
         * 倒计时过程中调用
         */
        void onTick(long millisUntilFinished);

        /**
         * 倒计时完成调用
         */
        void onFinish();
    }
}

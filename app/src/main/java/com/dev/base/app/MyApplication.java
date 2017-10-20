package com.dev.base.app;

import android.app.Application;

import com.dev.base.model.net.RetrofitUtil;
import com.dev.base.util.CrashLogUtil;
import com.dev.base.util.EventBusUtil;
import com.dev.base.util.FrescoUtil;
import com.dev.base.util.ToastUtil;
import com.dev.base.util.log.LogUtil;

/**
 * author:  ljy
 * date:    2017/9/13
 * description: 全局初始化操作
 *
 * 开发框架的各个模块用法与介绍，请到我的博客 http://blog.csdn.net/ljy_programmer 进行查看
 *
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        ToastUtil.init(this);//初始化吐司
        LogUtil.init(true);//初始化Log打印
        CrashLogUtil.getInstance().init(this);//初始化崩溃输出
        RetrofitUtil.init(this);//初始化retrofit
        FrescoUtil.getInstance().initializeFresco(this);//初始化Fresco
        EventBusUtil.openIndex();//开启Index加速
    }





}

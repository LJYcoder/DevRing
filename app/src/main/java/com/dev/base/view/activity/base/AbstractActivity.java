package com.dev.base.view.activity.base;

import android.support.v7.app.AppCompatActivity;

/**
 * date：      2017/9/13
 * version     1.0
 * description: Activity的抽象基类，这个类里面的方法适用于全部activity的需求，有特殊需求的请继承BaseActivity重写
 * modify by
 */
public abstract class AbstractActivity extends AppCompatActivity {

    /**
     * 设置布局
     */
    protected abstract void setContentLayout();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void obtainData();

    /**
     * 初始化监听
     */
    protected abstract void initEvent();
}

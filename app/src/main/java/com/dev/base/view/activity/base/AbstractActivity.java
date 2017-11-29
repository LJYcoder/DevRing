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
     * 实现setContentLayout来设置布局ID
     */
    protected abstract void setContentLayout();

    /**
     * 实现initView来做视图相关的初始化
     */
    protected abstract void initView();

    /**
     * 实现obtainData来做数据的初始化
     */
    protected abstract void obtainData();

    /**
     * 实现initEvent来做事件监听的初始化
     */
    protected abstract void initEvent();
}

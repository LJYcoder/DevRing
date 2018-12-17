package com.ljy.devring.base.activity;

import com.ljy.devring.base.fragment.FragmentLifeCallback;
import com.ljy.devring.base.fragment.IBaseFragment;
import com.ljy.devring.other.ActivityListManager;

/**
 * author:  ljy
 * date:    2018/3/19
 * description: Activity基类接口
 *
 * DevRing.init(application)初始化后，只要Activity实现该接口，将自动完成以下操作：
 * 1.根据isUseEventBus()来决定是否进行EventBus的注册/注销。
 * 2.根据isUseFragment()来决定{@link IBaseFragment}是否起作用
 * 3.进行Activity的入列、出列等操作，后面即可直接通过{@link ActivityListManager}对Activity进行相关操作
 * 4.在onPause()/onStop()/onDestroy()中发射终止信号，以便控制Retrofit网络请求在页面进入特定状态时终止。如何终止请看使用文档中的网络模块使用
 *
 * 具体实现过程可查看{@link ActivityLifeCallback}以及{@link ActivityLife}
 * <a>https://www.jianshu.com/p/3d9ee98a9570</a>
 */

public interface IBaseActivity {

    /**
     * 该Activity是否使用EventBus事件总线
     * @return true则自动进行注册/注销操作
     */
    boolean isUseEventBus();

    /**
     * 该Activity是否包含Fragment
     * @return
     * 返回false则不注册FragmentLifecycleCallbacks，也就是说{@link FragmentLifeCallback}中的操作不会进行
     */
    boolean isUseFragment();
}

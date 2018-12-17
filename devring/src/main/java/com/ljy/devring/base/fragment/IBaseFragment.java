package com.ljy.devring.base.fragment;

import android.os.Bundle;

/**
 * author:  ljy
 * date:    2018/3/19
 * description:  Fragment基类接口
 *
 * Fragment实现该接口，然后所在的Activity实现了IBaseActivity接口且isUseFragment()返回true，将自动完成以下操作：
 *
 * 1.根据isUseEventBus()来决定是否进行EventBus的注册/注销。
 * 2.数据的保存与恢复   <a>https://blog.csdn.net/donglynn/article/details/47065999</a>
 * 3.在onPause()/onStop()/onDestroy()中发射终止信号，以便控制Retrofit网络请求在页面进入特定状态时终止。如何终止请看使用文档中的网络模块使用
 *
 * 具体实现过程可查看{@link FragmentLifeCallback}以及{@link FragmentLife}
 * 具体查看 <a>https://www.jianshu.com/p/3d9ee98a9570</a>
 */

public interface IBaseFragment {

    /**
     * 需要保存数据时，将数据写进bundleToSave
     */
    void onSaveState(Bundle bundleToSave);

    /**
     * 从bundleToRestore中获取你保存进去的数据
     */
    void onRestoreState(Bundle bundleToRestore);

    /**
     * 该Fragment是否使用EventBus事件总线
     * @return true则自动进行注册/注销操作
     */
    boolean isUseEventBus();

}

package com.ljy.devring.base.fragment;

import android.os.Bundle;

/**
 * author:  ljy
 * date:    2018/3/19
 * description:
 */

public interface IBaseFragment {

    /**
     * 需要保存数据时，将数据写进bundleToSave
     */
    void onSaveState(Bundle bundleToSave);

    /**
     * 从bundleToRestore中获取你保存金曲的数据
     */
    void onRestoreState(Bundle bundleToRestore);

    /**
     * 该Fragment是否订阅事件总线
     * @return true则自动进行注册/注销操作，false则不注册
     */
    boolean isUseEventBus();

}
